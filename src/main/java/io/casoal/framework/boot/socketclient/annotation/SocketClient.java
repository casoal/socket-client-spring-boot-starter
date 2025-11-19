package io.casoal.framework.boot.socketclient.annotation;

import io.casoal.framework.boot.socketclient.serializer.JsonSerializer;
import io.casoal.framework.boot.socketclient.serializer.Serializer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Socket客户端接口注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface SocketClient {
    /**
     * 服务端主机
     */
    String host() default "${socket.client.default.host:localhost}";

    /**
     * 端口
     */
    String port() default "${socket.client.default.port:8888}";

    /**
     * 连接超时
     */
    String connectTimeout() default "${socket.client.default.connectTimeout:3000}";

    /**
     * 读取超时
     */
    String readTimeout() default "${socket.client.default.readTimeout:5000}";

    /**
     * 序列化器
     */
    Class<? extends Serializer> serializer() default JsonSerializer.class;

    /**
     * 是否使用长度前缀
     */
    boolean useLengthPrefix() default false;

    /**
     * 最大重试次数(-1=不重试)
     */
    String retryMaxAttempts() default "${socket.client.default.retryMaxAttempts:-1}";

    /**
     * 重试间隔(ms)
     */
    String retryBackoff() default "${socket.client.default.retryBackoff:-1}";

    /**
     * 重试异常
     */
    Class<? extends Throwable>[] retryInclude() default {IOException.class};

    /**
     * 不重试异常
     */
    Class<? extends Throwable>[] retryExclude() default {};
}
