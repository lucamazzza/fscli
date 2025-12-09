package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.i18n.FrontendMessageProvider;
import ch.supsi.fscli.frontend.controller.ValidatedField;
import ch.supsi.fscli.frontend.util.FrontendGlobalVariables;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Map;

public class PreferencesView {

    private static PreferencesView instance;

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


    public static PreferencesView getInstance(Map<String, String> prefs) {
        if (instance == null) {
            instance = new PreferencesView(prefs);
        } else {
            instance.updateValues(prefs);
        }
        return instance;
    }


    private PreferencesView(Map<String, String> prefs) {
        initUI(prefs);
    }


    private void initUI(Map<String, String> prefs) {

        stage = new Stage();
        stage.setTitle(FrontendMessageProvider.get("preferences.title"));

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

        saveBtn = new Button(FrontendMessageProvider.get("preferences.save"));
        cancelBtn = new Button(FrontendMessageProvider.get("preferences.cancel"));
        reloadBtn = new Button(FrontendMessageProvider.get("preferences.reload"));

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


    public void updateValues(Map<String, String> prefs) {
        setLanguage(prefs.get("language"));
        setCmdColumns(prefs.get("cmdColumns"));
        setOutputLines(prefs.get("outputLines"));
        setLogLines(prefs.get("logLines"));
        setCmdFont(prefs.get("cmdFont"));
        setOutputFont(prefs.get("outputFont"));
        setLogFont(prefs.get("logFont"));
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
