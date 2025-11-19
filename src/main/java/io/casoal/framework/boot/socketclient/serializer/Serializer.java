package io.casoal.framework.boot.socketclient.serializer;

import java.io.IOException;

/**
 * 序列化器接口
 */
public interface Serializer {
    /**
     * 序列化器名称（用于日志）
     */
    String getName();

    /**
     * 序列化
     */
    byte[] serialize(Object obj) throws IOException;

    /**
     * 反序列化
     */
    <T> T deserialize(byte[] data, Class<T> clazz) throws IOException;
}
