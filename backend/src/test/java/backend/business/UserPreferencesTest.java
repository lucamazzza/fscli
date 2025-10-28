package backend.business;

import ch.supsi.fscli.backend.business.UserPreferences;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserPreferencesTest {

    @Test
    void testClamping() {
        UserPreferences prefs = new UserPreferences();
        prefs.setOutputLines(1);
        prefs.setLogLines(200);
        prefs.setCmdColumns(5);

        assertEquals(3, prefs.getOutputLines());
        assertEquals(100, prefs.getLogLines());
        assertEquals(10, prefs.getCmdColumns());
    }

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
}
