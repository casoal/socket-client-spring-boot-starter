package io.casoal.framework.boot.socketclient.annotation;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法级别重置配置注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SocketRetry {
    /**
     * 最大重试次数(-1=不重试)，包含首次失败，默认使用类级配置
     */
    String maxAttempts() default "${socket.client.default.retry.maxAttempts:-1}";

    /**
     * 重试间隔(ms)，默认使用类级配置
     */
    String backoff() default "${socket.client.default.retry.backoff:-1}";

    /**
     * 需要重试的异常类型，默认使用类级配置
     */
    Class<? extends Throwable>[] include() default {IOException.class};

    /**
     * 不需要重试的异常烈性，默认使用类级配置
     */
    Class<? extends Throwable>[] exclude() default {};
}
