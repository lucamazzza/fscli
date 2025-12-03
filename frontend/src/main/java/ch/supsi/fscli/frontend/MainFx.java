package ch.supsi.fscli.frontend;

import ch.supsi.fscli.backend.controller.PreferencesController;
import ch.supsi.fscli.backend.core.UserPreferences;
import ch.supsi.fscli.frontend.i18n.FrontendMessageProvider;
import ch.supsi.fscli.frontend.util.FxLogger;
import ch.supsi.fscli.backend.util.PreferencesLogger;
import ch.supsi.fscli.frontend.controller.FileSystemController;
import ch.supsi.fscli.frontend.event.FileEventManager;
import ch.supsi.fscli.frontend.model.FileSystem;
import ch.supsi.fscli.frontend.view.CommandLineView;
import ch.supsi.fscli.frontend.view.LogAreaView;
import ch.supsi.fscli.frontend.view.MenuBarView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class MainFx extends Application {

    private ResourceBundle MESSAGES;

    // VIEWS
    private final MenuBarView menuBar;
    private final CommandLineView commandLine;
    private final LogAreaView logArea;

    public MainFx() {


        this.menuBar = MenuBarView.getInstance();
        this.commandLine = CommandLineView.getInstance();
        this.logArea = LogAreaView.getInstance();
    }

    @Override
    public void start(Stage primaryStage) {

        // --- Backend preferences ---
        PreferencesController backendController = new PreferencesController();
        UserPreferences prefs = backendController.getPreferences();

        // --- Imposta la lingua ---
        Locale locale = new Locale(prefs.getLanguage());
        Locale.setDefault(locale);
        FrontendMessageProvider.setLocale(locale);

        // --- Inizializza le views ---
        this.menuBar.init();
        this.commandLine.init();
        this.logArea.init();

        // --- Gestione eventi ---
        FileEventManager fileEventManager = new FileEventManager();
        fileEventManager.addListener(this.menuBar);

        // --- FileSystem e Controller ---
        FileSystem fileSystem = FileSystem.getInstance();
        fileSystem.setEventManager(fileEventManager);

        FileSystemController fileSystemController = FileSystemController.getInstance();
        fileSystemController.setModel(fileSystem);

        menuBar.setController(fileSystemController);

        // --- Logger ---
        FxLogger fxLogger = FxLogger.getInstance();
        fxLogger.setLogArea(this.logArea.getLogView());
        fxLogger.setLogAreaRowCount(prefs.getLogLines());

        PreferencesLogger.setExternalListener((level, message) -> {
            fxLogger.log("[" + level + "] " + message);
        });

        fxLogger.log(FrontendMessageProvider.get("logger.frontendInitialized"));
        PreferencesLogger.logInfo(FrontendMessageProvider.get("logger.backendBridgeActive"));

        // --- Impostazioni Command Line ---
        this.commandLine.getCommandLine().setPrefColumnCount(prefs.getCmdColumns());
        this.commandLine.getCommandLine().setStyle("-fx-font-family: '" + prefs.getCmdFont() + "';");

        HBox commandLinePane = new HBox(10,
                this.commandLine.getCommandLineLabel(),
                this.commandLine.getCommandLine(),
                this.commandLine.getEnter()
        );
        commandLinePane.setAlignment(Pos.BASELINE_LEFT);
        commandLinePane.setPadding(new Insets(5));
        HBox.setHgrow(this.commandLine.getCommandLine(), Priority.ALWAYS);

        VBox top = new VBox(this.menuBar.getMenuBar(), commandLinePane);

        this.commandLine.getOutputView().setPrefRowCount(prefs.getOutputLines());
        this.commandLine.getOutputView().setStyle("-fx-font-family: '" + prefs.getOutputFont() + "';");

        this.logArea.getLogView().setPrefRowCount(prefs.getLogLines());
        this.logArea.getLogView().setStyle("-fx-font-family: '" + prefs.getLogFont() + "';");

        BorderPane rootPane = new BorderPane();
        rootPane.setTop(top);
        rootPane.setCenter(new ScrollPane(this.commandLine.getOutputView()));
        rootPane.setBottom(new ScrollPane(this.logArea.getLogView()));

        double charWidth = 8.0;
        double baseWidth = prefs.getCmdColumns() * charWidth + 120;

        this.commandLine.getCommandLine().setPrefColumnCount(prefs.getCmdColumns());
        this.commandLine.getCommandLine().setMinWidth(Region.USE_PREF_SIZE);
        this.commandLine.getCommandLine().setMaxWidth(Region.USE_PREF_SIZE);

        this.commandLine.getOutputView().setPrefWidth(baseWidth);
        this.commandLine.getOutputView().setMinWidth(baseWidth);
        this.commandLine.getOutputView().setMaxWidth(baseWidth);

        this.logArea.getLogView().setPrefWidth(baseWidth);
        this.logArea.getLogView().setMinWidth(baseWidth);
        this.logArea.getLogView().setMaxWidth(baseWidth);

        // --- Imposta la finestra principale ---
        primaryStage.setResizable(false);
        Scene mainScene = new Scene(rootPane);
        primaryStage.setTitle(FrontendMessageProvider.get("mainfx.title"));
        primaryStage.setScene(mainScene);
        primaryStage.setHeight(80 + prefs.getOutputLines() * 20 + prefs.getLogLines() * 15);
        primaryStage.show();
    }


    public static void main(String[] args) {
        Application.launch(args);
    }
}
