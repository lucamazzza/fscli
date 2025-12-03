package backend.controller;

import backend.util.TestInjectorFactory;
import ch.supsi.fscli.backend.controller.PreferencesController;
import ch.supsi.fscli.backend.core.UserPreferences;
import ch.supsi.fscli.backend.util.BackendGlobalVariables;
import com.google.inject.Injector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PreferencesControllerTest {
    
    private PreferencesController controller;
    
    @BeforeEach
    void setUp() {
        Injector injector = TestInjectorFactory.createTestInjector();
        controller = injector.getInstance(PreferencesController.class);
    }
    
    @Test
    void testGetPreferences() {
        UserPreferences prefs = controller.getPreferences();
        assertNotNull(prefs);
    }
    
    @Test
    void testSetLanguage() {
        controller.setLanguage("it");
        assertEquals("it", controller.getPreferences().getLanguage());
    }
    
    @Test
    void testSetLanguage_Invalid() {
        controller.setLanguage("invalid");
        assertEquals(BackendGlobalVariables.DEFAULT_LANGUAGE, controller.getPreferences().getLanguage());
    }
    
    @Test
    void testSetCmdColumns() {
        controller.setCmdColumns(50);
        assertEquals(50, controller.getPreferences().getCmdColumns());
    }
    
    @Test
    void testSetCmdColumns_ClampedToMax() {
        controller.setCmdColumns(200);
        assertEquals(BackendGlobalVariables.MAX_COLUMNS, controller.getPreferences().getCmdColumns());
    }
    
    @Test
    void testSetCmdColumns_ClampedToMin() {
        controller.setCmdColumns(5);
        assertEquals(BackendGlobalVariables.MIN_COLUMNS, controller.getPreferences().getCmdColumns());
    }
    
    @Test
    void testSetOutputLines() {
        controller.setOutputLines(50);
        assertEquals(50, controller.getPreferences().getOutputLines());
    }
    
    @Test
    void testSetOutputLines_ClampedToMax() {
        controller.setOutputLines(500);
        assertEquals(BackendGlobalVariables.MAX_LINES, controller.getPreferences().getOutputLines());
    }
    
    @Test
    void testSetLogLines() {
        controller.setLogLines(50);
        assertEquals(50, controller.getPreferences().getLogLines());
    }
    
    @Test
    void testSetLogLines_ClampedToMax() {
        controller.setLogLines(250);
        assertEquals(BackendGlobalVariables.MAX_LINES, controller.getPreferences().getLogLines());
    }
    
    @Test
    void testSetCmdFont_ValidFont() {
        String validFont = BackendGlobalVariables.SYSTEM_FONTS.get(0);
        controller.setCmdFont(validFont);
        assertEquals(validFont, controller.getPreferences().getCmdFont());
    }
    
    @Test
    void testSetCmdFont_InvalidFont() {
        controller.setCmdFont("NonExistentFont12345");
        assertEquals(BackendGlobalVariables.DEFAULT_CMD_FONT, controller.getPreferences().getCmdFont());
    }
    
    @Test
    void testSetOutputFont_ValidFont() {
        String validFont = BackendGlobalVariables.SYSTEM_FONTS.get(0);
        controller.setOutputFont(validFont);
        assertEquals(validFont, controller.getPreferences().getOutputFont());
    }
    
    @Test
    void testSetLogFont_ValidFont() {
        String validFont = BackendGlobalVariables.SYSTEM_FONTS.get(0);
        controller.setLogFont(validFont);
        assertEquals(validFont, controller.getPreferences().getLogFont());
    }
    
    @Test
    void testUpdatePreferences() {
        controller.updatePreferences(prefs -> {
            prefs.setLanguage("fr");
            prefs.setCmdColumns(50);
        });
        
        UserPreferences prefs = controller.getPreferences();
        assertEquals("fr", prefs.getLanguage());
        assertEquals(50, prefs.getCmdColumns());
    }
    
    @Test
    void testReloadPreferences() {
        controller.setLanguage("de");
        controller.reloadPreferences();
        assertNotNull(controller.getPreferences());
    }
    
    @Test
    void testUpdateOptionalPreference_Language() {
        controller.updateOptionalPreference("language", Optional.of("it"));
        assertEquals("it", controller.getPreferences().getLanguage());
    }
    
    @Test
    void testUpdateOptionalPreference_CmdColumns() {
        controller.updateOptionalPreference("cmdColumns", Optional.of("50"));
        assertEquals(50, controller.getPreferences().getCmdColumns());
    }
    
    @Test
    void testUpdateOptionalPreference_OutputLines() {
        controller.updateOptionalPreference("outputLines", Optional.of("50"));
        assertEquals(50, controller.getPreferences().getOutputLines());
    }
    
    @Test
    void testUpdateOptionalPreference_LogLines() {
        controller.updateOptionalPreference("logLines", Optional.of("50"));
        assertEquals(50, controller.getPreferences().getLogLines());
    }
    
    @Test
    void testUpdateOptionalPreference_CmdFont() {
        String validFont = BackendGlobalVariables.SYSTEM_FONTS.get(0);
        controller.updateOptionalPreference("cmdFont", Optional.of(validFont));
        assertEquals(validFont, controller.getPreferences().getCmdFont());
    }
    
    @Test
    void testUpdateOptionalPreference_OutputFont() {
        String validFont = BackendGlobalVariables.SYSTEM_FONTS.get(0);
        controller.updateOptionalPreference("outputFont", Optional.of(validFont));
        assertEquals(validFont, controller.getPreferences().getOutputFont());
    }
    
    @Test
    void testUpdateOptionalPreference_LogFont() {
        String validFont = BackendGlobalVariables.SYSTEM_FONTS.get(0);
        controller.updateOptionalPreference("logFont", Optional.of(validFont));
        assertEquals(validFont, controller.getPreferences().getLogFont());
    }
    
    @Test
    void testUpdateOptionalPreference_EmptyOptional() {
        String originalLanguage = controller.getPreferences().getLanguage();
        controller.updateOptionalPreference("language", Optional.empty());
        assertEquals(originalLanguage, controller.getPreferences().getLanguage());
    }
    
    @Test
    void testUpdateOptionalPreference_InvalidKey() {
        // Should not throw exception, just log error
        assertDoesNotThrow(() -> {
            controller.updateOptionalPreference("invalidKey", Optional.of("value"));
        });
    }
}
