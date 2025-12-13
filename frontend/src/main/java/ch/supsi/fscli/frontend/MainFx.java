package ch.supsi.fscli.frontend;

import ch.supsi.fscli.backend.controller.FileSystemPersistenceController;
import ch.supsi.fscli.backend.controller.PreferencesController;
import ch.supsi.fscli.backend.core.UserPreferences;
import ch.supsi.fscli.frontend.controller.AboutController;
import ch.supsi.fscli.frontend.controller.FileSystemController;
import ch.supsi.fscli.frontend.event.AboutEvent;
import ch.supsi.fscli.frontend.event.CommandLineEvent;
import ch.supsi.fscli.frontend.event.EventManager;
import ch.supsi.fscli.frontend.event.FileSystemEvent;
import ch.supsi.fscli.frontend.event.PreferencesEvent;
import ch.supsi.fscli.frontend.i18n.FrontendMessageProvider;
import ch.supsi.fscli.frontend.model.ApplicationModel;
import ch.supsi.fscli.frontend.model.FileSystemModel;
import ch.supsi.fscli.frontend.util.FxLogger;
import ch.supsi.fscli.backend.util.PreferencesLogger;
import ch.supsi.fscli.frontend.view.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import ch.supsi.fscli.backend.di.BackendInjector;
import ch.supsi.fscli.frontend.util.*;

