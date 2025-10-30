package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.backend.application.PreferencesService;
import ch.supsi.fscli.backend.business.UserPreferences;
import ch.supsi.fscli.frontend.util.FieldValidator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.scene.control.TextFormatter;

import java.util.Map;
import java.util.function.UnaryOperator;

public class PreferencesController implements PreferencesHandler {

    private final PreferencesService service;

    public PreferencesController() {
        this.service = new PreferencesService();
    }

    @Override
    public void edit(Map<String, String> settings) {}

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

        // --- ComboBox lingua
        ComboBox<String> languageBox = new ComboBox<>();
        languageBox.getItems().addAll("en", "it", "de", "fr");
        languageBox.setValue(prefs.getLanguage());

        // --- ComboBox font
        ComboBox<String> fontBox = new ComboBox<>();
        fontBox.getItems().addAll("Monospaced", "SansSerif", "Serif", "Consolas");
        fontBox.setValue(prefs.getCmdFont());

        ComboBox<String> outputFontBox = new ComboBox<>(fontBox.getItems());
        outputFontBox.setValue(prefs.getOutputFont());

        ComboBox<String> logFontBox = new ComboBox<>(fontBox.getItems());
        logFontBox.setValue(prefs.getLogFont());

        // --- Campi numerici validati
        ValidatedField columnsField = createValidatedIntField(
                prefs.getCmdColumns(),
                UserPreferences.MIN_COLUMNS,
                UserPreferences.MAX_COLUMNS
        );

        ValidatedField outputLinesField = createValidatedIntField(
                prefs.getOutputLines(),
                UserPreferences.MIN_LINES,
                UserPreferences.MAX_LINES
        );

        ValidatedField logLinesField = createValidatedIntField(
                prefs.getLogLines(),
                UserPreferences.MIN_LINES,
                UserPreferences.MAX_LINES
        );

        // --- Pulsanti
        Button saveBtn = new Button("Save");
        Button cancelBtn = new Button("Cancel");
        Button reloadBtn = new Button("Reload from disk");

        // --- Layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setHgap(10);
        grid.setVgap(10);

        int row = 0;
        grid.addRow(row++, new Label("Language:"), languageBox);
        grid.addRow(row++, new Label("Command Columns:"), columnsField.container());
        grid.addRow(row++, new Label("Command Font:"), fontBox);
        grid.addRow(row++, new Label("Output Lines:"), outputLinesField.container());
        grid.addRow(row++, new Label("Output Font:"), outputFontBox);
        grid.addRow(row++, new Label("Log Lines:"), logLinesField.container());
        grid.addRow(row++, new Label("Log Font:"), logFontBox);
        grid.addRow(row++, saveBtn, reloadBtn, cancelBtn);

        // --- Disabilita Save se ci sono errori
        saveBtn.disableProperty().bind(
                Bindings.or(columnsField.invalid(),
                        Bindings.or(outputLinesField.invalid(), logLinesField.invalid()))
        );

        // --- Azioni pulsanti
        saveBtn.setOnAction(e -> {
            try {
                service.updatePreference(p -> {
                    p.setLanguage(languageBox.getValue());
                    p.setCmdColumns(Integer.parseInt(columnsField.field().getText()));
                    p.setOutputLines(Integer.parseInt(outputLinesField.field().getText()));
                    p.setLogLines(Integer.parseInt(logLinesField.field().getText()));
                    p.setCmdFont(fontBox.getValue());
                    p.setOutputFont(outputFontBox.getValue());
                    p.setLogFont(logFontBox.getValue());
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
            loadPrefs(reloaded, languageBox, columnsField, fontBox, outputLinesField, outputFontBox, logLinesField, logFontBox);
        });

        // --- Scena + CSS
        Scene scene = new Scene(grid);
        scene.getStylesheets().add(getClass().getResource("/styles/preferences.css").toExternalForm());
        stage.setScene(scene);
        stage.showAndWait();
    }

    // --- Helpers ---

    private void loadPrefs(UserPreferences prefs,
                           ComboBox<String> languageBox,
                           ValidatedField columnsField,
                           ComboBox<String> fontBox,
                           ValidatedField outputLinesField,
                           ComboBox<String> outputFontBox,
                           ValidatedField logLinesField,
                           ComboBox<String> logFontBox) {

        languageBox.setValue(prefs.getLanguage());
        columnsField.field().setText(String.valueOf(prefs.getCmdColumns()));
        outputLinesField.field().setText(String.valueOf(prefs.getOutputLines()));
        logLinesField.field().setText(String.valueOf(prefs.getLogLines()));
        fontBox.setValue(prefs.getCmdFont());
        outputFontBox.setValue(prefs.getOutputFont());
        logFontBox.setValue(prefs.getLogFont());
    }

    private ValidatedField createValidatedIntField(int initialValue, int min, int max) {
        TextField field = new TextField(String.valueOf(initialValue));

        // Blocca input non numerici
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if (change.getControlNewText().matches("\\d*")) {
                return change;
            }
            return null;
        };
        field.setTextFormatter(new TextFormatter<>(new StringConverter<>() {
            @Override
            public String toString(Integer object) {
                return object == null ? "" : object.toString();
            }

            @Override
            public Integer fromString(String string) {
                return (string.isEmpty()) ? 0 : Integer.parseInt(string);
            }
        }, initialValue, filter));

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        BooleanProperty invalid = new SimpleBooleanProperty(false);

        field.textProperty().addListener((obs, oldVal, newVal) -> {
            String error = FieldValidator.validateInt(newVal, min, max);
            if (error != null) {
                if (!field.getStyleClass().contains("text-field-error"))
                    field.getStyleClass().add("text-field-error");
                errorLabel.setText(error);
                invalid.set(true);
            } else {
                field.getStyleClass().remove("text-field-error");
                errorLabel.setText("");
                invalid.set(false);
            }
        });

        VBox container = new VBox(field, errorLabel);
        container.setSpacing(2);
        return new ValidatedField(container, field, invalid);
    }
}
