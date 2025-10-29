package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.backend.application.PreferencesService;
import ch.supsi.fscli.backend.business.UserPreferences;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.*;

import java.util.Map;

public class PreferencesController implements PreferencesHandler {

    private final PreferencesService service;

    public PreferencesController() {
        this.service = new PreferencesService();
    }

    @Override
    public void edit(Map<String, String> settings) {

    }

    @Override
    public Map<String, String> load() {
        UserPreferences prefs = service.getCurrentPrefs();
        return Map.of(
                "language", prefs.getLanguage(),
                "cmdColumns", String.valueOf(prefs.getCmdColumns()),
                "outputLines", String.valueOf(prefs.getOutputLines()),
                "logLines", String.valueOf(prefs.getLogLines()),
                "cmdFont", prefs.getCmdFont(),
                "outputFont", prefs.getOutputFont(),
                "logFont", prefs.getLogFont()
        );
    }

    public void openPreferencesWindow() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Preferences");

        UserPreferences prefs = service.getCurrentPrefs();

        TextField languageField = new TextField(prefs.getLanguage());
        TextField columnsField = new TextField(String.valueOf(prefs.getCmdColumns()));
        TextField outputLinesField = new TextField(String.valueOf(prefs.getOutputLines()));
        TextField logLinesField = new TextField(String.valueOf(prefs.getLogLines()));
        TextField cmdFontField = new TextField(prefs.getCmdFont());
        TextField outputFontField = new TextField(prefs.getOutputFont());
        TextField logFontField = new TextField(prefs.getLogFont());

        Button saveBtn = new Button("Save");
        Button cancelBtn = new Button("Cancel");
        Button reloadBtn = new Button("Reload from disk");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setHgap(10);
        grid.setVgap(10);

        int row = 0;
        grid.addRow(row++, new Label("Language:"), languageField);
        grid.addRow(row++, new Label("Command Columns:"), columnsField);
        grid.addRow(row++, new Label("Output Lines:"), outputLinesField);
        grid.addRow(row++, new Label("Log Lines:"), logLinesField);
        grid.addRow(row++, new Label("Command Font:"), cmdFontField);
        grid.addRow(row++, new Label("Output Font:"), outputFontField);
        grid.addRow(row++, new Label("Log Font:"), logFontField);

        grid.addRow(row++, saveBtn, cancelBtn);
        grid.addRow(row++, reloadBtn);

        saveBtn.setOnAction(e -> {
            try {
                service.updatePreference(p -> {
                    p.setLanguage(languageField.getText());
                    p.setCmdColumns(Integer.parseInt(columnsField.getText()));
                    p.setOutputLines(Integer.parseInt(outputLinesField.getText()));
                    p.setLogLines(Integer.parseInt(logLinesField.getText()));
                    p.setCmdFont(cmdFontField.getText());
                    p.setOutputFont(outputFontField.getText());
                    p.setLogFont(logFontField.getText());
                });
                stage.close();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Error saving preferences: " + ex.getMessage()).showAndWait();
            }
        });

        cancelBtn.setOnAction(e -> stage.close());

        reloadBtn.setOnAction(e -> {
            service.reload();
            UserPreferences reloaded = service.getCurrentPrefs();
            languageField.setText(reloaded.getLanguage());
            columnsField.setText(String.valueOf(reloaded.getCmdColumns()));
            outputLinesField.setText(String.valueOf(reloaded.getOutputLines()));
            logLinesField.setText(String.valueOf(reloaded.getLogLines()));
            cmdFontField.setText(reloaded.getCmdFont());
            outputFontField.setText(reloaded.getOutputFont());
            logFontField.setText(reloaded.getLogFont());
        });

        stage.setScene(new Scene(grid));
        stage.showAndWait();
    }
}