package ch.supsi.fscli.frontend;

import ch.supsi.fscli.backend.controller.FileSystemPersistenceController;
import ch.supsi.fscli.backend.controller.PreferencesController;
import ch.supsi.fscli.backend.core.UserPreferences;
import ch.supsi.fscli.frontend.controller.FileSystemController;
import ch.supsi.fscli.frontend.event.CommandLineEvent;
import ch.supsi.fscli.frontend.event.EventManager;
import ch.supsi.fscli.frontend.event.FileSystemEvent;
import ch.supsi.fscli.frontend.i18n.FrontendMessageProvider;
import ch.supsi.fscli.frontend.model.FileSystemModel;
import ch.supsi.fscli.frontend.util.FxLogger;
import ch.supsi.fscli.backend.util.PreferencesLogger;
import ch.supsi.fscli.frontend.view.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainFx extends Application {
    private final String applicationTitle;
    private final MenuBarView menuBar;
    private final CommandLineView commandLine;
    private final LogAreaView logArea;

    public MainFx() {
        // 0) Prepara la logArea e l'FxLogger subito
        this.logArea = LogAreaView.getInstance();
        this.logArea.init();

        FxLogger fxLogger = FxLogger.getInstance();
        fxLogger.setLogArea(this.logArea.getLogView());

        // 1) Listener TEMPORANEO per catturare il primo messaggio prodotto dal backend
        AtomicReference<String> capturedLevel = new AtomicReference<>(null);
        AtomicReference<String> capturedMessage = new AtomicReference<>(null);

        PreferencesLogger.setExternalListener((level, message) -> {
            if (capturedMessage.get() == null) {
                capturedLevel.set(level);
                capturedMessage.set(message);
            }
            // non inoltriamo nulla ora (evitiamo duplicati o stampa su terminale)
        });

        // 2) Carichiamo le preferenze (il backend potrebbe creare un nuovo file e loggare)
        PreferencesController backendController = PreferencesController.getInstance();
        UserPreferences prefs = backendController.getPreferences();

        // 3) Imposta la lingua dalle preferenze (ora che prefs è disponibile)
        Locale locale = prefs.getLanguage().equalsIgnoreCase("en") ? Locale.ENGLISH : Locale.ITALIAN;
        FrontendMessageProvider.setLocale(locale);

        // 4) Costruisci e pubblica un messaggio localizzato in base al tipo di log catturato
        String lvl = capturedLevel.get();
        String raw = capturedMessage.get();
        if (raw != null && lvl != null) {
            // Determine if it's the "created new preferences" message (it/eng)
            String rawLower = raw.toLowerCase();
            boolean created = rawLower.contains("nessun file") ||
                    rawLower.contains("no preferences file") ||
                    rawLower.contains("created a new") ||
                    rawLower.contains("creato uno nuovo") ||
                    rawLower.contains("creato un nuovo");

            // Extract path robustly: look for drive-letter pattern (e.g. "C:") and take substring from there
            String path = raw.trim();
            Matcher driveMatcher = Pattern.compile("[A-Za-z]:").matcher(raw);
            if (driveMatcher.find()) {
                int start = driveMatcher.start();
                path = raw.substring(start).trim();
                // Strip trailing punctuation or brackets if present
                path = path.replaceAll("[\\]\\)\\r\\n]+$", "");
            } else {
                // fallback: use whole message (trimmed)
                path = raw.trim();
            }

            // Get localized prefix depending on event type
            String key = created ? "preferences.created" : "preferences.loaded";
            String localizedPrefix;
            try {
                localizedPrefix = FrontendMessageProvider.get(key);
            } catch (Exception e) {
                localizedPrefix = null;
            }

            if (localizedPrefix == null || localizedPrefix.isBlank()) {
                // fallback testo semplice nelle due lingue
                if (created) {
                    localizedPrefix = locale.equals(Locale.ENGLISH)
                            ? "No preferences file found, created a new one at"
                            : "Nessun file di preferenze trovato, creato uno nuovo a";
                } else {
                    localizedPrefix = locale.equals(Locale.ENGLISH)
                            ? "Preferences loaded from"
                            : "Preferenze caricate da";
                }
            }

            fxLogger.log("[" + lvl + "] " + localizedPrefix + ": " + path);
        }

        // 5) Ora sostituiamo il listener temporaneo con quello definitivo
        PreferencesLogger.setExternalListener((level, message) -> fxLogger.log("[" + level + "] " + message));

        // 6) Inizializza le view che usano testi (dopo aver settato il provider di lingua)
        this.menuBar = MenuBarView.getInstance();
        this.commandLine = CommandLineView.getInstance();

        this.applicationTitle = FrontendMessageProvider.get("mainfx.title");

        // 7) Backend controller e event manager
        FileSystemPersistenceController backendPersistenceController = new FileSystemPersistenceController();

        EventManager<FileSystemEvent> fileSystemEventManager = new EventManager<>();
        EventManager<CommandLineEvent> commandLineEventManager = new EventManager<>();

        fileSystemEventManager.addListener(this.menuBar.getFileSystemListener());
        fileSystemEventManager.addListener(this.logArea.getFileSystemListener());
        fileSystemEventManager.addListener(this.commandLine.getFileSystemListener());

        commandLineEventManager.addListener(this.commandLine.getCommandLineListener());
        commandLineEventManager.addListener(this.logArea.getCommandLineListener());

        // 8) Model
        FileSystemModel fileSystemModel = FileSystemModel.getInstance();
        fileSystemModel.setFileSystemEventManager(fileSystemEventManager);
        fileSystemModel.setCommandLineEventManager(commandLineEventManager);
        fileSystemModel.setBackendPersistenceController(backendPersistenceController);

        // 9) Controller
        FileSystemController fileSystemController = FileSystemController.getInstance();
        fileSystemController.setFileSystemModel(fileSystemModel);

        // 10) Init views
        this.menuBar.setFileSystemEventHandler(fileSystemController);
        this.commandLine.setCommandLineEventHandler(fileSystemController);

        this.menuBar.init();
        this.commandLine.init();
    }

    @Override
    public void start(Stage primaryStage) {
        PreferencesController backendController = PreferencesController.getInstance();
        UserPreferences prefs = backendController.getPreferences();

        FxLogger fxLogger = FxLogger.getInstance();
        fxLogger.setLogAreaRowCount(prefs.getLogLines());

        // GUI
        this.commandLine.getCommandLine().setPrefColumnCount(prefs.getCmdColumns());
        this.commandLine.getCommandLine().setStyle("-fx-font-family: '" + prefs.getCmdFont() + "';");

        HBox commandLinePane = new HBox(
                10,
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

        // Larghezze
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
