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
