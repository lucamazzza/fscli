package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.controller.AboutController;
import ch.supsi.fscli.frontend.controller.PreferencesController;
import ch.supsi.fscli.frontend.event.EventError;
import ch.supsi.fscli.frontend.event.FileEvent;
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

@Getter
public class MenuBarView implements View, Listener<FileEvent> {
    private final Menu fileMenu;
    private final Menu editMenu;
    private final Menu helpMenu;
    private final MenuBar menuBar;

    private final MenuItem saveMenuItem;
    private final MenuItem saveAsMenuItem;

    @Setter
    private FileSystemEventHandler controller;

    private static MenuBarView instance;

    public static MenuBarView getInstance() {
        if (instance == null) {
            instance = new MenuBarView();
        }
        return instance;
    }

    private MenuBarView() {
        this.fileMenu = new Menu("File");
        this.editMenu = new Menu("Edit");
        this.helpMenu = new Menu("Help");
        this.menuBar = new MenuBar();
        this.saveMenuItem = new MenuItem("Save");
        this.saveAsMenuItem = new MenuItem("Save as...");
    }

    private void fileMenuInit() {
        MenuItem newMenuItem = new MenuItem("New");
        newMenuItem.setId("newMenuItem");

        MenuItem openMenuItem = new MenuItem("Open...");
        openMenuItem.setId("openMenuItem");

        this.saveMenuItem.setId("saveMenuItem");
        this.saveMenuItem.setDisable(true);

        this.saveAsMenuItem.setId("saveAsMenuItem");
        this.saveAsMenuItem.setDisable(true);

        MenuItem exitMenuItem = new MenuItem("Exit");
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
        newMenuItem.setOnAction(e -> controller.newFileSystem());
        openMenuItem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open a filesystem...");
            fileChooser.setInitialFileName("fscli_filesystem");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("JSON Files", "*.json"));
            File file = fileChooser.showOpenDialog(null);
            controller.load(file);
        });
        saveMenuItem.setOnAction(e -> controller.save());
        saveAsMenuItem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open a filesystem...");
            fileChooser.setInitialFileName("fscli_filesystem");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("JSON Files", "*.json"));
            File file = fileChooser.showSaveDialog(null);
            controller.load(file);
            controller.saveAs(file);
        });
        exitMenuItem.setOnAction(e -> Platform.exit());
    }

    private void editMenuInit() {
        MenuItem preferencesMenuItem = new MenuItem("Preferences...");
        preferencesMenuItem.setId("preferencesMenuItem");

        this.editMenu.setId("editMenu");
        this.editMenu.getItems().add(preferencesMenuItem);

        preferencesMenuItem.setOnAction(e -> {
            PreferencesController controller = new PreferencesController();
            controller.show();
        });
    }

    private void helpMenuInit() {
        MenuItem helpMenuItem = new MenuItem("Help");
        helpMenuItem.setId("helpMenuItem");

        MenuItem aboutMenuItem = new MenuItem("About");
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
        aboutStage.setTitle("Application Information");

        aboutStage.initModality(Modality.APPLICATION_MODAL);
        aboutStage.initOwner(ownerStage); // Set the owner window

        VBox contentBox = new VBox(15); // 15px spacing
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(20));

        Label titleLabel = new Label(applicationName);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label buildDateLabel = new Label("Build date: " + buildDate);
        Label versionLabel = new Label("Version: " + version);
        Label copyrightLabel = new Label("Developers: " + developers);

        Button closeButton = new Button("Close");
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


    @Override
    public void update(FileEvent event) {
        if (event == null) {
            return;
        }
        System.out.println(event);

        if (event.getError() == EventError.ERROR) {
            return;
        }

        if (event.getIsSuccess()) {
            saveMenuItem.setDisable(false);
            saveAsMenuItem.setDisable(false);
            return;
        }
        saveMenuItem.setDisable(true);
        saveAsMenuItem.setDisable(true);
    }
}
