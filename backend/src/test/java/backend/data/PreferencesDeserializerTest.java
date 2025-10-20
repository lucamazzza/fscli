package backend.data;

import ch.supsi.fscli.backend.data.PreferencesDeserializer;
import ch.supsi.fscli.backend.model.UserPreferences;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PreferencesDeserializerTest {
    @Test
    void testDeserialize() throws Exception {
        String json = """
                {
                  "language": "en",
                  "cmdColumns": 80,
                  "outputLines": 10,
                  "logLines": 5,
                  "cmdFont": "Monospaced",
                  "outputFont": "Monospaced",
                  "logFont": "SansSerif"
                }
                """;

        PreferencesDeserializer deserializer = new PreferencesDeserializer();
        UserPreferences prefs = deserializer.deserialize(json);

        assertEquals("en", prefs.getLanguage());
        assertEquals(80, prefs.getCmdColumns());
        assertEquals(10, prefs.getOutputLines());
        assertEquals(5, prefs.getLogLines());
        assertEquals("Monospaced", prefs.getCmdFont());
        assertEquals("Monospaced", prefs.getOutputFont());
        assertEquals("SansSerif", prefs.getLogFont());
    }
}
