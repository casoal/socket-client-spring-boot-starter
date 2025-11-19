package io.casoal.framework.boot.socketclient.retry;

import io.casoal.framework.boot.socketclient.annotation.SocketClient;
import io.casoal.framework.boot.socketclient.annotation.SocketRetry;
import io.casoal.framework.boot.socketclient.autoconfigure.ConfigResolver;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;

public class RetryConfigResolver {

    public static SocketRetryMeta resolve(Method method, ConfigResolver resolver, SocketClient classAnnotation) {
        SocketRetry methodAnnotation = method.getAnnotation(SocketRetry.class);

        // 基础配置（类级）
        int maxAttempts = resolver.resolveInt(classAnnotation.retryMaxAttempts());
        int backoff = resolver.resolveInt(classAnnotation.retryBackoff());
        Class<? extends Throwable>[] include = classAnnotation.retryInclude();
        Class<? extends Throwable>[] exclude = classAnnotation.retryExclude();

        // 方法级配置覆盖
        if (methodAnnotation != null) {
            int attempts = resolver.resolveInt(methodAnnotation.maxAttempts());
            if (attempts != -1) {
                maxAttempts = attempts;
            }

            int anInt = resolver.resolveInt(methodAnnotation.backoff());
            if (anInt != -1) {
                backoff = anInt;
            }

            if (methodAnnotation.include().length > 0) {
                include = methodAnnotation.include();
            }

            if (methodAnnotation.exclude().length > 0) {
                exclude = methodAnnotation.exclude();
            }
        }
        return new SocketRetryMeta(maxAttempts, backoff, include, exclude);
    }

    @RequiredArgsConstructor
    @Getter
    public static class SocketRetryMeta {
        private final int maxAttempts;
        private final int backoff;
        private final Class<? extends Throwable>[] include;
        private final Class<? extends Throwable>[] exclude;
    }
}
