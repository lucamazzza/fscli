package ch.supsi.fscli.backend.data;

import ch.supsi.fscli.backend.business.UserPreferences;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Serializer<T> {
    private final ObjectMapper mapper = new ObjectMapper();

    public String serialize(T data) throws IOException {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
    }
}
