package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.controller.AboutController;
import ch.supsi.fscli.frontend.model.ApplicationModel;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;

@Getter
public class MenuView implements View{
    private final Menu fileMenu;
    private final Menu editMenu;
    private final Menu helpMenu;

    private static MenuView instance;

    public static MenuView getInstance() {
        if (instance == null) {
            instance = new MenuView();
        }
        return instance;
    }

    private MenuView () {
        // FILE MENU
        this.fileMenu = new Menu("File");
        fileMenuInit();

        // EDIT MENU
        this.editMenu = new Menu("Edit");
        editMenuInit();

        // HELP MENU
        this.helpMenu = new Menu("Help");
        helpMenuInit();
    }

    private void fileMenuInit() {
        MenuItem newMenuItem = new MenuItem("New");
        newMenuItem.setId("newMenuItem");

        MenuItem openMenuItem = new MenuItem("Open...");
        openMenuItem.setId("openMenuItem");

        MenuItem saveMenuItem = new MenuItem("Save");
        saveMenuItem.setId("saveMenuItem");
        saveMenuItem.setDisable(true);

        MenuItem saveAsMenuItem = new MenuItem("Save as...");
        saveAsMenuItem.setId("saveAsMenuItem");
        saveAsMenuItem.setDisable(true);

        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setId("exitMenuItem");
        exitMenuItem.setOnAction(e -> Platform.exit());

        this.fileMenu.setId("fileMenu");
        this.fileMenu.getItems().add(newMenuItem);
        this.fileMenu.getItems().add(new SeparatorMenuItem());
        this.fileMenu.getItems().add(openMenuItem);
        this.fileMenu.getItems().add(saveMenuItem);
        this.fileMenu.getItems().add(saveAsMenuItem);
        this.fileMenu.getItems().add(new SeparatorMenuItem());
        this.fileMenu.getItems().add(exitMenuItem);
    }

    private void editMenuInit() {
        MenuItem preferencesMenuItem = new MenuItem("Preferences...");
        preferencesMenuItem.setId("preferencesMenuItem");

        this.editMenu.setId("editMenu");
        this.editMenu.getItems().add(preferencesMenuItem);
    }

    private void helpMenuInit() {
        MenuItem helpMenuItem = new MenuItem("Help");
        helpMenuItem.setId("helpMenuItem");

        MenuItem aboutMenuItem = new MenuItem("About");
        aboutMenuItem.setId("aboutMenuItem");

        aboutMenuItem.setOnAction(e -> {
            Stage ownerStage = (Stage) aboutMenuItem.getParentPopup().getOwnerWindow();
            showAboutWindow(ownerStage);
        });

        this.helpMenu.setId("helpMenu");
        this.helpMenu.getItems().add(helpMenuItem);
        this.helpMenu.getItems().add(aboutMenuItem);
    }

    private void showAboutWindow(Stage ownerStage) {
        AboutController controller = AboutController.getInstance();
        String applicationName = controller.getAppName();
        String buildDate = controller.getBuildDate();
        String version = controller.getVerion();
        String developers = controller.getDevelopers();

        // 1. Create the new Stage (the window)
        Stage aboutStage = new Stage();
        aboutStage.setTitle("Application Information");

        // 2. Set it as a modal window
        // Modality.APPLICATION_MODAL blocks all other application windows
        aboutStage.initModality(Modality.APPLICATION_MODAL);
        aboutStage.initOwner(ownerStage); // Set the owner window

        // 3. Create the layout for the content
        VBox contentBox = new VBox(15); // 15px spacing
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(20));

        // 4. Add content (Icon, Labels, Button)
        // --- App Info ---
        Label titleLabel = new Label(applicationName);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label buildDateLabel = new Label("Build date: " + buildDate);
        Label versionLabel = new Label("Version: " + version);
        Label copyrightLabel = new Label("Developers: " + developers);

        // --- Close Button ---
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> aboutStage.close()); // Action to close this stage

        // 5. Add all content to the layout
        contentBox.getChildren().addAll(titleLabel, buildDateLabel, versionLabel, copyrightLabel, closeButton);

        // 6. Create the scene and set it on the stage
        Scene aboutScene = new Scene(contentBox, 350, 250);
        aboutStage.setScene(aboutScene);

        // Optional: Prevent resizing
        aboutStage.setResizable(false);

        // 7. Show the window and wait for it to be closed
        aboutStage.showAndWait();
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    @Override
    public void update() {

    }
}
