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
import java.util.ResourceBundle;
import java.util.Locale;

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

    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages", Locale.getDefault());

    public static MenuBarView getInstance() {
        if (instance == null) {
            instance = new MenuBarView();
        }
        return instance;
    }

    private MenuBarView() {
        this.fileMenu = new Menu(MESSAGES.getString("menu.file"));
        this.editMenu = new Menu(MESSAGES.getString("menu.edit"));
        this.helpMenu = new Menu(MESSAGES.getString("menu.help"));
        this.menuBar = new MenuBar();
        this.saveMenuItem = new MenuItem(MESSAGES.getString("menu.save"));
        this.saveAsMenuItem = new MenuItem(MESSAGES.getString("menu.saveAs"));
    }

    private void fileMenuInit() {
        MenuItem newMenuItem = new MenuItem(MESSAGES.getString("menu.new"));
        newMenuItem.setId("newMenuItem");

        MenuItem openMenuItem = new MenuItem(MESSAGES.getString("menu.open"));
        openMenuItem.setId("openMenuItem");

        this.saveMenuItem.setId("saveMenuItem");
        this.saveMenuItem.setDisable(true);

        this.saveAsMenuItem.setId("saveAsMenuItem");
        this.saveAsMenuItem.setDisable(true);

        MenuItem exitMenuItem = new MenuItem(MESSAGES.getString("menu.exit"));
        exitMenuItem.setId("exitMenuItem");

        this.fileMenu.setId("fileMenu");
        this.fileMenu.getItems().add(newMenuItem);
        this.fileMenu.getItems().add(new SeparatorMenuItem());
        this.fileMenu.getItems().add(openMenuItem);
        this.fileMenu.getItems().add(saveMenuItem);
        this.fileMenu.getItems().add(saveAsMenuItem);
        this.fileMenu.getItems().add(new SeparatorMenuItem());
        this.fileMenu.getItems().add(exitMenuItem);

        newMenuItem.setOnAction(e -> controller.newFileSystem());
        openMenuItem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(MESSAGES.getString("fileChooser.open"));
            fileChooser.setInitialFileName("fscli_filesystem");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("JSON Files", "*.json"));
            File file = fileChooser.showOpenDialog(null);
            controller.load(file);
        });
        saveMenuItem.setOnAction(e -> controller.save());
        saveAsMenuItem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(MESSAGES.getString("fileChooser.saveAs"));
            fileChooser.setInitialFileName("fscli_filesystem");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("JSON Files", "*.json"));
            File file = fileChooser.showSaveDialog(null);
            controller.saveAs(file);
        });
        exitMenuItem.setOnAction(e -> Platform.exit());
    }

    private void editMenuInit() {
        MenuItem preferencesMenuItem = new MenuItem(MESSAGES.getString("menu.preferences"));
        preferencesMenuItem.setId("preferencesMenuItem");

        this.editMenu.setId("editMenu");
        this.editMenu.getItems().add(preferencesMenuItem);

        preferencesMenuItem.setOnAction(e -> {
            PreferencesController controller = new PreferencesController();
            controller.show();
        });
    }

    private void helpMenuInit() {
        MenuItem helpMenuItem = new MenuItem(MESSAGES.getString("menu.helpItem"));
        helpMenuItem.setId("helpMenuItem");

        MenuItem aboutMenuItem = new MenuItem(MESSAGES.getString("menu.about"));
        aboutMenuItem.setId("aboutMenuItem");

        this.helpMenu.setId("helpMenu");
        this.helpMenu.getItems().add(helpMenuItem);
        this.helpMenu.getItems().add(aboutMenuItem);

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
        aboutStage.setTitle(MESSAGES.getString("about.title"));

        aboutStage.initModality(Modality.APPLICATION_MODAL);
        aboutStage.initOwner(ownerStage);

        VBox contentBox = new VBox(15);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(20));

        Label titleLabel = new Label(applicationName);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label buildDateLabel = new Label(MESSAGES.getString("about.buildDate") + ": " + buildDate);
        Label versionLabel = new Label(MESSAGES.getString("about.version") + ": " + version);
        Label developersLabel = new Label(MESSAGES.getString("about.developers") + ": " + developers);

        Button closeButton = new Button(MESSAGES.getString("about.close"));
        closeButton.setOnAction(e -> aboutStage.close());

        contentBox.getChildren().addAll(titleLabel, buildDateLabel, versionLabel, developersLabel, closeButton);

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
        if (event == null) return;
        System.out.println(event);

        if (event.getError() == EventError.ERROR) return;

        if (event.getIsSuccess()) {
            saveMenuItem.setDisable(false);
            saveAsMenuItem.setDisable(false);
        } else {
            saveMenuItem.setDisable(true);
            saveAsMenuItem.setDisable(true);
        }
    }
}
