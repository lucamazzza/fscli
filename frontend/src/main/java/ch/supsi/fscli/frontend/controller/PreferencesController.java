package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.frontend.handler.PreferencesHandler;
import ch.supsi.fscli.frontend.i18n.FrontendMessageProvider;
import ch.supsi.fscli.backend.di.BackendInjector;
import ch.supsi.fscli.frontend.model.PreferencesModel;
import ch.supsi.fscli.frontend.view.PreferencesView;
import javafx.scene.control.Alert;

import java.util.Map;
import java.util.Optional;

public class PreferencesController implements PreferencesHandler {

    private final ch.supsi.fscli.backend.controller.PreferencesController backendController;
    private final PreferencesModel model;
    private final PreferencesView view;

    private Map<String, String> originalPrefs;


    public PreferencesController() {

        this.backendController =
                BackendInjector.getInstance(ch.supsi.fscli.backend.controller.PreferencesController.class);

        this.originalPrefs = load();

        this.model = new PreferencesModel(originalPrefs);

        this.view = PreferencesView.getInstance(originalPrefs);

        initializeView();
        bindSaveButton();
    }


    private void initializeView() {
        Map<String, String> prefs = model.getAll();

        view.setLanguage(prefs.get("language"));
        view.setCmdColumns(prefs.get("cmdColumns"));
        view.setOutputLines(prefs.get("outputLines"));
        view.setLogLines(prefs.get("logLines"));
        view.setCmdFont(prefs.get("cmdFont"));
        view.setOutputFont(prefs.get("outputFont"));
        view.setLogFont(prefs.get("logFont"));

        view.setOnSave(this::onSave);
        view.setOnCancel(view::close);
        view.setOnReload(this::onReload);
    }


    private void bindSaveButton() {
        view.saveBtnDisableProperty().bind(
                view.cmdColumnsInvalidProperty()
                        .or(view.outputLinesInvalidProperty())
                        .or(view.logLinesInvalidProperty())
        );
    }


    private void onSave() {

        Map<String, String> newPrefs = Map.of(
                "language", view.getLanguage(),
                "cmdColumns", view.getCmdColumns(),
                "outputLines", view.getOutputLines(),
                "logLines", view.getLogLines(),
                "cmdFont", view.getCmdFont(),
                "outputFont", view.getOutputFont(),
                "logFont", view.getLogFont()
        );

        if (!newPrefs.equals(originalPrefs)) {
            edit(newPrefs);
            originalPrefs = newPrefs;
            showRestartAlert();
        }

        view.close();
    }


    private void onReload() {
        originalPrefs = load();      // ricarica dal backend
        model.update(originalPrefs);
        view.updateValues(originalPrefs);
    }


    private void showRestartAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(FrontendMessageProvider.get("alert.information"));
        alert.setHeaderText(null);
        alert.setContentText(FrontendMessageProvider.get("alert.applyPreferences"));
        alert.showAndWait();
    }


    @Override
    public void edit(Map<String, String> settings) {
        settings.forEach(
                (key, value) ->
                        backendController.updateOptionalPreference(key, Optional.of(value))
        );
    }

    @Override
    public Map<String, String> load() {
        var p = backendController.getPreferences();
        return Map.of(
                "language", p.getLanguage(),
                "cmdColumns", String.valueOf(p.getCmdColumns()),
                "outputLines", String.valueOf(p.getOutputLines()),
                "logLines", String.valueOf(p.getLogLines()),
                "cmdFont", p.getCmdFont(),
                "outputFont", p.getOutputFont(),
                "logFont", p.getLogFont()
        );
    }


    public void show() {
        view.show();
    }
}
