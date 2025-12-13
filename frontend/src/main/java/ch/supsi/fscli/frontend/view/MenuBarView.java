package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.event.AboutEvent;
import ch.supsi.fscli.frontend.handler.AboutEventHandler;
import ch.supsi.fscli.frontend.handler.PreferencesHandler;
import ch.supsi.fscli.frontend.i18n.FrontendMessageProvider;
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
import java.util.Map;
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
    private final MenuItem exitMenuItem;

    @Setter
    private FileSystemEventHandler fileSystemEventHandler;
    @Setter
    private AboutEventHandler aboutEventHandler;
    @Setter
    private PreferencesHandler preferencesHandler;
    @Setter
    private ch.supsi.fscli.frontend.event.EventManager<ch.supsi.fscli.frontend.event.PreferencesEvent> preferencesEventManager;

    // LISTENERS
    private final Listener<FileSystemEvent> fileSystemListener;
    private final Listener<AboutEvent> aboutListener;
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
        this.exitMenuItem = new MenuItem(FrontendMessageProvider.get("menu.exit"));
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
        aboutListener = event -> {
            if (event == null) return;
            if (event.appInfo() == null) return;
            showAboutWindow(event.appInfo());
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
        exitMenuItem.setOnAction(e -> handleExit());
    }

    private void handleExit() {
        if (fileSystemEventHandler != null && fileSystemEventHandler.hasUnsavedChanges()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(FrontendMessageProvider.get("alert.unsavedChanges"));
            alert.setHeaderText(FrontendMessageProvider.get("alert.unsavedChanges"));
            alert.setContentText(FrontendMessageProvider.get("alert.unsavedChangesMessage"));
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                if (!"true".equals(System.getProperty("testMode"))) {
                    Platform.exit();
                }
            }
        } else {
            if (!"true".equals(System.getProperty("testMode"))) {
                Platform.exit();
            }
        }
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
            if (preferencesHandler != null) {
                PreferencesView preferencesView = PreferencesView.getInstance();
                
                // Register listener if event manager is available and not already registered
                if (preferencesEventManager != null) {
                    preferencesEventManager.addListener(preferencesView.getPreferencesListener());
                }
                
                preferencesView.setPreferencesHandler(preferencesHandler);
                preferencesView.show();
            }
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
            aboutEventHandler.showAppInfo();
        });
    }

    private void showAboutWindow(Map<String, String> appInfo) {
        String applicationName = appInfo.get("AppName");
        String buildDate = appInfo.get("Build Date");
        String version = appInfo.get("Version");
        String developers = appInfo.get("Developers");

        Stage aboutStage = new Stage();
        aboutStage.setTitle(FrontendMessageProvider.get("about.title"));

        aboutStage.initModality(Modality.APPLICATION_MODAL);
        Stage ownerStage = (Stage) newMenuItem.getParentPopup().getOwnerWindow();
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
        String title = FrontendMessageProvider.get("help.title");
        String text = FrontendMessageProvider.get("help.text");

        Stage helpStage = new Stage();
        helpStage.setTitle(title);

        helpStage.initModality(Modality.APPLICATION_MODAL);
        helpStage.initOwner(ownerStage);

        VBox contentBox = new VBox(15);
        contentBox.setAlignment(Pos.TOP_LEFT);
        contentBox.setPadding(new Insets(10));

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Text helpText = new Text(text); 
        helpText.setWrappingWidth(500);
        helpText.setStyle("-fx-font-family: 'Monospaced', 'Courier New', monospace; -fx-font-size: 11px;");
        ScrollPane scrollPane = new ScrollPane(helpText);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(500);


        Button closeButton = new Button(FrontendMessageProvider.get("help.closeButton"));
        closeButton.setOnAction(e -> helpStage.close());

        contentBox.getChildren().addAll(titleLabel, scrollPane, closeButton);

        Scene aboutScene = new Scene(contentBox, 550, 600);
        helpStage.setScene(aboutScene);

        helpStage.setResizable(false);

        helpStage.showAndWait();
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
