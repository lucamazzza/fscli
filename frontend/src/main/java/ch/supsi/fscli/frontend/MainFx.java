package ch.supsi.fscli.frontend;

import ch.supsi.fscli.backend.service.PreferencesService;
import ch.supsi.fscli.backend.core.UserPreferences;
import ch.supsi.fscli.frontend.util.*;
import ch.supsi.fscli.backend.util.PreferencesLogger;
import ch.supsi.fscli.frontend.controller.FileSystemController;
import ch.supsi.fscli.frontend.event.FileEventManager;
import ch.supsi.fscli.frontend.model.FileSystem;
import ch.supsi.fscli.frontend.view.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainFx extends Application {
    private final String applicationTitle;
    // VIEWS
    private final MenuBarView menuBar;
    private final CommandLineView commandLine;
    private final LogAreaView logArea;

    public MainFx() {
        this.applicationTitle = "filesystem command interpreter simulator";
        this.menuBar = MenuBarView.getInstance();
        this.commandLine = CommandLineView.getInstance();
        this.logArea = LogAreaView.getInstance();

        FileEventManager fileEventManager = new FileEventManager();
        fileEventManager.addListener(this.menuBar);

        FileSystem fileSystem = FileSystem.getInstance();
        fileSystem.setEventManager(fileEventManager);

        FileSystemController fileSystemController = FileSystemController.getInstance();
        fileSystemController.setModel(fileSystem);

        menuBar.setController(fileSystemController);
    }

    @Override
    public void start(Stage primaryStage) {
        this.menuBar.init();
        this.commandLine.init();
        this.logArea.init();

        // --- CARICA PREFERENZE SANITIZZATE ---
        PreferencesService prefService = new PreferencesService();
        UserPreferences prefs = prefService.getCurrentPrefs(); // già sanitizzate

        // --- LOGGER FX ---
        FxLogger fxLogger = FxLogger.getInstance();
        fxLogger.setLogArea(this.logArea.getLogView());
        fxLogger.setLogAreaRowCount(prefs.getLogLines());

        PreferencesLogger.setExternalListener((level, message) -> {
            fxLogger.log("[" + level + "] " + message);
        });

        fxLogger.log("Frontend logger initialized.");
        PreferencesLogger.logInfo("Backend logger bridge active.");


        // --- GUI ---
        this.commandLine.getCommandLine().setPrefColumnCount(prefs.getCmdColumns());
        this.commandLine.getCommandLine().setStyle("-fx-font-family: '" + prefs.getCmdFont() + "';");

        HBox commandLinePane = new HBox(10, this.commandLine.getCommandLineLabel(), this.commandLine.getCommandLine(), this.commandLine.getEnter());
        commandLinePane.setAlignment(Pos.BASELINE_LEFT);
        commandLinePane.setPadding(new Insets(5));

        HBox.setHgrow(this.commandLine.getCommandLine(), Priority.ALWAYS);

        // vertical pane to hold the menu bar and the command line
        VBox top = new VBox(
                this.menuBar.getMenuBar(),
                commandLinePane
        );

        this.commandLine.getOutputView().setPrefRowCount(prefs.getOutputLines());
        this.commandLine.getOutputView().setStyle("-fx-font-family: '" + prefs.getOutputFont() + "';");

        this.logArea.getLogView().setPrefRowCount(prefs.getLogLines());
        this.logArea.getLogView().setStyle("-fx-font-family: '" + prefs.getLogFont() + "';");

        BorderPane rootPane = new BorderPane();
        rootPane.setTop(top);
        rootPane.setCenter(new ScrollPane(this.commandLine.getOutputView()));
        rootPane.setBottom(new ScrollPane(this.logArea.getLogView()));

        // --- Calcolo larghezza fissa basata sulle colonne ---
        double charWidth = 8.0;
        double baseWidth = prefs.getCmdColumns() * charWidth + 120;

// --- Blocca larghezze coerenti ---
        this.commandLine.getCommandLine().setPrefColumnCount(prefs.getCmdColumns());
        this.commandLine.getCommandLine().setMinWidth(Region.USE_PREF_SIZE);
        this.commandLine.getCommandLine().setMaxWidth(Region.USE_PREF_SIZE);

        this.commandLine.getOutputView().setPrefWidth(baseWidth);
        this.commandLine.getOutputView().setMinWidth(baseWidth);
        this.commandLine.getOutputView().setMaxWidth(baseWidth);

        this.logArea.getLogView().setPrefWidth(baseWidth);
        this.logArea.getLogView().setMinWidth(baseWidth);
        this.logArea.getLogView().setMaxWidth(baseWidth);

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
