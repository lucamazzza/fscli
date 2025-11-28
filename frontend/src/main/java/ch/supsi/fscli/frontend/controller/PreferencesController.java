package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.backend.controller.BackendPreferencesController;
import ch.supsi.fscli.frontend.model.PreferencesModel;
import ch.supsi.fscli.frontend.view.PreferencesView;

import java.util.Map;

public class PreferencesController {

    private final BackendPreferencesController backendController;
    private final PreferencesModel model;
    private final PreferencesView view;

    public PreferencesController() {
        this.backendController = new BackendPreferencesController();
        this.model = new PreferencesModel(backendController);
        this.view = new PreferencesView(model.load());
        initializeView();
        bindSaveButton();
    }

    private void initializeView() {
        Map<String, String> prefs = model.load();

        view.setLanguage(prefs.get("language"));
        view.setCmdColumns(prefs.get("cmdColumns"));
        view.setOutputLines(prefs.get("outputLines"));
        view.setLogLines(prefs.get("logLines"));
        view.setCmdFont(prefs.get("cmdFont"));
        view.setOutputFont(prefs.get("outputFont"));
        view.setLogFont(prefs.get("logFont"));

        view.setOnSave(this::savePreferences);
        view.setOnCancel(view::close);
        view.setOnReload(this::reloadPreferences);
    }

    private void bindSaveButton() {
        view.saveBtnDisableProperty().bind(
                view.cmdColumnsInvalidProperty()
                        .or(view.outputLinesInvalidProperty())
                        .or(view.logLinesInvalidProperty())
        );
    }

    private void savePreferences() {
        model.edit(Map.of(
                "language", view.getLanguage(),
                "cmdColumns", view.getCmdColumns(),
                "outputLines", view.getOutputLines(),
                "logLines", view.getLogLines(),
                "cmdFont", view.getCmdFont(),
                "outputFont", view.getOutputFont(),
                "logFont", view.getLogFont()
        ));
        view.close();
    }

    private void reloadPreferences() {
        model.reload();
        Map<String, String> prefs = model.load();
        view.setLanguage(prefs.get("language"));
        view.setCmdColumns(prefs.get("cmdColumns"));
        view.setOutputLines(prefs.get("outputLines"));
        view.setLogLines(prefs.get("logLines"));
        view.setCmdFont(prefs.get("cmdFont"));
        view.setOutputFont(prefs.get("outputFont"));
        view.setLogFont(prefs.get("logFont"));
    }

    public void show() {
        view.show();
    }
}
