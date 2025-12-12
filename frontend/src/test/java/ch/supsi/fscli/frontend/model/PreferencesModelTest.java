package ch.supsi.fscli.frontend.model;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class PreferencesModelTest {

    @Test
    void testInitializeAndGet() {
        Map<String, String> initial = new HashMap<>();
        initial.put("theme", "dark");
        PreferencesModel model = new PreferencesModel(initial);
        assertEquals("dark", model.get("theme"));
    }

    @Test
    void testSetAndUpdate() {
        PreferencesModel model = new PreferencesModel(new HashMap<>());
        model.set("language", "en");
        assertEquals("en", model.get("language"));
    }

    @Test
    void testImmutabilityOfGetAll() {
        PreferencesModel model = new PreferencesModel(new HashMap<>());
        model.set("key", "value");
        Map<String, String> exposedMap = model.getAll();
        exposedMap.clear();
        assertEquals("value", model.get("key"));
    }
}