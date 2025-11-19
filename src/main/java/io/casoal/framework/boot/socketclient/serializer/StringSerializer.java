package io.casoal.framework.boot.socketclient.serializer;

import java.io.IOException;
import java.nio.charset.Charset;

public abstract class StringSerializer implements Serializer {


    @Override
    public byte[] serialize(Object obj) throws IOException {
        if (obj == null) {
            return new byte[0];
        }
        return objectToString(obj).getBytes(getCharset());
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws IOException {
        if (data == null || data.length == 0) {
            return null;
        }
        return stringToObject(new String(data, getCharset()), clazz);
    }

    protected abstract String objectToString(Object obj) throws IOException;

    protected abstract <T> T stringToObject(String str, Class<T> clazz) throws IOException;

    protected abstract Charset getCharset();
}
