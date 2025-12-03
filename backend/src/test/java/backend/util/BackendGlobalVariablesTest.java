package backend.util;

import ch.supsi.fscli.backend.util.BackendGlobalVariables;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BackendGlobalVariablesTest {
    
    @Test
    void testSystemFontsNotNull() {
        assertNotNull(BackendGlobalVariables.SYSTEM_FONTS);
    }
    
    @Test
    void testSystemFontsNotEmpty() {
        assertFalse(BackendGlobalVariables.SYSTEM_FONTS.isEmpty());
    }
    
    @Test
    void testMinColumns() {
        assertEquals(10, BackendGlobalVariables.MIN_COLUMNS);
    }
    
    @Test
    void testMaxColumns() {
        assertEquals(100, BackendGlobalVariables.MAX_COLUMNS);
    }
    
    @Test
    void testMinLines() {
        assertEquals(3, BackendGlobalVariables.MIN_LINES);
    }
    
    @Test
    void testMaxLines() {
        assertEquals(100, BackendGlobalVariables.MAX_LINES);
    }
    
    @Test
    void testDefaultLanguage() {
        assertEquals("en", BackendGlobalVariables.DEFAULT_LANGUAGE);
    }
    
    @Test
    void testDefaultCmdColumns() {
        assertEquals(80, BackendGlobalVariables.DEFAULT_CMD_COLUMNS);
    }
    
    @Test
    void testDefaultOutputLines() {
        assertEquals(10, BackendGlobalVariables.DEFAULT_OUTPUT_LINES);
    }
    
    @Test
    void testDefaultLogLines() {
        assertEquals(5, BackendGlobalVariables.DEFAULT_LOG_LINES);
    }
    
    @Test
    void testDefaultCmdFont() {
        assertEquals("Monospaced", BackendGlobalVariables.DEFAULT_CMD_FONT);
    }
    
    @Test
    void testDefaultOutputFont() {
        assertEquals("Monospaced", BackendGlobalVariables.DEFAULT_OUTPUT_FONT);
    }
    
    @Test
    void testDefaultLogFont() {
        assertEquals("SansSerif", BackendGlobalVariables.DEFAULT_LOG_FONT);
    }
    
    @Test
    void testDefaultPrefPathNotNull() {
        assertNotNull(BackendGlobalVariables.DEFAULT_PREF_PATH);
    }
    
    @Test
    void testDefaultPrefPathEndsWithJson() {
        assertTrue(BackendGlobalVariables.DEFAULT_PREF_PATH.toString().endsWith(".fs_prefs.json"));
    }
    
    @Test
    void testLimitsConsistency() {
        assertTrue(BackendGlobalVariables.MIN_COLUMNS < BackendGlobalVariables.MAX_COLUMNS);
        assertTrue(BackendGlobalVariables.MIN_LINES < BackendGlobalVariables.MAX_LINES);
    }
    
    @Test
    void testDefaultValuesWithinLimits() {
        assertTrue(BackendGlobalVariables.DEFAULT_CMD_COLUMNS >= BackendGlobalVariables.MIN_COLUMNS);
        assertTrue(BackendGlobalVariables.DEFAULT_CMD_COLUMNS <= BackendGlobalVariables.MAX_COLUMNS);
        assertTrue(BackendGlobalVariables.DEFAULT_OUTPUT_LINES >= BackendGlobalVariables.MIN_LINES);
        assertTrue(BackendGlobalVariables.DEFAULT_OUTPUT_LINES <= BackendGlobalVariables.MAX_LINES);
        assertTrue(BackendGlobalVariables.DEFAULT_LOG_LINES >= BackendGlobalVariables.MIN_LINES);
        assertTrue(BackendGlobalVariables.DEFAULT_LOG_LINES <= BackendGlobalVariables.MAX_LINES);
    }
}
