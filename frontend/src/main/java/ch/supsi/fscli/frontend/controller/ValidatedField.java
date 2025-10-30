package ch.supsi.fscli.frontend.controller;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

import java.util.function.UnaryOperator;

public class ValidatedField {
    private final VBox container;
    private final TextField field;
    private final BooleanProperty invalid;

    public ValidatedField(int initialValue, int min, int max) {
        this.field = new TextField(String.valueOf(initialValue));
        this.invalid = new javafx.beans.property.SimpleBooleanProperty(false);

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");

        container = new VBox(field, errorLabel);
        container.setSpacing(2);

        // Blocca input non numerico
        UnaryOperator<TextFormatter.Change> filter = change ->
                change.getControlNewText().matches("\\d*") ? change : null;

        field.setTextFormatter(new TextFormatter<Integer>(new StringConverter<>() {
            @Override
            public String toString(Integer object) { return object == null ? "" : object.toString(); }
            @Override
            public Integer fromString(String string) { return string.isEmpty() ? 0 : Integer.parseInt(string); }
        }, initialValue, filter));

        // Listener per validazione
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            int value = newVal.isEmpty() ? 0 : Integer.parseInt(newVal);
            if (value < min || value > max) {
                if (!field.getStyleClass().contains("text-field-error"))
                    field.getStyleClass().add("text-field-error");
                errorLabel.setText("Value must be between " + min + " and " + max);
                invalid.set(true);
            } else {
                field.getStyleClass().remove("text-field-error");
                errorLabel.setText("");
                invalid.set(false);
            }
        });
    }

    public VBox container() { return container; }
    public TextField field() { return field; }
    public BooleanProperty invalid() { return invalid; }
}
