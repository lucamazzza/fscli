package ch.supsi.fscli.frontend.controller;

import javafx.beans.property.BooleanProperty;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public record ValidatedField(VBox container, TextField field, BooleanProperty invalid) {
    public boolean isInvalid() {
        return invalid.get();
    }
}
