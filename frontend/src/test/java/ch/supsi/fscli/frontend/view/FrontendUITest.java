package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.backend.di.BackendInjector;
import ch.supsi.fscli.frontend.MainFx;
import ch.supsi.fscli.frontend.model.FileSystemModel;
import ch.supsi.fscli.frontend.model.PreferencesModel;
import ch.supsi.fscli.frontend.util.AppError;
import javafx.application.Platform;
import javafx.scene.Node;
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

    @Override
    public void start(Stage stage) {
        // Reset singletons to ensure clean state
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
        } catch (Exception ignored) {
            // Ignore file access errors
        }

        new MainFx().start(stage);
    }

    @BeforeEach
    public void setUp() {
        WaitForAsyncUtils.waitForFxEvents();
    }

    @AfterEach
    public void tearDown() {
        try {
            FxToolkit.hideStage();
        } catch (Exception ignored) { }

        try {
            release(new KeyCode[]{});
            release(new javafx.scene.input.MouseButton[]{});
        } catch (Exception ignored) { }

        resetSingleton(BackendInjector.class, "injector");
        resetSingleton(MenuBarView.class, "instance");
        resetSingleton(CommandLineView.class, "instance");
        resetSingleton(LogAreaView.class, "instance");
        resetSingleton(PreferencesView.class, "instance");
        resetSingleton(FileSystemModel.class, "instance");
        resetSingleton(PreferencesModel.class, "instance");
    }

    // =========================================================================
    // HELPER: ROBUST FS CREATION
    // =========================================================================

    private void createFileSystemAndVerify() throws TimeoutException {
        clickOn("#fileMenu");
        clickOn("#newMenuItem");

        // Wait until the "Save" menu item is enabled.
        // This is the specific signal that the backend has finished creating the FS.
        WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS, () ->
                !MenuBarView.getInstance().getSaveMenuItem().isDisable()
        );
        WaitForAsyncUtils.waitForFxEvents();
    }

    // =========================================================================
    // FEATURE 1: COMMAND PARSING
    // =========================================================================

    @Test
    public void testNoFileSystemPresentCommandInput() {
        clickOn("#commandInput");
        write("ls");
        clickOn("#enter");

        TextArea log = lookup("#logView").query();
        String logText = log.getText();
        assertTrue(logText.contains("501") || logText.contains("FSNotReady"),
                "Log should show FS not initialized error.");
    }

    @Test
    public void testFileSystemPresentCommandInput() throws TimeoutException {
        createFileSystemAndVerify(); // Uses robust wait

        clickOn("#commandInput");
        write("ls");
        clickOn("#enter");

        TextArea log = lookup("#logView").query();
        String logText = log.getText();
        assertFalse(logText.contains(String.valueOf(AppError.CMD_EXECUTION_FAILED_FS_MISSING.getErrorCode())),
                "Log should not show 501 error.");
    }

    @Test
    public void testCommandOutputScrolling() throws TimeoutException {
        createFileSystemAndVerify();

        // Generate overflow
        for (int i = 0; i < 25; i++) {
            clickOn("#commandInput");
            write("pwd");
            clickOn("#enter");
        }
        WaitForAsyncUtils.waitForFxEvents();

        Node outputNode = lookup("#outputView").query();
        // Look up scroll pane inside the specific output view node
        ScrollPane scrollPane = from(outputNode).lookup(".scroll-pane").query();

        // Wait for VMax to update (Layout pass)
        WaitForAsyncUtils.waitFor(3, TimeUnit.SECONDS, () -> scrollPane.getVmax() > 0);
        assertTrue(scrollPane.getVmax() > 0, "Output should be scrollable");

        // Reset to top
        interact(() -> scrollPane.setVvalue(0.0));
        WaitForAsyncUtils.waitForFxEvents();
        // Wait for value to settle (in case of auto-scroll fighting)
        WaitForAsyncUtils.waitFor(2, TimeUnit.SECONDS, () -> scrollPane.getVvalue() == 0.0);
        assertEquals(0.0, scrollPane.getVvalue(), 0.01);

        // Scroll to bottom
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

        clickOn("#commandInput");
        write("touch persistence_check.txt");
        clickOn("#enter");

        File tempFile = File.createTempFile("fs_test_persistence", ".json");
        tempFile.deleteOnExit();
        injectFileIntoModel(tempFile);

        clickOn("#fileMenu");
        clickOn("#saveMenuItem");
        WaitForAsyncUtils.waitForFxEvents();

        // Clear state (New FS)
        createFileSystemAndVerify();

        // Load
        Platform.runLater(() -> FileSystemModel.getInstance().load(tempFile));
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#commandInput");
        write("ls");
        clickOn("#enter");

        TextArea output = lookup("#outputView").query();
        assertTrue(output.getText().contains("persistence_check.txt"),
                "Loaded FileSystem should contain the file we saved.");
    }

    @Test
    public void testSaveAsAndLoadWithContent() throws Exception {
        createFileSystemAndVerify();

        clickOn("#commandInput");
        write("touch saveas_data.txt");
        clickOn("#enter");

        File tempFile = File.createTempFile("fs_saveas_test", ".json");
        tempFile.deleteOnExit();

        Platform.runLater(() -> MenuBarView.getInstance().getFileSystemEventHandler().saveAs(tempFile));
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(Files.exists(tempFile.toPath()));
        String content = Files.readString(tempFile.toPath());
        assertTrue(content.contains("saveas_data.txt"), "Saved file on disk should contain data");

        // Reset
        createFileSystemAndVerify();

        // Load
        Platform.runLater(() -> FileSystemModel.getInstance().load(tempFile));
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#commandInput");
        write("ls");
        clickOn("#enter");

        TextArea output = lookup("#outputView").query();
        assertTrue(output.getText().contains("saveas_data.txt"),
                "Loaded FileSystem should contain the file we saved via Save As.");
    }

    @Test
    public void testSaveDisabledWhenNoFileSystem() {
        clickOn("#fileMenu");
        WaitForAsyncUtils.waitForFxEvents();

        MenuBarView view = MenuBarView.getInstance();
        assertTrue(view.getSaveMenuItem().isDisable());
        assertTrue(view.getSaveAsMenuItem().isDisable());

        push(KeyCode.ESCAPE);
    }

    @Test
    public void testSaveAsEnabledAfterFS() throws TimeoutException {
        assertTrue(MenuBarView.getInstance().getSaveMenuItem().isDisable());

        createFileSystemAndVerify(); // This handles the check that it actually enables

        assertFalse(MenuBarView.getInstance().getSaveMenuItem().isDisable());
        assertFalse(MenuBarView.getInstance().getSaveAsMenuItem().isDisable());
    }

    // =========================================================================
    // FEATURE 3: EXIT & NEW WITH UNSAVED CHANGES
    // =========================================================================

    @Test
    public void testExitWithUnsavedChanges() throws TimeoutException {
        createFileSystemAndVerify();

        clickOn(".text-field");
        write("touch keep.txt");
        clickOn("#enter");

        clickOn("#fileMenu");
        clickOn("#exitMenuItem");

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat(".alert", isVisible());

        push(KeyCode.RIGHT);
        push(KeyCode.ENTER);

        verifyThat("#outputView", isVisible());
    }

    @Test
    public void testNewWithUnsavedChanges() throws TimeoutException {
        createFileSystemAndVerify();

        clickOn(".text-field");
        write("mkdir dirty2");
        clickOn("#enter");

        clickOn("#fileMenu");
        clickOn("#newMenuItem");

        WaitForAsyncUtils.waitForFxEvents();
        verifyThat(".alert", isVisible());

        push(KeyCode.RIGHT);
        push(KeyCode.ENTER);
    }

    // =========================================================================
    // FEATURE 5: PREFERENCES (Fixed Lookups)
    // =========================================================================

    private List<javafx.scene.control.TextField> getPreferencesFields() throws TimeoutException {
        // 1. Wait for the Preferences window to appear
        // The lambda must return Boolean (true = found, false = keep waiting)
        WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS, () -> {
            for (Window w : listTargetWindows()) {
                if (w instanceof Stage && "Preferences".equals(((Stage) w).getTitle())) {
                    return true;
                }
            }
            return false;
        });

        // 2. Retrieve the window object
        Window prefWindow = listTargetWindows().stream()
                .filter(w -> w instanceof Stage && "Preferences".equals(((Stage) w).getTitle()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Preferences window not found after waiting"));

        // 3. Scoped Lookup using 'from()'
        // We find the root of the window's scene and search inside it.
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
        clickOn("#editMenu");
        clickOn("#preferencesMenuItem");

        List<TextField> prefFields = getPreferencesFields();
        assertFalse(prefFields.isEmpty(), "Should find preference fields in the Preferences window");

        // Index 0: Cmd Cols, 1: Output Lines, 2: Log Lines
        TextField cmdColsField = prefFields.get(0);
        TextField outputLinesField = prefFields.get(1);
        TextField logLinesField = prefFields.get(2);

        doubleClickOn(cmdColsField); write("90");
        doubleClickOn(outputLinesField); write("25");
        doubleClickOn(logLinesField); write("15");

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
        clickOn("#editMenu");
        clickOn("#preferencesMenuItem");

        List<TextField> prefFields = getPreferencesFields();
        assertFalse(prefFields.isEmpty(), "Should find preference fields");

        TextField cmdColsField = prefFields.get(0);
        String initialValue = cmdColsField.getText();

        doubleClickOn(cmdColsField);
        write("55");

        assertEquals("55", cmdColsField.getText());

        clickOn("Reload from disk");
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(initialValue, cmdColsField.getText());
    }

    // =========================================================================
    // FEATURE 6: ABOUT & HELP
    // =========================================================================

    @Test
    public void testAboutWindow() {
        clickOn("#helpMenu");
        clickOn("#aboutMenuItem");
        WaitForAsyncUtils.waitForFxEvents();

        verifyThat((Node) lookup(".button").nth(0).query(), isVisible());
        push(KeyCode.ESCAPE);
    }

    @Test
    public void testHelpWindow() throws TimeoutException {
        clickOn("#helpMenu");
        clickOn("#helpMenuItem");
        WaitForAsyncUtils.waitForFxEvents();

        // 1. Wait for Help Window (Boolean condition)
        WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS, () -> {
            for (Window w : listTargetWindows()) {
                if (w instanceof Stage && "Help".equals(((Stage) w).getTitle())) {
                    return true;
                }
            }
            return false;
        });

        // 2. Retrieve Help Window
        Window helpWindow = listTargetWindows().stream()
                .filter(w -> w instanceof Stage && "Help".equals(((Stage) w).getTitle()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Help window should be found"));

        assertNotNull(helpWindow, "Help window should be found");

        // Find ScrollPane strictly within that window
        ScrollPane scrollPane = from(helpWindow.getScene().getRoot()).lookup(".scroll-pane").query();
        assertNotNull(scrollPane);

        assertEquals(0.0, scrollPane.getVvalue(), 0.01);

        // Programmatic Scroll Check
        if (scrollPane.getVmax() > 0) {
            interact(() -> scrollPane.setVvalue(0.5));
            WaitForAsyncUtils.waitForFxEvents();
            assertTrue(scrollPane.getVvalue() > 0.0);

            interact(() -> scrollPane.setVvalue(1.0));
            WaitForAsyncUtils.waitForFxEvents();
            assertEquals(scrollPane.getVmax(), scrollPane.getVvalue(), 0.01);
        }

        // Close button lookup within the help window
        Node closeBtn = from(helpWindow.getScene().getRoot()).lookup(".button").query();
        clickOn(closeBtn);
    }

    // =========================================================================
    // HELPERS
    // =========================================================================

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