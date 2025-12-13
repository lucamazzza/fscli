package ch.supsi.fscli.frontend.model;

import ch.supsi.fscli.backend.controller.PreferencesController;
import ch.supsi.fscli.backend.core.UserPreferences;
import ch.supsi.fscli.backend.di.BackendInjector;
import ch.supsi.fscli.frontend.event.EventPublisher;
import ch.supsi.fscli.frontend.event.PreferencesEvent;
import ch.supsi.fscli.frontend.util.AppError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PreferencesModelTest {

    private PreferencesModel model;
    private TestEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        BackendInjector.initialize();
        PreferencesController backendController = BackendInjector.getInstance(PreferencesController.class);
        
        model = new PreferencesModel(backendController);
        eventPublisher = new TestEventPublisher();
        model.setPreferencesEventPublisher(eventPublisher);
    }

    @Test
    void testLoadPreferences() {
        Map<String, String> result = model.loadPreferences();

        assertNotNull(result);
        assertTrue(result.containsKey("language"));
        assertTrue(result.containsKey("cmdColumns"));
        assertTrue(result.containsKey("outputLines"));
        assertTrue(result.containsKey("logLines"));
        assertTrue(result.containsKey("cmdFont"));
        assertTrue(result.containsKey("outputFont"));
        assertTrue(result.containsKey("logFont"));
        
        // Verify event was published
        assertEquals(1, eventPublisher.events.size());
        assertEquals(AppError.PREFERENCES_LOADED, eventPublisher.events.get(0).error());
    }

    @Test
    void testSavePreferences() {
        Map<String, String> newPrefs = Map.of(
            "language", "it",
            "cmdColumns", "100",
            "outputLines", "15",
            "logLines", "8",
            "cmdFont", "Courier",
            "outputFont", "Courier",
            "logFont", "Arial"
        );

        model.savePreferences(newPrefs);

        // Verify event was published
        assertEquals(1, eventPublisher.events.size());
        assertEquals(AppError.PREFERENCES_SAVED, eventPublisher.events.get(0).error());
        
        // Verify preferences were saved
        Map<String, String> current = model.getCurrentPreferences();
        assertEquals("it", current.get("language"));
        assertEquals("100", current.get("cmdColumns"));
    }

    @Test
    void testSavePreferencesWithNull() {
        model.savePreferences(null);

        // Verify error event was published
        assertEquals(1, eventPublisher.events.size());
        assertEquals(AppError.PREFERENCES_SAVE_FAILED, eventPublisher.events.get(0).error());
    }

    @Test
    void testSavePreferencesWithEmptyMap() {
        model.savePreferences(Map.of());

        // Verify error event was published
        assertEquals(1, eventPublisher.events.size());
        assertEquals(AppError.PREFERENCES_SAVE_FAILED, eventPublisher.events.get(0).error());
    }

    @Test
    void testGetCurrentPreferences() {
        Map<String, String> current = model.getCurrentPreferences();

        assertNotNull(current);
        assertTrue(current.containsKey("language"));
        assertTrue(current.containsKey("cmdColumns"));
        
        // Verify immutability - modifying returned map shouldn't affect model
        current.clear();
        Map<String, String> current2 = model.getCurrentPreferences();
        assertFalse(current2.isEmpty());
    }

    @Test
    void testMultipleSaveAndLoad() {
        // Save preferences
        Map<String, String> prefs1 = Map.of(
            "language", "en",
            "cmdColumns", "80",
            "outputLines", "10",
            "logLines", "5",
            "cmdFont", "Monospaced",
            "outputFont", "Monospaced",
            "logFont", "SansSerif"
        );
        model.savePreferences(prefs1);
        
        // Load and verify
        eventPublisher.events.clear();
        Map<String, String> loaded = model.loadPreferences();
        assertEquals("en", loaded.get("language"));
        assertEquals("80", loaded.get("cmdColumns"));
        
        assertEquals(1, eventPublisher.events.size());
        assertEquals(AppError.PREFERENCES_LOADED, eventPublisher.events.get(0).error());
    }

    // Simple test event publisher
    private static class TestEventPublisher implements EventPublisher<PreferencesEvent> {
        List<PreferencesEvent> events = new ArrayList<>();

        @Override
        public void notify(PreferencesEvent event) {
            events.add(event);
        }
    }
}