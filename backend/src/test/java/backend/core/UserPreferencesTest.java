package backend.core;

import ch.supsi.fscli.backend.core.UserPreferences;
import ch.supsi.fscli.backend.util.BackendGlobalVariables;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

public class UserPreferencesTest {

    @Test
    void testDefaultValues() {
        UserPreferences prefs = new UserPreferences();

        assertEquals("en", prefs.getLanguage());
        assertEquals(80, prefs.getCmdColumns());
        assertEquals(10, prefs.getOutputLines());
        assertEquals(5, prefs.getLogLines());
        assertEquals("Monospaced", prefs.getCmdFont());
        assertEquals("Monospaced", prefs.getOutputFont());
        assertEquals("SansSerif", prefs.getLogFont());
    }

    @Test
    void testClamping() {
        UserPreferences prefs = new UserPreferences();

        // valori troppo bassi o alti vengono clampati ai limiti definiti
        prefs.setCmdColumns(-10);
        prefs.setOutputLines(9999);
        prefs.setLogLines(0);

        assertEquals(BackendGlobalVariables.MIN_COLUMNS, prefs.getCmdColumns());
        assertEquals(BackendGlobalVariables.MAX_LINES, prefs.getOutputLines());
        assertEquals(BackendGlobalVariables.MIN_LINES, prefs.getLogLines());
    }

    @Test
    void testCopyConstructor() {
        // Mock getSystemFonts() to include fonts needed for the test
        List<String> mockFonts = List.of("Monospaced", "Arial", "SansSerif", "Serif");
        
        try (MockedStatic<BackendGlobalVariables> mockedGlobals = mockStatic(BackendGlobalVariables.class)) {
            mockedGlobals.when(BackendGlobalVariables::getSystemFonts).thenReturn(mockFonts);
            
            UserPreferences original = new UserPreferences();
            original.setLanguage("it");
            original.setCmdColumns(100);
            original.setOutputLines(15);
            original.setLogLines(40);
            original.setCmdFont("Monospaced");
            original.setOutputFont("Arial");
            original.setLogFont("SansSerif");

            UserPreferences copy = new UserPreferences(original);

            assertEquals("it", copy.getLanguage());
            assertEquals(100, copy.getCmdColumns());
            assertEquals(15, copy.getOutputLines());
            assertEquals(40, copy.getLogLines());
            assertEquals("Monospaced", copy.getCmdFont());
            assertEquals("Arial", copy.getOutputFont());
            assertEquals("SansSerif", copy.getLogFont());

            // assicura che sia una copia indipendente
            original.setLanguage("fr");
            assertNotEquals(original.getLanguage(), copy.getLanguage());
        }
    }
}
