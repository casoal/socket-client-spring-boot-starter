package io.casoal.framework.boot.socketclient.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class JsonSerializer extends StringSerializer {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getName() {
        return "json";
    }

    @Override
    protected String objectToString(Object obj) throws IOException {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IOException("Json序列化失败", e);
        }
    }

    @Override
    protected <T> T stringToObject(String str, Class<T> clazz) throws IOException {
        return objectMapper.readValue(str, clazz);
    }

    @Override
    protected Charset getCharset() {
        return StandardCharsets.UTF_8;
    }
}
