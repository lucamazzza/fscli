package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.backend.application.PreferencesService;
import ch.supsi.fscli.frontend.model.PreferencesModel;
import ch.supsi.fscli.frontend.view.PreferencesView;
import javafx.beans.binding.Bindings;

import java.util.Map;

public class PreferencesController {

    private final PreferencesModel model;
    private final PreferencesView view;

    public PreferencesController(PreferencesModel model, PreferencesView view) {
        this.model = model;
        this.view = view;

        bindEvents();
        bindSaveButton();
    }

    public PreferencesController() {
        this.model = new PreferencesModel(new PreferencesService());
        this.view = new PreferencesView(model.load());
        bindEvents();
        bindSaveButton();
    }

    private void bindSaveButton() {
        view.saveBtn.disableProperty().bind(
                Bindings.or(view.cmdColumnsField.invalid(),
                        Bindings.or(view.outputLinesField.invalid(), view.logLinesField.invalid()))
        );
    }

    private void bindEvents() {
        view.saveBtn.setOnAction(e -> {
            model.edit(Map.of(
                    "language", view.languageBox.getValue(),
                    "cmdColumns", view.cmdColumnsField.field().getText(),
                    "outputLines", view.outputLinesField.field().getText(),
                    "logLines", view.logLinesField.field().getText(),
                    "cmdFont", view.cmdFontBox.getValue(),
                    "outputFont", view.outputFontBox.getValue(),
                    "logFont", view.logFontBox.getValue()
            ));
            view.stage.close();
        });

        view.cancelBtn.setOnAction(e -> view.stage.close());

        view.reloadBtn.setOnAction(e -> {
            model.reload();
            Map<String, String> prefs = model.load();
            view.languageBox.setValue(prefs.get("language"));
            view.cmdColumnsField.field().setText(prefs.get("cmdColumns"));
            view.outputLinesField.field().setText(prefs.get("outputLines"));
            view.logLinesField.field().setText(prefs.get("logLines"));
            view.cmdFontBox.setValue(prefs.get("cmdFont"));
            view.outputFontBox.setValue(prefs.get("outputFont"));
            view.logFontBox.setValue(prefs.get("logFont"));
        });
    }

    public void show() {
        view.show();
    }
}
