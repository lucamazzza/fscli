package ch.supsi.fscli.frontend;

import ch.supsi.fscli.backend.controller.PreferencesController;
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

import java.util.Locale;
import java.util.ResourceBundle;

public class MainFx extends Application {
    private final String applicationTitle;
    private final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages", Locale.getDefault());

    // VIEWS
    private final MenuBarView menuBar;
    private final CommandLineView commandLine;
    private final LogAreaView logArea;

    public MainFx() {
        // titolo preso da file di risorse
        this.applicationTitle = MESSAGES.getString("mainfx.title");

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

        PreferencesController backendController = new PreferencesController();
        UserPreferences prefs = backendController.getPreferences();

        FxLogger fxLogger = FxLogger.getInstance();
        fxLogger.setLogArea(this.logArea.getLogView());
        fxLogger.setLogAreaRowCount(prefs.getLogLines());

        PreferencesLogger.setExternalListener((level, message) -> {
            fxLogger.log("[" + level + "] " + message);
        });

        fxLogger.log(MESSAGES.getString("logger.frontendInitialized"));
        PreferencesLogger.logInfo(MESSAGES.getString("logger.backendBridgeActive"));

        // GUI
        this.commandLine.getCommandLine().setPrefColumnCount(prefs.getCmdColumns());
        this.commandLine.getCommandLine().setStyle("-fx-font-family: '" + prefs.getCmdFont() + "';");

        HBox commandLinePane = new HBox(10, this.commandLine.getCommandLineLabel(), this.commandLine.getCommandLine(), this.commandLine.getEnter());
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
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
