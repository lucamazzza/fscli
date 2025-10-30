package ch.supsi.fscli.frontend;

import ch.supsi.fscli.frontend.controller.PreferencesController;
import ch.supsi.fscli.frontend.util.FxLogger;
import ch.supsi.fscli.backend.util.PreferencesLogger;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.logging.Level;

public class MainFx extends Application {
    private static final int PREF_INSETS_SIZE = 7;
    private static final int PREF_COMMAND_SPACER_WIDTH = 11;
    private static final int COMMAND_LINE_PREF_COLUMN_COUNT = 72;
    private static final int PREF_OUTPUT_VIEW_ROW_COUNT = 25;
    private static final int PREF_LOG_VIEW_ROW_COUNT = 5;

    private final String applicationTitle;
    private final MenuBar menuBar;
    private final Menu fileMenu;
    private final Menu editMenu;
    private final Menu helpMenu;
    private final Label commandLineLabel;
    private final Button enter;
    private final TextField commandLine;
    private final TextArea outputView;
    private final TextArea logView;

    public MainFx() {
        this.applicationTitle = "filesystem command interpreter simulator";

        // FILE MENU
        MenuItem newMenuItem = new MenuItem("New");
        newMenuItem.setId("newMenuItem");

        MenuItem openMenuItem = new MenuItem("Open...");
        openMenuItem.setId("openMenuItem");

        MenuItem saveMenuItem = new MenuItem("Save");
        saveMenuItem.setId("saveMenuItem");

        MenuItem saveAsMenuItem = new MenuItem("Save as...");
        saveAsMenuItem.setId("saveAsMenuItem");

        MenuItem exitMenuItem = new MenuItem("Exit...");
        exitMenuItem.setId("exitMenuItem");

        this.fileMenu = new Menu("File");
        this.fileMenu.setId("fileMenu");
        this.fileMenu.getItems().addAll(
                newMenuItem,
                new SeparatorMenuItem(),
                openMenuItem,
                saveMenuItem,
                saveAsMenuItem,
                new SeparatorMenuItem(),
                exitMenuItem
        );

        // EDIT MENU
        MenuItem preferencesMenuItem = new MenuItem("Preferences...");
        preferencesMenuItem.setId("preferencesMenuItem");
        preferencesMenuItem.setOnAction(e -> {
            PreferencesController controller = new PreferencesController();
            controller.show();
        });

        this.editMenu = new Menu("Edit");
        this.editMenu.setId("editMenu");
        this.editMenu.getItems().add(preferencesMenuItem);

        // HELP MENU
        MenuItem helpMenuItem = new MenuItem("Help");
        helpMenuItem.setId("helpMenuItem");

        MenuItem aboutMenuItem = new MenuItem("About");
        aboutMenuItem.setId("aboutMenuItem");

        this.helpMenu = new Menu("Help");
        this.helpMenu.setId("helpMenu");
        this.helpMenu.getItems().addAll(helpMenuItem, aboutMenuItem);

        // MENU BAR
        this.menuBar = new MenuBar();
        this.menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);

        // COMMAND LINE
        this.enter = new Button("enter");
        this.enter.setId("enter");

        this.commandLineLabel = new Label("command");
        this.commandLine = new TextField();

        // OUTPUT VIEW
        this.outputView = new TextArea();
        this.outputView.setId("outputView");
        this.outputView.appendText("This is an example output text...\n");

        // LOG VIEW
        this.logView = new TextArea();
        this.logView.setId("logView");
        this.logView.setEditable(false);
        this.logView.appendText("Application started.\n");
    }

    @Override
    public void start(Stage primaryStage) {
        // command line
        this.commandLine.setPrefColumnCount(COMMAND_LINE_PREF_COLUMN_COUNT);

        // horizontal box to hold the command line
        HBox commandLinePane = new HBox();
        commandLinePane.setAlignment(Pos.BASELINE_LEFT);
        commandLinePane.setPadding(new Insets(PREF_INSETS_SIZE));

        Region spacer1 = new Region();
        spacer1.setPrefWidth(PREF_COMMAND_SPACER_WIDTH);

        Region spacer2 = new Region();
        spacer2.setPrefWidth(PREF_COMMAND_SPACER_WIDTH);

        commandLinePane.getChildren().addAll(
                this.commandLineLabel, spacer1, this.commandLine, spacer2, this.enter
        );

        // vertical pane to hold the menu bar and the command line
        VBox top = new VBox(this.menuBar, commandLinePane);

        // output view
        this.outputView.setPrefRowCount(PREF_OUTPUT_VIEW_ROW_COUNT);
        this.outputView.setEditable(false);

        // scroll pane to hold the output view
        ScrollPane centerPane = new ScrollPane();
        centerPane.setFitToHeight(true);
        centerPane.setFitToWidth(true);
        centerPane.setPadding(new Insets(PREF_INSETS_SIZE));
        centerPane.setContent(this.outputView);

        // log view
        this.logView.setPrefRowCount(PREF_LOG_VIEW_ROW_COUNT);
        this.logView.setEditable(false);

        // scroll pane to hold log view
        ScrollPane bottomPane = new ScrollPane();
        bottomPane.setFitToHeight(true);
        bottomPane.setFitToWidth(true);
        bottomPane.setPadding(new Insets(PREF_INSETS_SIZE));
        bottomPane.setContent(this.logView);

        // root pane
        BorderPane rootPane = new BorderPane();
        rootPane.setTop(top);
        rootPane.setCenter(centerPane);
        rootPane.setBottom(bottomPane);

        // scene
        Scene mainScene = new Scene(rootPane);

        // setup stage
        primaryStage.setTitle(this.applicationTitle);
        primaryStage.setResizable(true);
        primaryStage.setScene(mainScene);
        primaryStage.setOnCloseRequest(e -> primaryStage.close());

        // === INITIALIZZA LOGGER FX ===
        FxLogger fxLogger = FxLogger.getInstance();
        fxLogger.setLogArea(this.logView);

        PreferencesLogger.setExternalListener((level, message) -> {
            fxLogger.log("[" + level + "] " + message);
        });

        // esempio di log iniziale
        fxLogger.log("Frontend logger initialized.");
        PreferencesLogger.logInfo("Backend logger bridge active.");

        // show the stage
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
