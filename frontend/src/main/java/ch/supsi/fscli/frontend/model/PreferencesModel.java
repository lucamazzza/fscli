package ch.supsi.fscli.frontend.model;

import java.util.HashMap;
import java.util.Map;

public class PreferencesModel {

    private final Map<String, String> prefs;

    public PreferencesModel(Map<String, String> initialPrefs) {
        this.prefs = new HashMap<>(initialPrefs);
    }

    public Map<String, String> getAll() {
        return new HashMap<>(prefs);
    }

    public void update(Map<String, String> newPrefs) {
        prefs.clear();
        prefs.putAll(newPrefs);
    }

    public String get(String key) {
        return prefs.get(key);
    }

    public void set(String key, String value) {
        prefs.put(key, value);
    }
}
