package backend.data;

import ch.supsi.fscli.backend.data.PreferencesDeserializer;
import ch.supsi.fscli.backend.model.UserPreferences;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PreferencesDeserializerTest {

    @Test
    void testDeserializeWithClamp() throws Exception {
        String json = """
                {
                  "language": "it",
                  "cmdColumns": 200,   // troppo alto
                  "outputLines": 1,    // troppo basso
                  "logLines": 200,     // troppo alto
                  "cmdFont": "Monospaced",
                  "outputFont": "Monospaced",
                  "logFont": "SansSerif"
                }
                """;

        PreferencesDeserializer deserializer = new PreferencesDeserializer();
        UserPreferences prefs = deserializer.deserialize(json);

        assertEquals("it", prefs.getLanguage());
        assertEquals(100, prefs.getCmdColumns());
        assertEquals(3, prefs.getOutputLines());
        assertEquals(100, prefs.getLogLines());

        assertEquals("Monospaced", prefs.getCmdFont());
        assertEquals("Monospaced", prefs.getOutputFont());
        assertEquals("SansSerif", prefs.getLogFont());
    }
}
