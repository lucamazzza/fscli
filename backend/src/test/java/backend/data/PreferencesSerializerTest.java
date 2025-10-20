package backend.data;

import ch.supsi.fscli.backend.data.PreferencesSerializer;
import ch.supsi.fscli.backend.model.UserPreferences;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PreferencesSerializerTest {

    @Test
    void testSerialize() throws Exception {
        UserPreferences prefs = new UserPreferences();
        prefs.setLanguage("it");
        prefs.setCmdColumns(80);
        prefs.setOutputLines(10);

        PreferencesSerializer serializer = new PreferencesSerializer();
        String json = serializer.serialize(prefs);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);

        assertEquals("it", node.get("language").asText());
        assertEquals(80, node.get("cmdColumns").asInt());
        assertEquals(10, node.get("outputLines").asInt());

        //check the existance of other fields
        assertTrue(node.has("logLines"));
        assertTrue(node.has("cmdFont"));
        assertTrue(node.has("outputFont"));
        assertTrue(node.has("logFont"));
    }
}
