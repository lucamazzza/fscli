package ch.supsi.fscli.frontend.controller;

import ch.supsi.fscli.frontend.handler.PreferencesHandler;
import ch.supsi.fscli.frontend.model.PreferencesModel;
import lombok.Setter;

import java.util.Map;

public class PreferencesController implements PreferencesHandler {
    
    @Setter
    private PreferencesModel model;

    private static PreferencesController instance;

    public static PreferencesController getInstance() {
        if (instance == null) {
            instance = new PreferencesController();
        }
        return instance;
    }

    PreferencesController() {
    }

    @Override
    public void edit(Map<String, String> settings) {
        if (model == null || settings == null) return;
        model.savePreferences(settings);
    }

    @Override
    public Map<String, String> load() {
        if (model == null) return Map.of();
        return model.getCurrentPreferences();
    }
}
