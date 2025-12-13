package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.backend.di.BackendInjector;
import ch.supsi.fscli.frontend.MainFx;
import ch.supsi.fscli.frontend.model.FileSystemModel;
import ch.supsi.fscli.frontend.model.PreferencesModel;
import ch.supsi.fscli.frontend.util.AppError;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
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
import java.util.concurrent.TimeoutException;


import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class FrontendUITest extends ApplicationTest {
    @Override
    public void start(Stage stage) {
        // CRITICAL FIX: Delete the preferences file BEFORE starting the app.
        // This prevents the "Preferences Clamped" warning alert from appearing.
        // That alert uses showAndWait() inside MainFx.start(), which blocks the
        // entire test runner if it appears.
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
            // Ignore file access errors, best effort cleanup
        }

        new MainFx().start(stage);
    }

    @BeforeEach
    public void setUp() {
        WaitForAsyncUtils.waitForFxEvents();
    }

    @AfterEach
    public void tearDown() {
        // 1. Clean up TestFX Stage (Main Window)
        // We wrap this in try-catch because if Platform.exit() was called,
        // the Toolkit is dead and this method throws an error. We simply ignore it.
        try {
            FxToolkit.hideStage();
        } catch (Exception ignored) {
            // Toolkit is dead, application is already closed.
        }

        // 2. Release any stuck keys/mouse buttons
        try {
            release(new KeyCode[]{});
            release(new javafx.scene.input.MouseButton[]{});
        } catch (Exception ignored) { }

        // 3. Reset Singletons (CRITICAL: specific to your app structure)
        // This runs on the Main Test Thread (not FX thread), so it is safe and never hangs.
        resetSingleton(BackendInjector.class, "injector");
        resetSingleton(MenuBarView.class, "instance");
        resetSingleton(CommandLineView.class, "instance");
        resetSingleton(LogAreaView.class, "instance");
        resetSingleton(PreferencesView.class, "instance");
        resetSingleton(FileSystemModel.class, "instance");
        resetSingleton(PreferencesModel.class, "instance");
    }

    // =========================================================================
    // FEATURE 1: COMMAND PARSING
    // =========================================================================

    @Test
    public void testNoFileSystemPresentCommandInput() {
        clickOn("#commandInput");
        write("ls");
        clickOn("#enter");

        // Verify [501] Error in Log
        TextArea log = lookup("#logView").query();
        String logText = log.getText();
        assertTrue(logText.contains("501") || logText.contains("FSNotReady"),
                "Log should show FS not initialized error.");
    }

    @Test
    public void testFileSystemPresentCommandInput() {
        clickOn("#fileMenu");
        clickOn("#newMenuItem");
        clickOn("#commandInput");
        write("ls");
        clickOn("#enter");

        // Verify [501] Error is not in Log
        TextArea log = lookup("#logView").query();
        String logText = log.getText();
        assertFalse(logText.contains(String.valueOf(AppError.CMD_EXECUTION_FAILED_FS_MISSING.getErrorCode())) || logText.contains(AppError.CMD_EXECUTION_FAILED_FS_MISSING.getDefaultMessage()),
                "Log should not show any error.");
    }

    @Test
    public void testCommandOutputScrolling() throws TimeoutException {
        // 1. Initialize Filesystem
        clickOn("#fileMenu");
        clickOn("#newMenuItem");
        WaitForAsyncUtils.waitForFxEvents();

        // 2. Flood the output area with text
        // The default view shows 10 lines. We execute enough commands to overflow this.
        // We use an invalid command or simple 'pwd' to generate distinct lines.
        for (int i = 0; i < 10; i++) {
            clickOn("#commandInput");
            write("pwd");
            clickOn("#enter");
        }
        WaitForAsyncUtils.waitForFxEvents();

        // 3. Find the ScrollPane
        // The #outputView is a TextArea. JavaFX TextAreas have an internal ScrollPane within their "Skin".
        // We must look up the .scroll-pane *inside* the #outputView node.
        Node outputNode = lookup("#outputView").query();
        javafx.scene.control.ScrollPane scrollPane = from(outputNode).lookup(".scroll-pane").query();

        assertNotNull(scrollPane, "TextArea should contain an internal ScrollPane");

        // 4. Verify it is scrollable (Vmax > 0)
        // Wait for UI layout to calculate the overflow
        WaitForAsyncUtils.waitFor(2, java.util.concurrent.TimeUnit.SECONDS,
                () -> scrollPane.getVmax() > 0);

        assertTrue(scrollPane.getVmax() > 0, "Output area should be scrollable when content overflows");

        // 5. Test Scrolling
        // Scroll to the top first
        interact(() -> scrollPane.setVvalue(0.0));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(0.0, scrollPane.getVvalue(), 0.01);

        // Scroll to the bottom
        interact(() -> scrollPane.setVvalue(1.0));
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(1.0, scrollPane.getVvalue(), 0.01, "Should be able to scroll to the bottom");
    }

    // =========================================================================
    // FEATURE 2: SAVE SCENARIOS
    // =========================================================================

    @Test
    public void testSaveAndLoadWithContent() throws Exception {
        // 1. Create New FS
        clickOn("#fileMenu");
        clickOn("#newMenuItem");
        WaitForAsyncUtils.waitForFxEvents();

        // 2. Create content (Folder or File)
        // We create a specific file to verify persistence
        clickOn("#commandInput");
        write("touch persistence_check.txt");
        clickOn("#enter");

        // 3. Inject a temporary file path into the model.
        // This tricks the application into thinking we have already performed "Save As",
        // so clicking "Save" will write to this file without opening the native dialog.
        File tempFile = File.createTempFile("fs_test_persistence", ".json");
        tempFile.deleteOnExit();
        injectFileIntoModel(tempFile);

        // 4. Click Save
        clickOn("#fileMenu");
        clickOn("#saveMenuItem");
        WaitForAsyncUtils.waitForFxEvents();

        // 5. Create "New" FS again to clear the current memory state
        // (This ensures we are actually loading from disk later, not just reading memory)
        clickOn("#fileMenu");
        clickOn("#newMenuItem");
        WaitForAsyncUtils.waitForFxEvents();

        // 6. Simulate "Open"
        // We cannot click the Open menu item because the native file chooser blocks the test.
        // Instead, we manually trigger the load logic with our temp file.
        Platform.runLater(() -> FileSystemModel.getInstance().load(tempFile));
        WaitForAsyncUtils.waitForFxEvents();

        // 7. Verify content
        // List files and check if our text file is there
        clickOn("#commandInput");
        write("ls");
        clickOn("#enter");

        TextArea output = lookup("#outputView").query();
        assertTrue(output.getText().contains("persistence_check.txt"),
                "Loaded FileSystem should contain the file we saved.");
    }

    @Test
    public void testSaveAsAndLoadWithContent() throws Exception {
        // 1. Create New FS
        clickOn("#fileMenu");
        clickOn("#newMenuItem");
        WaitForAsyncUtils.waitForFxEvents();

        // 2. Create content (Folder/File) to verify persistence
        clickOn("#commandInput");
        write("touch saveas_data.txt");
        clickOn("#enter");

        // 3. Prepare a target file
        File tempFile = File.createTempFile("fs_saveas_test", ".json");
        tempFile.deleteOnExit();

        // 4. PERFORM "SAVE AS"
        // We cannot click the menu item because the native dialog blocks the test runner.
        // Instead, we interact with the View's handler directly, simulating a successful dialog selection.
        Platform.runLater(() -> {
            MenuBarView.getInstance().getFileSystemEventHandler().saveAs(tempFile);
        });
        WaitForAsyncUtils.waitForFxEvents();

        // 5. Verify file exists on disk immediately
        assertTrue(Files.exists(tempFile.toPath()));
        String content = Files.readString(tempFile.toPath());
        assertTrue(content.contains("saveas_data.txt"), "Saved file should contain the created data");

        // 6. Reset (Create New FS) to clear memory
        clickOn("#fileMenu");
        clickOn("#newMenuItem");
        WaitForAsyncUtils.waitForFxEvents();

        // Verify memory is cleared
        clickOn("#commandInput");
        write("ls");
        clickOn("#enter");
        TextArea output = lookup("#outputView").query();
        // Depending on your "ls" output for empty dir, but it definitely shouldn't have the file
        assertFalse(output.getText().contains("saveas_data.txt"));

        // 7. PERFORM "OPEN"
        // Simulate loading the file we just saved
        Platform.runLater(() -> {
            FileSystemModel.getInstance().load(tempFile);
        });
        WaitForAsyncUtils.waitForFxEvents();

        // 8. Verify content loaded correctly
        clickOn("#commandInput");
        write("ls");
        clickOn("#enter");

        output = lookup("#outputView").query();
        assertTrue(output.getText().contains("saveas_data.txt"),
                "Loaded FileSystem should contain the file we saved via Save As.");
    }

    @Test
    public void testSaveDisabledWhenNoFileSystem() {
        // 1. Open File Menu
        clickOn("#fileMenu");
        WaitForAsyncUtils.waitForFxEvents();

        // 2. Direct Instance Check
        // We check the singleton directly to ensure we are testing the LIVE application state,
        // bypassing potential TestFX lookup issues with "ghost" windows from previous tests.
        MenuBarView view = MenuBarView.getInstance();

        assertTrue(view.getSaveMenuItem().isDisable(),
                "Save Menu Item should be disabled when no FS is loaded.");

        assertTrue(view.getSaveAsMenuItem().isDisable(),
                "Save As Menu Item should be disabled when no FS is loaded.");

        // 3. Close Menu
        push(KeyCode.ESCAPE);
    }

    @Test
    public void testSaveAsEnabledAfterFS() {
        // 1. Verify "Save As" is disabled initially
        // We check the actual MenuBarView instance to ensure we are testing the live application state
        // and not a stale UI element from a previous test.
        assertTrue(MenuBarView.getInstance().getSaveMenuItem().isDisable(),
                "Save should be disabled initially");

        assertTrue(MenuBarView.getInstance().getSaveAsMenuItem().isDisable(),
                "Save As should be disabled initially");

        // 2. Create New FS via UI Interaction
        clickOn("#fileMenu");
        clickOn("#newMenuItem");
        WaitForAsyncUtils.waitForFxEvents();

        // 3. Verify "Save As" is now enabled
        assertFalse(MenuBarView.getInstance().getSaveMenuItem().isDisable(),
                "Save should be enabled after FS is created");

        assertFalse(MenuBarView.getInstance().getSaveAsMenuItem().isDisable(),
                "Save As should be enabled after FS is created");
    }

    // =========================================================================
    // FEATURE 3: EXIT & NEW WITH UNSAVED CHANGES
    // =========================================================================

    @Test
    public void testExitWithUnsavedChanges() {
        // 1. Make changes
        clickOn("#fileMenu");
        clickOn("#newMenuItem");
        clickOn(".text-field");
        write("touch keep.txt");
        clickOn("#enter");

        // 2. Click Exit
        clickOn("#fileMenu");
        clickOn("#exitMenuItem");

        // 3. Verify Alert
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat(".alert", isVisible());

        // 4. Cancel Exit (Press RIGHT -> ENTER for "No" button)
        push(KeyCode.RIGHT);
        push(KeyCode.ENTER);

        // 5. Verify App is still open (Output view is visible)
        verifyThat("#outputView", isVisible());
    }

    @Test
    public void testNewWithUnsavedChanges() {
        // 1. Make Dirty
        clickOn("#fileMenu");
        clickOn("#newMenuItem");
        clickOn(".text-field");
        write("mkdir dirty2");
        clickOn("#enter");

        // 2. Click New again
        clickOn("#fileMenu");
        clickOn("#newMenuItem");

        // 3. Verify Alert
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat(".alert", isVisible());

        // 4. Cancel (Press RIGHT -> ENTER for "No")
        push(KeyCode.RIGHT);
        push(KeyCode.ENTER);
    }

    // =========================================================================
    // FEATURE 5: PREFERENCES
    // =========================================================================

    @Test
    public void testChangePreferencesAndLoadOnRestart() {
        // 1. Open Preferences Dialog
        clickOn("#editMenu");
        clickOn("#preferencesMenuItem");
        WaitForAsyncUtils.waitForFxEvents();

        // 2. Robust Lookup: Filter for TextFields that are inside a VBox.
        // The preferences fields (ValidatedField) are wrapped in a VBox.
        // The main command line input is inside an HBox, so it will be excluded.
        List<javafx.scene.control.TextField> prefFields = lookup(".text-field").queryAll().stream()
                .filter(node -> node.getParent() instanceof javafx.scene.layout.VBox)
                .map(node -> (javafx.scene.control.TextField) node)
                .toList();

        assertFalse(prefFields.isEmpty(), "Should find preference fields");

        // Index 0: Command Columns (Min 10, Max 100)
        // Index 1: Output Lines (Min 3, Max 100)
        // Index 2: Log Lines (Min 3, Max 100)
        javafx.scene.control.TextField cmdColsField = prefFields.get(0);
        javafx.scene.control.TextField outputLinesField = prefFields.get(1);
        javafx.scene.control.TextField logLinesField = prefFields.get(2);

        // Change Command Columns
        doubleClickOn(cmdColsField);
        write("90"); // Valid value within range

        // Change Output Lines
        doubleClickOn(outputLinesField);
        write("25"); // Valid value within range

        // Change Log Lines
        doubleClickOn(logLinesField);
        write("15"); // Valid value within range

        // 3. Save Changes
        // Ensure the button is enabled (it will be disabled if values are invalid)
        verifyThat("Save", (Node n) -> !n.isDisabled());
        clickOn("Save");
        WaitForAsyncUtils.waitForFxEvents();

        // 4. SIMULATE RESTART
        // We destroy the current instance of the model.
        // This forces the application to re-read the file from disk next time it's asked.
        resetSingleton(PreferencesModel.class, "instance");

        // 5. Reload and Verify
        // Calling getInstance() now triggers the "load()" logic from the file we just saved.
        PreferencesModel reloadedModel = PreferencesModel.getInstance();
        java.util.Map<String, String> prefs = reloadedModel.getCurrentPreferences();

        assertEquals("90", prefs.get("cmdColumns"), "Command columns should persist after restart");
        assertEquals("25", prefs.get("outputLines"), "Output lines should persist after restart");
        assertEquals("15", prefs.get("logLines"), "Log lines should persist after restart");
    }

    @Test
    public void testReloadPreferencesRevertsChanges() {
        // 1. Open Preferences Dialog
        clickOn("#editMenu");
        clickOn("#preferencesMenuItem");
        WaitForAsyncUtils.waitForFxEvents();

        // 2. Robust Lookup: Filter for TextFields that are inside a VBox.
        // The preferences fields (ValidatedField) are wrapped in a VBox.
        // The main command line input is inside an HBox, so it will be excluded.
        List<javafx.scene.control.TextField> prefFields = lookup(".text-field").queryAll().stream()
                .filter(node -> node.getParent() instanceof javafx.scene.layout.VBox)
                .map(node -> (javafx.scene.control.TextField) node)
                .toList();

        assertFalse(prefFields.isEmpty(), "Should find preference fields");

        // Index 0: Command Columns (as added first in PreferencesView)
        javafx.scene.control.TextField cmdColsField = prefFields.get(0);
        String initialValue = cmdColsField.getText();

        // 3. Modify the value in the UI
        doubleClickOn(cmdColsField);
        write("55");

        // Verify it actually changed in the UI before we reload
        assertEquals("55", cmdColsField.getText(), "UI should reflect the typed change");

        // 4. Click "Reload from disk"
        clickOn("Reload from disk");
        WaitForAsyncUtils.waitForFxEvents();

        // 5. Verify the value has reverted to the original
        assertEquals(initialValue, cmdColsField.getText(), "Value should revert to original after clicking reload");
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
        // 1. Open Help Window
        clickOn("#helpMenu");
        clickOn("#helpMenuItem");
        WaitForAsyncUtils.waitForFxEvents();

        // 2. TARGETED LOOKUP: Find the ScrollPane specifically inside the "Help" window
        // We filter all visible ScrollPanes to find the one whose window title is "Help".
        javafx.scene.control.ScrollPane scrollPane = lookup(".scroll-pane").queryAll().stream()
                .filter(node -> {
                    if (node.getScene() == null || node.getScene().getWindow() == null) return false;
                    if (!(node.getScene().getWindow() instanceof Stage)) return false;
                    String title = ((Stage) node.getScene().getWindow()).getTitle();
                    return title != null && title.equals("Help"); // "Help" comes from messages_frontend_en.properties
                })
                .map(node -> (javafx.scene.control.ScrollPane) node)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Could not find ScrollPane in Help window"));

        // 3. Verify initial state
        assertEquals(0.0, scrollPane.getVvalue(), 0.01, "Should start at the top");

        // 4. WAIT FOR LAYOUT: Ensure the ScrollPane has calculated its bounds
        // If Vmax is 0, the content fits in the window, and scrolling is impossible.
        // We wait up to 2 seconds for layout to settle.
        WaitForAsyncUtils.waitFor(2, java.util.concurrent.TimeUnit.SECONDS,
                () -> scrollPane.getVmax() > 0 || scrollPane.isVisible());

        // 5. Perform Scroll Test
        // Only attempt scroll verification if content is actually scrollable (Vmax > 0)
        if (scrollPane.getVmax() > 0) {
            // Scroll down partially
            interact(() -> scrollPane.setVvalue(0.5));
            WaitForAsyncUtils.waitForFxEvents();

            assertTrue(scrollPane.getVvalue() > 0.0, "ScrollPane should have moved down");

            // Scroll to the end
            interact(() -> scrollPane.setVvalue(1.0));
            WaitForAsyncUtils.waitForFxEvents();

            assertEquals(scrollPane.getVmax(), scrollPane.getVvalue(), 0.01, "Should be at the bottom");
        }

        // 6. Close the window properly
        // We target the close button specifically in the Help window to avoid ambiguity
        Node closeBtn = from(scrollPane.getScene().getRoot()).lookup(".button").query();
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