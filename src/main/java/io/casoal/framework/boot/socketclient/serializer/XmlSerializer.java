package io.casoal.framework.boot.socketclient.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class XmlSerializer extends StringSerializer {
    private final XmlMapper xmlMapper = new XmlMapper();

    @Override
    public String getName() {
        return "json";
    }

    @Override
    protected String objectToString(Object obj) throws IOException {
        try {
            return xmlMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IOException("Xml序列化失败", e);
        }
    }

    @Override
    protected <T> T stringToObject(String str, Class<T> clazz) throws IOException {
        return xmlMapper.readValue(str, clazz);
    }

    @Override
    protected Charset getCharset() {
        return StandardCharsets.UTF_8;
    }
}
