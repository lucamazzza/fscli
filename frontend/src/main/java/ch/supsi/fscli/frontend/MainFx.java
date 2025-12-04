package ch.supsi.fscli.frontend;

import ch.supsi.fscli.backend.controller.FileSystemPersistenceController;
import ch.supsi.fscli.backend.controller.PreferencesController;
import ch.supsi.fscli.backend.core.UserPreferences;
import ch.supsi.fscli.frontend.controller.FileSystemController;
import ch.supsi.fscli.frontend.event.CommandLineEvent;
import ch.supsi.fscli.frontend.event.EventManager;
import ch.supsi.fscli.frontend.event.FileSystemEvent;
import ch.supsi.fscli.frontend.util.*;
import ch.supsi.fscli.frontend.i18n.FrontendMessageProvider;
import ch.supsi.fscli.frontend.util.FxLogger;
import ch.supsi.fscli.backend.util.PreferencesLogger;
import ch.supsi.fscli.frontend.model.FileSystemModel;
import ch.supsi.fscli.frontend.view.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Locale;

public class MainFx extends Application {
    private final String applicationTitle;
    // VIEWS
    private final MenuBarView menuBar;
    private final CommandLineView commandLine;
    private final LogAreaView logArea;

    public MainFx() {
        this.applicationTitle = FrontendMessageProvider.get("mainfx.title");
        this.menuBar = MenuBarView.getInstance();
        this.commandLine = CommandLineView.getInstance();
        this.logArea = LogAreaView.getInstance();

        this.logArea.init();
        FxLogger fxLogger = FxLogger.getInstance();
        fxLogger.setLogArea(this.logArea.getLogView());

        PreferencesLogger.setExternalListener((level, message) -> {
            fxLogger.log("[" + level + "] " + message);
        });

        PreferencesController backendController =
                ch.supsi.fscli.backend.controller.PreferencesController.getInstance();
        String lang = backendController.getPreferences().getLanguage();
        Locale locale = lang.equalsIgnoreCase("en") ? Locale.ENGLISH : Locale.ITALIAN;
        FrontendMessageProvider.setLocale(locale);

        // BACKEND STUFF
        FileSystemPersistenceController backendPersistenceController = new FileSystemPersistenceController();

        // EVENT MANAGERS
        EventManager<FileSystemEvent> fileSystemEventManager = new EventManager<>();
        EventManager<CommandLineEvent> commandLineEventManager = new EventManager<>();

        // EVENT MANAGERS INIT
        fileSystemEventManager.addListener(this.menuBar.getFileSystemListener());
        fileSystemEventManager.addListener(this.logArea.getFileSystemListener());
        fileSystemEventManager.addListener(this.commandLine.getFileSystemListener());
        commandLineEventManager.addListener(this.commandLine.getCommandLineListener());
        commandLineEventManager.addListener(this.logArea.getCommandLineListener());

        // MODELS
        FileSystemModel fileSystemModel = FileSystemModel.getInstance();

        // MODELS INIT
        fileSystemModel.setFileSystemEventManager(fileSystemEventManager);
        fileSystemModel.setCommandLineEventManager(commandLineEventManager);
        fileSystemModel.setBackendPersistenceController(backendPersistenceController);

        // CONTROLLERS
        FileSystemController fileSystemController = FileSystemController.getInstance();

        // CONTROLLERS INIT
        fileSystemController.setFileSystemModel(fileSystemModel);

        // VIEWS INIT
        this.menuBar.setFileSystemEventHandler(fileSystemController);
        this.commandLine.setCommandLineEventHandler(fileSystemController);

        this.menuBar.init();
        this.commandLine.init();
    }

    @Override
    public void start(Stage primaryStage) {
        // --- CARICA PREFERENZE ---
        PreferencesController backendController =
                ch.supsi.fscli.backend.controller.PreferencesController.getInstance();
        UserPreferences prefs = backendController.getPreferences();

        FxLogger fxLogger = FxLogger.getInstance();
        fxLogger.setLogAreaRowCount(prefs.getLogLines());

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
