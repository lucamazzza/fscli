package ch.supsi.fscli.backend.data.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

public class Serializer<T> {
    private final ObjectMapper mapper;

    public Serializer() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public String serialize(T data) throws IOException {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
    }
}
