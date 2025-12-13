package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.backend.di.BackendInjector;
import ch.supsi.fscli.frontend.MainFx;
import ch.supsi.fscli.frontend.model.FileSystemModel;
import ch.supsi.fscli.frontend.model.PreferencesModel;
import ch.supsi.fscli.frontend.util.AppError;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class FrontendUITest extends ApplicationTest {
    private static final int TIMEOUT = 30;

    @Override
    public void start(Stage stage) {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("testMode", "true");
        
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            if (throwable instanceof IllegalStateException && 
                throwable.getMessage() != null && 
                throwable.getMessage().contains("Not on FX application thread")) {
                // Suppress known JavaFX threading quirk during window cleanup
                return;
            }
            throwable.printStackTrace();
        });

        resetSingleton(BackendInjector.class, "injector");
        resetSingleton(MenuBarView.class, "instance");
        resetSingleton(CommandLineView.class, "instance");
        resetSingleton(LogAreaView.class, "instance");
        resetSingleton(PreferencesView.class, "instance");
        resetSingleton(FileSystemModel.class, "instance");
        resetSingleton(PreferencesModel.class, "instance");

        try {
            String userHome = System.getProperty("user.home");
            Path prefsPath = Paths.get(userHome, ".fs_prefs.json");
            Files.deleteIfExists(prefsPath);
        } catch (Exception ignored) { }

        new MainFx().start(stage);
    }

    @BeforeEach
    public void setUp() {
        WaitForAsyncUtils.waitForFxEvents();
    }

    @AfterEach
    public void tearDown() {
        try { release(new KeyCode[]{}); release(new javafx.scene.input.MouseButton[]{}); } catch (Exception ignored) { }
        try { FxToolkit.hideStage(); } catch (Exception ignored) { }
        WaitForAsyncUtils.waitForFxEvents();

        resetSingleton(BackendInjector.class, "injector");
        resetSingleton(MenuBarView.class, "instance");
        resetSingleton(CommandLineView.class, "instance");
        resetSingleton(LogAreaView.class, "instance");
        resetSingleton(PreferencesView.class, "instance");
        resetSingleton(FileSystemModel.class, "instance");
        resetSingleton(PreferencesModel.class, "instance");
    }

    // =========================================================================
    // ROBUST HELPERS
    // =========================================================================

    private void fireMenuItem(String id) {
        Platform.runLater(() -> MenuBarView.getInstance().getMenuBar().getMenus().stream()
                .flatMap(m -> m.getItems().stream())
                .filter(item -> id.equals(item.getId()))
                .findFirst()
                .ifPresent(MenuItem::fire));
    }

    private void fastType(String text) {
        interact(() -> {
            TextField field = lookup("#commandInput").query();
            field.setText(text);
            field.positionCaret(text.length());
        });
    }

    private void createFileSystemAndVerify() throws TimeoutException {
        fireMenuItem("newMenuItem");

        WaitForAsyncUtils.waitFor(TIMEOUT, TimeUnit.SECONDS, () ->
                !MenuBarView.getInstance().getSaveMenuItem().isDisable()
        );
    }

    // =========================================================================
    // FEATURE 1: COMMAND PARSING
    // =========================================================================

    @Test
    public void testNoFileSystemPresentCommandInput() {
        fastType("ls");
        clickOn("#enter");

        TextArea log = lookup("#logView").query();
        String logText = log.getText();
        assertTrue(logText.contains("501") || logText.contains("FSNotReady"),
                "Log should show FS not initialized error.");
    }

    @Test
    public void testFileSystemPresentCommandInput() throws TimeoutException {
        createFileSystemAndVerify();

        fastType("ls");
        clickOn("#enter");

        TextArea log = lookup("#logView").query();
        assertFalse(log.getText().contains(String.valueOf(AppError.CMD_EXECUTION_FAILED_FS_MISSING.getErrorCode())),
                "Log should not show 501 error.");
    }

    @Test
    public void testCommandOutputScrolling() throws TimeoutException {
        createFileSystemAndVerify();

        interact(() -> {
            TextField cmd = CommandLineView.getInstance().getCommandLine();
            javafx.scene.control.Button enter = CommandLineView.getInstance().getEnter();
            for (int i = 0; i < 25; i++) {
                cmd.setText("pwd");
                enter.fire();
            }
        });

        Node outputNode = lookup("#outputView").query();
        ScrollPane scrollPane = from(outputNode).lookup(".scroll-pane").query();

        // Wait for layout update
        WaitForAsyncUtils.waitFor(TIMEOUT, TimeUnit.SECONDS, () -> scrollPane.getVmax() > 0);
        assertTrue(scrollPane.getVmax() > 0, "Output should be scrollable");

        interact(() -> scrollPane.setVvalue(0.0));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(0.0, scrollPane.getVvalue(), 0.01);

        interact(() -> scrollPane.setVvalue(1.0));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(1.0, scrollPane.getVvalue(), 0.01);
    }

    // =========================================================================
    // FEATURE 2: SAVE SCENARIOS
    // =========================================================================

    @Test
    public void testSaveAndLoadWithContent() throws Exception {
        createFileSystemAndVerify();

        fastType("touch persistence_check.txt");
        clickOn("#enter");

        File tempFile = File.createTempFile("fs_test_persistence", ".json");
        tempFile.deleteOnExit();
        injectFileIntoModel(tempFile);

        // Safe interaction for simple actions
        interact(() -> MenuBarView.getInstance().getFileSystemEventHandler().save());
        WaitForAsyncUtils.waitForFxEvents();

        createFileSystemAndVerify();

        Platform.runLater(() -> FileSystemModel.getInstance().load(tempFile));
        WaitForAsyncUtils.waitForFxEvents();

        fastType("ls");
        clickOn("#enter");

        TextArea output = lookup("#outputView").query();
        assertTrue(output.getText().contains("persistence_check.txt"),
                "Loaded FileSystem should contain the file we saved.");
    }

    @Test
    public void testSaveAsAndLoadWithContent() throws Exception {
        createFileSystemAndVerify();

        fastType("touch saveas_data.txt");
        clickOn("#enter");

        File tempFile = File.createTempFile("fs_saveas_test", ".json");
        tempFile.deleteOnExit();

        Platform.runLater(() -> MenuBarView.getInstance().getFileSystemEventHandler().saveAs(tempFile));
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(Files.exists(tempFile.toPath()));
        assertTrue(Files.readString(tempFile.toPath()).contains("saveas_data.txt"));

        createFileSystemAndVerify();

        Platform.runLater(() -> FileSystemModel.getInstance().load(tempFile));
        WaitForAsyncUtils.waitForFxEvents();

        fastType("ls");
        clickOn("#enter");

        TextArea output = lookup("#outputView").query();
        assertTrue(output.getText().contains("saveas_data.txt"),
                "Loaded FileSystem should contain the file we saved via Save As.");
    }

    @Test
    public void testSaveDisabledWhenNoFileSystem() {
        assertTrue(MenuBarView.getInstance().getSaveMenuItem().isDisable());
        assertTrue(MenuBarView.getInstance().getSaveAsMenuItem().isDisable());
    }

    @Test
    public void testSaveAsEnabledAfterFS() throws TimeoutException {
        assertTrue(MenuBarView.getInstance().getSaveMenuItem().isDisable());
        createFileSystemAndVerify();
        assertFalse(MenuBarView.getInstance().getSaveMenuItem().isDisable());
        assertFalse(MenuBarView.getInstance().getSaveAsMenuItem().isDisable());
    }

    // =========================================================================
    // FEATURE 3: EXIT & NEW WITH UNSAVED CHANGES
    // =========================================================================

    @Test
    public void testExitWithUnsavedChanges() throws TimeoutException {
        createFileSystemAndVerify();

        // Make dirty
        interact(() -> {
            CommandLineView.getInstance().getCommandLine().setText("touch keep.txt");
            CommandLineView.getInstance().getEnter().fire();
        });

        // Trigger exit (Opens Modal)
        fireMenuItem("exitMenuItem");

        // Explicitly wait for the alert to appear (handles the async nature of runLater)
        WaitForAsyncUtils.waitFor(TIMEOUT, TimeUnit.SECONDS, () -> lookup(".alert").tryQuery().isPresent());
        verifyThat(".alert", isVisible());

        push(KeyCode.RIGHT);
        push(KeyCode.ENTER);

        verifyThat("#outputView", isVisible());
    }

    @Test
    public void testNewWithUnsavedChanges() throws TimeoutException {
        createFileSystemAndVerify();

        interact(() -> {
            CommandLineView.getInstance().getCommandLine().setText("mkdir dirty2");
            CommandLineView.getInstance().getEnter().fire();
        });

        fireMenuItem("newMenuItem");

        WaitForAsyncUtils.waitFor(TIMEOUT, TimeUnit.SECONDS, () -> lookup(".alert").tryQuery().isPresent());
        verifyThat(".alert", isVisible());

        push(KeyCode.RIGHT);
        push(KeyCode.ENTER);
    }

    // =========================================================================
    // FEATURE 5: PREFERENCES
    // =========================================================================

    private List<javafx.scene.control.TextField> getPreferencesFields() throws TimeoutException {
        WaitForAsyncUtils.waitFor(TIMEOUT, TimeUnit.SECONDS, () -> {
            for (Window w : listTargetWindows()) {
                if (w instanceof Stage && "Preferences".equals(((Stage) w).getTitle())) return true;
            }
            return false;
        });

        Window prefWindow = listTargetWindows().stream()
                .filter(w -> w instanceof Stage && "Preferences".equals(((Stage) w).getTitle()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Preferences window not found"));

        return from(prefWindow.getScene().getRoot())
                .lookup(".text-field")
                .queryAll()
                .stream()
                .filter(node -> node instanceof javafx.scene.control.TextField)
                .map(node -> (javafx.scene.control.TextField) node)
                .collect(Collectors.toList());
    }

    @Test
    public void testChangePreferencesAndLoadOnRestart() throws TimeoutException {
        fireMenuItem("preferencesMenuItem");

        List<TextField> prefFields = getPreferencesFields();

        interact(() -> {
            prefFields.get(0).setText("90");
            prefFields.get(1).setText("25");
            prefFields.get(2).setText("15");
        });

        verifyThat("Save", (Node n) -> !n.isDisabled());
        clickOn("Save");
        WaitForAsyncUtils.waitForFxEvents();

        resetSingleton(PreferencesModel.class, "instance");

        PreferencesModel reloadedModel = PreferencesModel.getInstance();
        java.util.Map<String, String> prefs = reloadedModel.getCurrentPreferences();

        assertEquals("90", prefs.get("cmdColumns"));
        assertEquals("25", prefs.get("outputLines"));
        assertEquals("15", prefs.get("logLines"));
    }

    @Test
    public void testReloadPreferencesRevertsChanges() throws TimeoutException {
        fireMenuItem("preferencesMenuItem");

        List<TextField> prefFields = getPreferencesFields();
        TextField cmdColsField = prefFields.get(0);
        String initialValue = cmdColsField.getText();

        interact(() -> cmdColsField.setText("55"));
        assertEquals("55", cmdColsField.getText());

        clickOn("Reload from disk");
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(initialValue, cmdColsField.getText());
    }

    // =========================================================================
    // FEATURE 6: ABOUT & HELP
    // =========================================================================

    @Test
    public void testAboutWindow() throws TimeoutException {
        fireMenuItem("aboutMenuItem");

        // Wait for modal
        WaitForAsyncUtils.waitFor(TIMEOUT, TimeUnit.SECONDS, () -> lookup(".button").tryQuery().isPresent());

        verifyThat((Node) lookup(".button").nth(0).query(), isVisible());
        push(KeyCode.ESCAPE);
    }

    @Test
    public void testHelpWindow() throws TimeoutException {
        fireMenuItem("helpMenuItem");

        WaitForAsyncUtils.waitFor(TIMEOUT, TimeUnit.SECONDS, () -> {
            for (Window w : listTargetWindows()) {
                if (w instanceof Stage && "Help".equals(((Stage) w).getTitle())) return true;
            }
            return false;
        });

        Window helpWindow = listTargetWindows().stream()
                .filter(w -> w instanceof Stage && "Help".equals(((Stage) w).getTitle()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Help window should be found"));

        ScrollPane scrollPane = from(helpWindow.getScene().getRoot()).lookup(".scroll-pane").query();
        assertNotNull(scrollPane);

        if (scrollPane.getVmax() > 0) {
            interact(() -> scrollPane.setVvalue(0.5));
            WaitForAsyncUtils.waitForFxEvents();
            assertTrue(scrollPane.getVvalue() > 0.0);

            interact(() -> scrollPane.setVvalue(1.0));
            WaitForAsyncUtils.waitForFxEvents();
            assertEquals(scrollPane.getVmax(), scrollPane.getVvalue(), 0.01);
        }

        Node closeBtn = from(helpWindow.getScene().getRoot()).lookup(".button").query();
        clickOn(closeBtn);
    }

    private void resetSingleton(Class<?> clazz, String fieldName) {
        try {
            Field instance = clazz.getDeclaredField(fieldName);
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (Exception ignored) { }
    }

    private void injectFileIntoModel(File file) throws Exception {
        FileSystemModel model = FileSystemModel.getInstance();
        Field fileField = FileSystemModel.class.getDeclaredField("file");
        fileField.setAccessible(true);
        fileField.set(model, file);
    }
}