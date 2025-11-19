package io.casoal.framework.boot.socketclient.proxy;

import io.casoal.framework.boot.socketclient.annotation.SocketClient;
import io.casoal.framework.boot.socketclient.autoconfigure.ConfigResolver;
import io.casoal.framework.boot.socketclient.serializer.Serializer;
import org.springframework.core.env.Environment;

import java.lang.reflect.Proxy;

/**
 * Socket客户端代理工程
 */
public class SocketClientProxy {

    public static <T> T create(Class<T> interfaceClass, ConfigResolver resolver) {
        SocketClient annotation = interfaceClass.getAnnotation(SocketClient.class);
        if (annotation == null) {
            throw new IllegalArgumentException("接口必须标注@SocketClient注解：" + interfaceClass.getName());
        }

        // 创建序列化器实例
        Serializer serializer;
        try {
            serializer = annotation.serializer().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("创建序列化器失败：" + annotation.serializer().getName(), e);
        }

        // 创建动态代理
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),new Class[]{interfaceClass},new SocketInvocationHandler(resolver,annotation,serializer));
    }
}
