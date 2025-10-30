package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.controller.ValidatedField;
import ch.supsi.fscli.frontend.util.FieldValidator;
import ch.supsi.fscli.frontend.util.FrontendGlobalVariables;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Map;

public class PreferencesView {

    public Stage stage;
    public GridPane grid;

    public ComboBox<String> languageBox;
    public ComboBox<String> cmdFontBox;
    public ComboBox<String> outputFontBox;
    public ComboBox<String> logFontBox;
    public ValidatedField cmdColumnsField;
    public ValidatedField outputLinesField;
    public ValidatedField logLinesField;
    public Button saveBtn;
    public Button cancelBtn;
    public Button reloadBtn;

    public PreferencesView(Map<String, String> prefs) {
        initUI(prefs);
    }

    private void initUI(Map<String, String> prefs) {
        stage = new Stage();
        stage.setTitle("Preferences");

        grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setHgap(10);
        grid.setVgap(10);

        // --- ComboBox lingua
        languageBox = new ComboBox<>();
        languageBox.getItems().addAll("en", "it", "de", "fr");
        languageBox.setValue(prefs.get("language"));

        // --- ComboBox font
        cmdFontBox = new ComboBox<>();
        cmdFontBox.getItems().addAll(FrontendGlobalVariables.SYSTEM_FONTS);
        cmdFontBox.setValue(prefs.get("cmdFont"));

        outputFontBox = new ComboBox<>(cmdFontBox.getItems());
        outputFontBox.setValue(prefs.get("outputFont"));

        logFontBox = new ComboBox<>(cmdFontBox.getItems());
        logFontBox.setValue(prefs.get("logFont"));

        // --- Campi numerici validati
        cmdColumnsField = new ValidatedField(
                Integer.parseInt(prefs.get("cmdColumns")),
                FrontendGlobalVariables.MIN_COLUMNS,
                FrontendGlobalVariables.MAX_COLUMNS
        );

        outputLinesField = new ValidatedField(
                Integer.parseInt(prefs.get("outputLines")),
                FrontendGlobalVariables.MIN_LINES,
                FrontendGlobalVariables.MAX_LINES
        );

        logLinesField = new ValidatedField(
                Integer.parseInt(prefs.get("logLines")),
                FrontendGlobalVariables.MIN_LINES,
                FrontendGlobalVariables.MAX_LINES
        );

        // --- Pulsanti
        saveBtn = new Button("Save");
        cancelBtn = new Button("Cancel");
        reloadBtn = new Button("Reload from disk");

        // --- Layout
        int row = 0;
        grid.addRow(row++, new Label("Language:"), languageBox);
        grid.addRow(row++, new Label("Command Columns:"), cmdColumnsField.container());
        grid.addRow(row++, new Label("Command Font:"), cmdFontBox);
        grid.addRow(row++, new Label("Output Lines:"), outputLinesField.container());
        grid.addRow(row++, new Label("Output Font:"), outputFontBox);
        grid.addRow(row++, new Label("Log Lines:"), logLinesField.container());
        grid.addRow(row++, new Label("Log Font:"), logFontBox);
        grid.addRow(row++, saveBtn, reloadBtn, cancelBtn);

        // --- Scene + CSS
        Scene scene = new Scene(grid);
        scene.getStylesheets().add(getClass().getResource("/styles/preferences.css").toExternalForm());
        stage.setScene(scene);
    }

    public void show() {
        stage.showAndWait();
    }
}
