package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.frontend.i18n.FrontendMessageProvider;
import ch.supsi.fscli.backend.di.BackendInjector;
import ch.supsi.fscli.frontend.model.PreferencesModel;
import ch.supsi.fscli.frontend.view.PreferencesView;
import javafx.scene.control.Alert;

import java.util.Map;

public class PreferencesController {
    private final ch.supsi.fscli.backend.controller.PreferencesController backendController;
    private final PreferencesModel model;
    private final PreferencesView view;
    private Map<String, String> originalPrefs;

    public PreferencesController() {
        this.backendController = BackendInjector.getInstance(ch.supsi.fscli.backend.controller.PreferencesController.class);
        this.model = new PreferencesModel(backendController);
        this.view = new PreferencesView(model.load());
        initializeView();
        bindSaveButton();
    }

    private void initializeView() {
        originalPrefs = model.load();
        Map<String, String> prefs = originalPrefs;

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
            model.edit(newPrefs);
            showRestartAlert();
        }

        view.close();
    }

    private void showRestartAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(FrontendMessageProvider.get("alert.information"));
        alert.setHeaderText(null);
        alert.setContentText(FrontendMessageProvider.get("alert.applyPreferences"));
        alert.showAndWait();
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
