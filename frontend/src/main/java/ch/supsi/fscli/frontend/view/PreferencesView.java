package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.controller.ValidatedField;
import ch.supsi.fscli.frontend.util.FieldValidator;
import ch.supsi.fscli.frontend.util.FrontendGlobalVariables;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Map;
import java.util.ResourceBundle;
import java.util.Locale;

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

    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages", Locale.getDefault());

    public PreferencesView(Map<String, String> prefs) {
        initUI(prefs);
    }

    private void initUI(Map<String, String> prefs) {
        stage = new Stage();
        stage.setTitle(MESSAGES.getString("preferences.title"));

        grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setHgap(10);
        grid.setVgap(10);

        languageBox = new ComboBox<>();
        languageBox.getItems().addAll("en", "it");
        languageBox.setValue(prefs.get("language"));

        cmdFontBox = new ComboBox<>();
        cmdFontBox.getItems().addAll(FrontendGlobalVariables.SYSTEM_FONTS);
        cmdFontBox.setValue(prefs.get("cmdFont"));

        outputFontBox = new ComboBox<>(cmdFontBox.getItems());
        outputFontBox.setValue(prefs.get("outputFont"));

        logFontBox = new ComboBox<>(cmdFontBox.getItems());
        logFontBox.setValue(prefs.get("logFont"));

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

        saveBtn = new Button(MESSAGES.getString("preferences.save"));
        cancelBtn = new Button(MESSAGES.getString("preferences.cancel"));
        reloadBtn = new Button(MESSAGES.getString("preferences.reload"));

        int row = 0;
        grid.addRow(row++, new Label(MESSAGES.getString("preferences.language")), languageBox);
        grid.addRow(row++, new Label(MESSAGES.getString("preferences.cmdColumns")), cmdColumnsField.container());
        grid.addRow(row++, new Label(MESSAGES.getString("preferences.cmdFont")), cmdFontBox);
        grid.addRow(row++, new Label(MESSAGES.getString("preferences.outputLines")), outputLinesField.container());
        grid.addRow(row++, new Label(MESSAGES.getString("preferences.outputFont")), outputFontBox);
        grid.addRow(row++, new Label(MESSAGES.getString("preferences.logLines")), logLinesField.container());
        grid.addRow(row++, new Label(MESSAGES.getString("preferences.logFont")), logFontBox);
        grid.addRow(row++, saveBtn, reloadBtn, cancelBtn);

        Scene scene = new Scene(grid);
        scene.getStylesheets().add(getClass().getResource("/styles/preferences.css").toExternalForm());
        stage.setScene(scene);
    }

    public void setLanguage(String language) { languageBox.setValue(language); }
    public String getLanguage() { return languageBox.getValue(); }

    public void setCmdColumns(String value) { cmdColumnsField.field().setText(value); }
    public String getCmdColumns() { return cmdColumnsField.field().getText(); }
    public BooleanProperty cmdColumnsInvalidProperty() { return cmdColumnsField.invalid(); }

    public void setOutputLines(String value) { outputLinesField.field().setText(value); }
    public String getOutputLines() { return outputLinesField.field().getText(); }
    public BooleanProperty outputLinesInvalidProperty() { return outputLinesField.invalid(); }

    public void setLogLines(String value) { logLinesField.field().setText(value); }
    public String getLogLines() { return logLinesField.field().getText(); }
    public BooleanProperty logLinesInvalidProperty() { return logLinesField.invalid(); }

    public void setCmdFont(String font) { cmdFontBox.setValue(font); }
    public String getCmdFont() { return cmdFontBox.getValue(); }

    public void setOutputFont(String font) { outputFontBox.setValue(font); }
    public String getOutputFont() { return outputFontBox.getValue(); }

    public void setLogFont(String font) { logFontBox.setValue(font); }
    public String getLogFont() { return logFontBox.getValue(); }

    public void setOnSave(Runnable r) { saveBtn.setOnAction(e -> r.run()); }
    public void setOnCancel(Runnable r) { cancelBtn.setOnAction(e -> r.run()); }
    public void setOnReload(Runnable r) { reloadBtn.setOnAction(e -> r.run()); }

    public BooleanProperty saveBtnDisableProperty() { return saveBtn.disableProperty(); }

    public void show() { stage.show(); }
    public void close() { stage.close(); }
}
