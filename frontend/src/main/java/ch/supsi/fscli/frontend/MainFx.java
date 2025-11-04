package ch.supsi.fscli.frontend;

import ch.supsi.fscli.backend.application.PreferencesService;
import ch.supsi.fscli.backend.business.UserPreferences;
import ch.supsi.fscli.frontend.controller.PreferencesController;
import ch.supsi.fscli.frontend.util.*;
import ch.supsi.fscli.backend.util.PreferencesLogger;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainFx extends Application {

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
        MenuItem exitMenuItem = new MenuItem("Exit...");
        exitMenuItem.setId("exitMenuItem");
        this.fileMenu = new Menu("File");
        this.fileMenu.getItems().add(exitMenuItem);

        // EDIT MENU
        MenuItem preferencesMenuItem = new MenuItem("Preferences...");
        preferencesMenuItem.setOnAction(e -> {
            PreferencesController controller = new PreferencesController();
            controller.show();
        });
        this.editMenu = new Menu("Edit");
        this.editMenu.getItems().add(preferencesMenuItem);

        // HELP MENU
        this.helpMenu = new Menu("Help");

        // MENU BAR
        this.menuBar = new MenuBar();
        this.menuBar.getMenus().addAll(fileMenu, editMenu, helpMenu);

        // COMMAND LINE
        this.enter = new Button("enter");
        this.commandLineLabel = new Label("command");
        this.commandLine = new TextField();

        // OUTPUT VIEW
        this.outputView = new TextArea();
        this.outputView.appendText("This is an example output text...\n");

        // LOG VIEW
        this.logView = new TextArea();
        this.logView.setEditable(false);
        this.logView.appendText("Application started.\n");
    }

    @Override
    public void start(Stage primaryStage) {

        // --- CARICA PREFERENZE SANITIZZATE ---
        PreferencesService prefService = new PreferencesService();
        UserPreferences prefs = prefService.getCurrentPrefs(); // già sanitizzate

        // --- LOGGER FX ---
        FxLogger fxLogger = FxLogger.getInstance();
        fxLogger.setLogArea(this.logView);
        fxLogger.setLogAreaRowCount(prefs.getLogLines());

        PreferencesLogger.setExternalListener((level, message) -> {
            fxLogger.log("[" + level + "] " + message);
        });

        fxLogger.log("Frontend logger initialized.");
        PreferencesLogger.logInfo("Backend logger bridge active.");


        // --- GUI ---
        this.commandLine.setPrefColumnCount(prefs.getCmdColumns());
        this.commandLine.setStyle("-fx-font-family: '" + prefs.getCmdFont() + "';");

        HBox commandLinePane = new HBox(10, this.commandLineLabel, this.commandLine, this.enter);
        commandLinePane.setAlignment(Pos.BASELINE_LEFT);
        commandLinePane.setPadding(new Insets(5));

        HBox.setHgrow(this.commandLine, Priority.ALWAYS);


        VBox top = new VBox(this.menuBar, commandLinePane);

        this.outputView.setPrefRowCount(prefs.getOutputLines());
        this.outputView.setStyle("-fx-font-family: '" + prefs.getOutputFont() + "';");

        this.logView.setPrefRowCount(prefs.getLogLines());
        this.logView.setStyle("-fx-font-family: '" + prefs.getLogFont() + "';");

        BorderPane rootPane = new BorderPane();
        rootPane.setTop(top);
        rootPane.setCenter(new ScrollPane(this.outputView));
        rootPane.setBottom(new ScrollPane(this.logView));

        // --- Calcolo larghezza fissa basata sulle colonne ---
        double charWidth = 8.0;
        double baseWidth = prefs.getCmdColumns() * charWidth + 120;

// --- Blocca larghezze coerenti ---
        this.commandLine.setPrefColumnCount(prefs.getCmdColumns());
        this.commandLine.setMinWidth(Region.USE_PREF_SIZE);
        this.commandLine.setMaxWidth(Region.USE_PREF_SIZE);

        this.outputView.setPrefWidth(baseWidth);
        this.outputView.setMinWidth(baseWidth);
        this.outputView.setMaxWidth(baseWidth);

        this.logView.setPrefWidth(baseWidth);
        this.logView.setMinWidth(baseWidth);
        this.logView.setMaxWidth(baseWidth);

// --- GUI layout ---
        primaryStage.setResizable(false);
        Scene mainScene = new Scene(rootPane);
        primaryStage.setTitle(this.applicationTitle);
        primaryStage.setScene(mainScene);
        primaryStage.setHeight(
                80 + prefs.getOutputLines() * 20 + prefs.getLogLines() * 15
        );
        primaryStage.show();


    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
