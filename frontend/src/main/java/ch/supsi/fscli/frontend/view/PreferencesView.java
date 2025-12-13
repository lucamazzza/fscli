package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.event.PreferencesEvent;
import ch.supsi.fscli.frontend.handler.PreferencesHandler;
import ch.supsi.fscli.frontend.i18n.FrontendMessageProvider;
import ch.supsi.fscli.frontend.listener.Listener;
import ch.supsi.fscli.frontend.util.ValidatedField;
import ch.supsi.fscli.frontend.util.FrontendGlobalVariables;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class PreferencesView implements View {

    private Stage stage;
    private GridPane grid;

    private ComboBox<String> languageBox;
    private ComboBox<String> cmdFontBox;
    private ComboBox<String> outputFontBox;
    private ComboBox<String> logFontBox;

    private ValidatedField cmdColumnsField;
    private ValidatedField outputLinesField;
    private ValidatedField logLinesField;

    private Button saveBtn;
    private Button cancelBtn;
    private Button reloadBtn;

    @Setter
    private PreferencesHandler preferencesHandler;

    @Getter
    private final Listener<PreferencesEvent> preferencesListener;

    private Map<String, String> originalPrefs;

    private static PreferencesView instance;

    public static PreferencesView getInstance() {
        if (instance == null) {
            instance = new PreferencesView();
        }
        return instance;
    }

    private PreferencesView() {
        preferencesListener = this::handlePreferencesEvent;
        initUI();
    }

    private void initUI() {
        stage = new Stage();
        stage.setTitle(FrontendMessageProvider.get("preferences.title"));
        stage.setResizable(false);

        grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setHgap(10);
        grid.setVgap(10);

        languageBox = new ComboBox<>();
        languageBox.getItems().addAll("en", "it");

        cmdFontBox = new ComboBox<>();
        cmdFontBox.getItems().addAll(FrontendGlobalVariables.SYSTEM_FONTS);

        outputFontBox = new ComboBox<>(cmdFontBox.getItems());
        logFontBox = new ComboBox<>(cmdFontBox.getItems());

        cmdColumnsField = new ValidatedField(
                FrontendGlobalVariables.DEFAULT_CMD_COLUMNS,
                FrontendGlobalVariables.MIN_COLUMNS,
                FrontendGlobalVariables.MAX_COLUMNS
        );

        outputLinesField = new ValidatedField(
                FrontendGlobalVariables.DEFAULT_OUTPUT_LINES,
                FrontendGlobalVariables.MIN_LINES,
                FrontendGlobalVariables.MAX_LINES
        );

        logLinesField = new ValidatedField(
                FrontendGlobalVariables.DEFAULT_LOG_LINES,
                FrontendGlobalVariables.MIN_LINES,
                FrontendGlobalVariables.MAX_LINES
        );

        saveBtn = new Button(FrontendMessageProvider.get("preferences.save"));
        cancelBtn = new Button(FrontendMessageProvider.get("preferences.cancel"));
        reloadBtn = new Button(FrontendMessageProvider.get("preferences.reload"));

        saveBtn.setOnAction(e -> onSave());
        cancelBtn.setOnAction(e -> close());
        reloadBtn.setOnAction(e -> onReload());

        saveBtn.disableProperty().bind(
                cmdColumnsField.invalid()
                        .or(outputLinesField.invalid())
                        .or(logLinesField.invalid())
        );

        int row = 0;
        grid.addRow(row++, new Label(FrontendMessageProvider.get("preferences.language")), languageBox);
        grid.addRow(row++, new Label(FrontendMessageProvider.get("preferences.cmdColumns")), cmdColumnsField.container());
        grid.addRow(row++, new Label(FrontendMessageProvider.get("preferences.cmdFont")), cmdFontBox);
        grid.addRow(row++, new Label(FrontendMessageProvider.get("preferences.outputLines")), outputLinesField.container());
        grid.addRow(row++, new Label(FrontendMessageProvider.get("preferences.outputFont")), outputFontBox);
        grid.addRow(row++, new Label(FrontendMessageProvider.get("preferences.logLines")), logLinesField.container());
        grid.addRow(row++, new Label(FrontendMessageProvider.get("preferences.logFont")), logFontBox);
        grid.addRow(row++, saveBtn, reloadBtn, cancelBtn);

        Scene scene = new Scene(grid);
        scene.getStylesheets().add(
                getClass().getResource("/styles/preferences.css").toExternalForm()
        );

        stage.setScene(scene);
    }

    @Override
    public void init() {
        if (preferencesHandler != null) {
            originalPrefs = preferencesHandler.load();
            updateValues(originalPrefs);
        }
    }

    private void handlePreferencesEvent(PreferencesEvent event) {
        if (event == null || event.error() == null) return;

        switch (event.error()) {
            case PREFERENCES_LOADED -> {
                // Event notifying that preferences were loaded - don't reload, just acknowledge
            }
            case PREFERENCES_SAVED -> {
                showRestartAlert();
                close();
            }
            case PREFERENCES_SAVE_FAILED -> {
                showErrorAlert();
            }
        }
    }

    private void updateValues(Map<String, String> prefs) {
        if (prefs == null) return;
        
        languageBox.setValue(prefs.get("language"));
        cmdColumnsField.field().setText(prefs.get("cmdColumns"));
        outputLinesField.field().setText(prefs.get("outputLines"));
        logLinesField.field().setText(prefs.get("logLines"));
        cmdFontBox.setValue(prefs.get("cmdFont"));
        outputFontBox.setValue(prefs.get("outputFont"));
        logFontBox.setValue(prefs.get("logFont"));
    }

    private void onSave() {
        if (preferencesHandler == null) return;

        Map<String, String> newPrefs = Map.of(
                "language", languageBox.getValue(),
                "cmdColumns", cmdColumnsField.field().getText(),
                "outputLines", outputLinesField.field().getText(),
                "logLines", logLinesField.field().getText(),
                "cmdFont", cmdFontBox.getValue(),
                "outputFont", outputFontBox.getValue(),
                "logFont", logFontBox.getValue()
        );

        if (!newPrefs.equals(originalPrefs)) {
            preferencesHandler.edit(newPrefs);
            originalPrefs = newPrefs;
        } else {
            close();
        }
    }

    private void onReload() {
        if (preferencesHandler != null) {
            originalPrefs = preferencesHandler.load();
            updateValues(originalPrefs);
        }
    }

    private void showRestartAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(FrontendMessageProvider.get("alert.information"));
        alert.setHeaderText(null);
        alert.setContentText(FrontendMessageProvider.get("alert.applyPreferences"));
        alert.showAndWait();
    }

    private void showErrorAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(FrontendMessageProvider.get("alert.error"));
        alert.setHeaderText(null);
        alert.setContentText(FrontendMessageProvider.get("alert.preferencesError"));
        alert.showAndWait();
    }

    public void show() {
        init();
        stage.show();
    }

    public void close() {
        stage.close();
    }
}
