package ch.supsi.fscli.frontend.model;

import ch.supsi.fscli.frontend.controller.PreferencesHandler;

import java.util.Map;

public class PreferencesModel implements AbstractModel, PreferencesHandler {
    @Override
    public void edit(Map<String, String> settings) {

    }

    @Override
    public Map<String, String> load() {
        return Map.of();
    }
}
