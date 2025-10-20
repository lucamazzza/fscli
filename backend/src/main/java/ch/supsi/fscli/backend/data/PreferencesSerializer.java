package ch.supsi.fscli.backend.data;

import ch.supsi.fscli.backend.model.UserPreferences;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class PreferencesSerializer {
    private final ObjectMapper mapper = new ObjectMapper();

    public String serialize(UserPreferences prefs) throws IOException {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(prefs);
    }
}
