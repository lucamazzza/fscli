package backend.data.serde;

import ch.supsi.fscli.backend.data.serde.Deserializer;
import ch.supsi.fscli.backend.core.UserPreferences;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PreferencesDeserializerTest {

    @Test
    void testDeserializeWithClamp() throws Exception {
        String json = """
                {
                  "language": "it",
                  "cmdColumns": 200,
                  "outputLines": 1,
                  "logLines": 200,
                  "cmdFont": "Monospaced",
                  "outputFont": "Monospaced",
                  "logFont": "SansSerif"
                }
                """;

        Deserializer<UserPreferences> deserializer = new Deserializer<>();
        UserPreferences prefs = deserializer.deserialize(json, UserPreferences.class);

        assertEquals("it", prefs.getLanguage());
        assertEquals(100, prefs.getCmdColumns());
        assertEquals(3, prefs.getOutputLines());
        assertEquals(100, prefs.getLogLines());

        assertEquals("Monospaced", prefs.getCmdFont());
        assertEquals("Monospaced", prefs.getOutputFont());
        assertEquals("SansSerif", prefs.getLogFont());
    }
}
