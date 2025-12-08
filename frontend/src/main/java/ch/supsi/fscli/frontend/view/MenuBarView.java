package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.i18n.FrontendMessageProvider;import ch.supsi.fscli.frontend.controller.AboutController;
import ch.supsi.fscli.frontend.controller.PreferencesController;
import ch.supsi.fscli.frontend.event.FileSystemEvent;
import ch.supsi.fscli.frontend.handler.FileSystemEventHandler;
import ch.supsi.fscli.frontend.listener.Listener;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.Optional;

@Getter
public class MenuBarView implements View {
    private final Menu fileMenu;
    private final Menu editMenu;
    private final Menu helpMenu;
    private final MenuBar menuBar;

    private final MenuItem saveMenuItem;
    private final MenuItem saveAsMenuItem;
    private final MenuItem newMenuItem;

    @Setter
    private FileSystemEventHandler fileSystemEventHandler;

    // LISTENERS
    private final Listener<FileSystemEvent> fileSystemListener;

    private static MenuBarView instance;

    public static MenuBarView getInstance() {
        if (instance == null) {
            instance = new MenuBarView();
        }
        return instance;
    }

    private MenuBarView() {
        this.fileMenu = new Menu(FrontendMessageProvider.get("menu.file"));
        this.editMenu = new Menu(FrontendMessageProvider.get("menu.edit"));
        this.helpMenu = new Menu(FrontendMessageProvider.get("menu.help"));
        this.menuBar = new MenuBar();
        this.saveMenuItem = new MenuItem(FrontendMessageProvider.get("menu.save"));
        this.saveAsMenuItem = new MenuItem(FrontendMessageProvider.get("menu.saveAs"));
        this.newMenuItem = new MenuItem(FrontendMessageProvider.get("menu.new"));
        fileSystemListener = event -> {
            if (event == null) return;
            if (event.error() == null) return;
            switch (event.error()) {
                case NEW_SUCCESS, LOAD_SUCCESS -> {
                    saveMenuItem.setDisable(false);
                    saveAsMenuItem.setDisable(false);
                }
                case NEW_FAILED_UNSAVED_WORK -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    Stage owner = (Stage) newMenuItem.getParentPopup().getOwnerWindow();
                    alert.initOwner(owner);
                    alert.initModality(Modality.NONE);
                    alert.setTitle(FrontendMessageProvider.get("fileEvent.UnsavedWork"));
                    alert.setHeaderText(FrontendMessageProvider.get("fileEvent.UnsavedWork"));
                    alert.setContentText(FrontendMessageProvider.get("fileEvent.UnsavedWorkMessage"));
                    alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.YES) {
                        fileSystemEventHandler.newFileSystem(true);
                    }
                }
                case SAVE_FAILED_FILE_NOT_FOUND -> {
                    savePrompt();
                }
            }
        };
    }

    private void fileMenuInit() {
        this.newMenuItem.setId("newMenuItem");

        MenuItem openMenuItem = new MenuItem(FrontendMessageProvider.get("menu.open"));
        openMenuItem.setId("openMenuItem");

        this.saveMenuItem.setId("saveMenuItem");
        this.saveMenuItem.setDisable(true);

        this.saveAsMenuItem.setId("saveAsMenuItem");
        this.saveAsMenuItem.setDisable(true);

        MenuItem exitMenuItem = new MenuItem(FrontendMessageProvider.get("menu.exit"));
        exitMenuItem.setId("exitMenuItem");

        this.fileMenu.setId("fileMenu");
        this.fileMenu.getItems().add(newMenuItem);
        this.fileMenu.getItems().add(new SeparatorMenuItem());
        this.fileMenu.getItems().add(openMenuItem);
        this.fileMenu.getItems().add(saveMenuItem);
        this.fileMenu.getItems().add(saveAsMenuItem);
        this.fileMenu.getItems().add(new SeparatorMenuItem());
        this.fileMenu.getItems().add(exitMenuItem);

        // MODIFY BEHAVIOUR HERE
        newMenuItem.setOnAction(e -> fileSystemEventHandler.newFileSystem(false));
        openMenuItem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(FrontendMessageProvider.get("fileChooser.open"));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("JSON Files", "*.json"));
            File file = fileChooser.showOpenDialog(null);
            fileSystemEventHandler.load(file);
        });
        saveMenuItem.setOnAction(e -> fileSystemEventHandler.save());
        saveAsMenuItem.setOnAction(e -> savePrompt());
        exitMenuItem.setOnAction(e -> Platform.exit());
    }

    private void savePrompt() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(FrontendMessageProvider.get("fileChooser.saveAs"));
        fileChooser.setInitialFileName("fscli_filesystem");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showSaveDialog(null);
        fileSystemEventHandler.saveAs(file);
    }

    private void editMenuInit() {
        MenuItem preferencesMenuItem = new MenuItem(FrontendMessageProvider.get("menu.preferences"));
        preferencesMenuItem.setId("preferencesMenuItem");

        this.editMenu.setId("editMenu");
        this.editMenu.getItems().add(preferencesMenuItem);

        preferencesMenuItem.setOnAction(e -> {
            ch.supsi.fscli.backend.controller.PreferencesController backendController = 
                ch.supsi.fscli.backend.di.BackendInjector.getInstance(ch.supsi.fscli.backend.controller.PreferencesController.class);
            PreferencesController controller = new PreferencesController();
            controller.show();
        });
    }

    private void helpMenuInit() {
        MenuItem helpMenuItem = new MenuItem(FrontendMessageProvider.get("menu.help"));
        helpMenuItem.setId("helpMenuItem");

        MenuItem aboutMenuItem = new MenuItem(FrontendMessageProvider.get("menu.about"));
        aboutMenuItem.setId("aboutMenuItem");

        this.helpMenu.setId("helpMenu");
        this.helpMenu.getItems().add(helpMenuItem);
        this.helpMenu.getItems().add(aboutMenuItem);

        // MODIFY BEHAVIOUR HERE
        helpMenuItem.setOnAction(e -> {
            Stage ownerStage = (Stage) helpMenuItem.getParentPopup().getOwnerWindow();
            showHelpWindow(ownerStage);
        });
        aboutMenuItem.setOnAction(e -> {
            Stage ownerStage = (Stage) aboutMenuItem.getParentPopup().getOwnerWindow();
            showAboutWindow(ownerStage);
        });
    }

    private void showAboutWindow(Stage ownerStage) {
        AboutController controller = AboutController.getInstance();
        String applicationName = controller.getAppName();
        String buildDate = controller.getBuildDate();
        String version = controller.getVerion();
        String developers = controller.getDevelopers();

        Stage aboutStage = new Stage();
        aboutStage.setTitle(FrontendMessageProvider.get("about.title"));

        aboutStage.initModality(Modality.APPLICATION_MODAL);
        aboutStage.initOwner(ownerStage); // Set the owner window

        VBox contentBox = new VBox(15); // 15px spacing
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(20));

        Label titleLabel = new Label(applicationName);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label buildDateLabel = new Label(FrontendMessageProvider.get("about.buildDate") + buildDate);
        Label versionLabel = new Label(FrontendMessageProvider.get("about.version") + version);
        Label copyrightLabel = new Label(FrontendMessageProvider.get("about.developers") + developers);

        Button closeButton = new Button(FrontendMessageProvider.get("about.close"));
        closeButton.setOnAction(e -> aboutStage.close()); // Action to close this stage

        contentBox.getChildren().addAll(titleLabel, buildDateLabel, versionLabel, copyrightLabel, closeButton);

        Scene aboutScene = new Scene(contentBox, 350, 250);
        aboutStage.setScene(aboutScene);

        aboutStage.setResizable(false);

        aboutStage.showAndWait();
    }

    private void showHelpWindow(Stage ownerStage) {
        AboutController controller = AboutController.getInstance();
        String title = "Help";
        String text = """
            ═══════════════════════════════════════════════════════════════
            FILESYSTEM COMMAND LINE INTERFACE SIMULATOR - USER GUIDE
            ═══════════════════════════════════════════════════════════════
            
            GETTING STARTED
            ───────────────
            1. Create a new filesystem: File > New
            2. Enter commands in the command line interface
            3. Type 'help' in the CLI to see available commands
            4. Save your work: File > Save or Save As...
            
            MENU BAR
            ────────
            • File Menu:
              - New: Create a new virtual filesystem
              - Open: Load an existing filesystem from a JSON file
              - Save: Save the current filesystem to its file
              - Save As: Save the filesystem to a new location
              - Exit: Close the application
            
            • Edit Menu:
              - Preferences: Customize application settings including:
                · Language selection
                · Command line font and column width
                · Output view font and line count
                · Log view font and line count
            
            • Help Menu:
              - Help: Display this help window
              - About: View application information and version
            
            COMMAND LINE INTERFACE
            ──────────────────────
            Available Commands (type 'help' in CLI for detailed syntax):
            
            • pwd         - Print working directory
            • cd <path>   - Change directory
            • ls [path]   - List directory contents
            • mkdir <dir> - Create a new directory
            • rmdir <dir> - Remove an empty directory
            • touch <file>- Create a new empty file
            • rm <file>   - Remove a file
            • cp <src> <dst> - Copy file or directory
            • mv <src> <dst> - Move or rename file/directory
            • ln <target> <link> - Create a symbolic link
            
            Command Execution:
            - Type your command in the command field
            - Press Enter or click the "enter" button
            - View results in the output area below
            - Commands only work after creating/loading a filesystem
            
            FILE OPERATIONS
            ───────────────
            • Creating a Filesystem:
              Go to File > New. This initializes a fresh virtual filesystem.
              Any unsaved changes will prompt a confirmation dialog.
            
            • Opening a Filesystem:
              Go to File > Open and select a JSON filesystem file.
              Supported format: JSON files (*.json)
            
            • Saving Your Work:
              - Save: Quickly save to the current file location
              - Save As: Choose a new filename and location
              - Remember to save regularly to avoid data loss
            
            PREFERENCES
            ───────────
            Customize your experience via Edit > Preferences:
            
            • Language: Switch between English and Italian
            • Command Columns: Adjust command input field width
            • Command Font: Change command line text size
            • Output Lines: Set number of visible output lines
            • Output Font: Change output area text size
            • Log Lines: Set number of visible log lines
            • Log Font: Change log area text size
            
            Note: Restart required for some preferences to take effect.
            
            OUTPUT AREAS
            ────────────
            • Command Output View: Displays command execution results
              and error messages
            • Log Area: Shows system logs and application events
            
            TIPS & BEST PRACTICES
            ──────────────────────
            ✓ Always create or open a filesystem before running commands
            ✓ Use 'pwd' to check your current directory location
            ✓ Use 'ls' to see what files and directories exist
            ✓ Save your work frequently using File > Save
            ✓ Type 'help' in the CLI for command-specific syntax
            ✓ Check the log area for detailed system information
            
            TROUBLESHOOTING
            ───────────────
            • "No filesystem loaded" error:
              Create a new filesystem (File > New) or open an existing one
            
            • Command not executing:
              Ensure filesystem is initialized and command syntax is correct
            
            • Cannot save file:
              Use "Save As" to specify a new location
            
            • Preferences not applying:
              Restart the application after changing preferences
            
            KEYBOARD SHORTCUTS
            ──────────────────
            • Enter: Execute the current command (when in command field)
            
            ═══════════════════════════════════════════════════════════════
            For additional support or to report issues, please contact
            your system administrator or refer to the project documentation.
            ═══════════════════════════════════════════════════════════════
            """;

        Stage aboutStage = new Stage();
        aboutStage.setTitle("Help");

        aboutStage.initModality(Modality.APPLICATION_MODAL);
        aboutStage.initOwner(ownerStage); // Set the owner window

        VBox contentBox = new VBox(15); // 15px spacing
        contentBox.setAlignment(Pos.TOP_LEFT);
        contentBox.setPadding(new Insets(10));

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Text helpText = new Text(text); 
        helpText.setWrappingWidth(500); // Wrap text within 700px
        helpText.setStyle("-fx-font-family: 'Monospaced', 'Courier New', monospace; -fx-font-size: 11px;");
        ScrollPane scrollPane = new ScrollPane(helpText);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(500);


        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> aboutStage.close()); // Action to close this stage

        contentBox.getChildren().addAll(titleLabel, scrollPane, closeButton);

        Scene aboutScene = new Scene(contentBox, 550, 600);
        aboutStage.setScene(aboutScene);

        aboutStage.setResizable(true);

        aboutStage.showAndWait();
    }

    private void menuBarInit() {
        this.menuBar.getMenus().addAll(this.fileMenu, this.editMenu, this.helpMenu);
    }

    @Override
    public void init() {
        fileMenuInit();
        editMenuInit();
        helpMenuInit();
        menuBarInit();
    }
}