import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainFx extends Application {

    private final String applicationTitle;
    private final MenuBarView menuBar;
    private final CommandLineView commandLine;
    private final LogAreaView logArea;

    private static class Captured {
        String level;
        String message;
    }

    public MainFx() {
        // Initialize Guice DI
        BackendInjector.initialize();
        this.logArea = LogAreaView.getInstance();
        this.logArea.init();

        FxLogger fxLogger = FxLogger.getInstance();
        fxLogger.setLogArea(this.logArea.getLogView());

        Captured captured = new Captured();

        // Listener TEMPORANEO per catturare il primo log
        PreferencesLogger.setExternalListener((level, message) -> {
            if (captured.message == null) {
                captured.level = level;
                captured.message = message;
            }
        });

        PreferencesController backendController = BackendInjector.getInstance(PreferencesController.class);
        UserPreferences prefs = backendController.getPreferences(); // già sanitizzate


        // Imposta lingua
        Locale locale = prefs.getLanguage().equalsIgnoreCase("en") ? Locale.ENGLISH : Locale.ITALIAN;
        FrontendMessageProvider.setLocale(locale);

        // Log del primo messaggio con prefisso localizzato
        if (captured.message != null && captured.level != null) {
            String raw = captured.message;
            boolean created = raw.toLowerCase().contains("nessun file") ||
                    raw.toLowerCase().contains("no preferences file") ||
                    raw.toLowerCase().contains("created a new") ||
                    raw.toLowerCase().contains("creato uno nuovo") ||
                    raw.toLowerCase().contains("creato un nuovo");

            String path = raw.trim();
            Matcher driveMatcher = Pattern.compile("[A-Za-z]:").matcher(raw);
            if (driveMatcher.find()) {
                path = raw.substring(driveMatcher.start()).trim().replaceAll("[\\]\\)\\r\\n]+$", "");
            }

            String key = created ? "preferences.created" : "preferences.loaded";
            String localizedPrefix;
            try {
                localizedPrefix = FrontendMessageProvider.get(key);
            } catch (Exception e) {
                localizedPrefix = null;
            }
            if (localizedPrefix == null || localizedPrefix.isBlank()) {
                localizedPrefix = created
                        ? (locale.equals(Locale.ENGLISH) ? "No preferences file found, created a new one at" : "Nessun file di preferenze trovato, creato uno nuovo a")
                        : (locale.equals(Locale.ENGLISH) ? "Preferences loaded from" : "Preferenze caricate da");
            }

            fxLogger.log("[" + captured.level + "] " + localizedPrefix + ": " + path);
        }

        // Listener definitivo per i log delle preferenze
        PreferencesLogger.setExternalListener((level, message) -> fxLogger.log("[" + level + "] " + message));

        // Inizializza le view
        this.menuBar = MenuBarView.getInstance();
        this.commandLine = CommandLineView.getInstance();
        this.applicationTitle = FrontendMessageProvider.get("mainfx.title");

        FileSystemPersistenceController backendPersistenceController = BackendInjector.getInstance(FileSystemPersistenceController.class);

        EventManager<FileSystemEvent> fileSystemEventManager = new EventManager<>();
        EventManager<CommandLineEvent> commandLineEventManager = new EventManager<>();
        EventManager<AboutEvent> aboutEventManager = new EventManager<>();
        EventManager<PreferencesEvent> preferencesEventManager = new EventManager<>();

        fileSystemEventManager.addListener(this.menuBar.getFileSystemListener());
        fileSystemEventManager.addListener(this.logArea.getFileSystemListener());
        fileSystemEventManager.addListener(this.commandLine.getFileSystemListener());

        commandLineEventManager.addListener(this.commandLine.getCommandLineListener());
        commandLineEventManager.addListener(this.logArea.getCommandLineListener());

        aboutEventManager.addListener(menuBar.getAboutListener());

        preferencesEventManager.addListener(this.logArea.getPreferencesListener());

        ApplicationModel appModel = ApplicationModel.getInstance();
        appModel.setAboutEventPublisher(aboutEventManager);
        
        FileSystemModel fileSystemModel = FileSystemModel.getInstance();
        fileSystemModel.setFileSystemEventManager(fileSystemEventManager);
        fileSystemModel.setCommandLineEventManager(commandLineEventManager);
        fileSystemModel.setBackendPersistenceController(backendPersistenceController);

        ch.supsi.fscli.frontend.model.PreferencesModel preferencesModel = ch.supsi.fscli.frontend.model.PreferencesModel.getInstance();
        preferencesModel.setPreferencesEventPublisher(preferencesEventManager);

        FileSystemController fileSystemController = FileSystemController.getInstance();
        fileSystemController.setModel(fileSystemModel);
        
        AboutController aboutController = AboutController.getInstance();
        aboutController.setModel(appModel);

        ch.supsi.fscli.frontend.controller.PreferencesController preferencesController = ch.supsi.fscli.frontend.controller.PreferencesController.getInstance();
        preferencesController.setModel(preferencesModel);

        this.menuBar.setFileSystemEventHandler(fileSystemController);
        this.menuBar.setAboutEventHandler(aboutController);
        this.menuBar.setPreferencesHandler(preferencesController);
        this.menuBar.setPreferencesEventManager(preferencesEventManager);
        this.commandLine.setCommandLineEventHandler(fileSystemController);

        this.menuBar.init();
        this.commandLine.init();
    }

    @Override
    public void start(Stage primaryStage) {
        PreferencesController backendController = BackendInjector.getInstance(PreferencesController.class);
        UserPreferences prefs = backendController.getPreferences();

        FxLogger fxLogger = FxLogger.getInstance();
        fxLogger.setLogAreaRowCount(prefs.getLogLines());

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

        FileSystemController fsController = FileSystemController.getInstance();
        primaryStage.setOnCloseRequest(event -> {
            if (fsController != null && fsController.hasUnsavedChanges()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle(FrontendMessageProvider.get("alert.unsavedChanges"));
                alert.setHeaderText(FrontendMessageProvider.get("alert.unsavedChanges"));
                alert.setContentText(FrontendMessageProvider.get("alert.unsavedChangesMessage"));
                alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isEmpty() || result.get() != ButtonType.YES) {
                    event.consume(); // Cancel the close request
                }
            }
        });

        primaryStage.show();

        if (prefs.wasClamped()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(FrontendMessageProvider.get("preferences.warning.title"));
            alert.setHeaderText(null);
            alert.setContentText(FrontendMessageProvider.get("preferences.warning.message"));
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
