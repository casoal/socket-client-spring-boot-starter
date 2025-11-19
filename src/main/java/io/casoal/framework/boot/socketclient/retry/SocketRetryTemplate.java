package io.casoal.framework.boot.socketclient.retry;

import io.casoal.framework.boot.socketclient.util.SocketLogUtils;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;

public class SocketRetryTemplate {
    public static RetryTemplate create(RetryConfigResolver.SocketRetryMeta retryMeta) {
        RetryTemplate template = new RetryTemplate();

        Map<Class<? extends Throwable>,Boolean> retryableExceptions = new HashMap<>();
        // 配置重试异常
        for (Class<? extends Throwable> cls : retryMeta.getInclude()) {
            retryableExceptions.put(cls, true);
        }
        // 配置重试异常
        for (Class<? extends Throwable> cls : retryMeta.getExclude()) {
            retryableExceptions.put(cls, false);
        }
        // 重试策略
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(retryMeta.getMaxAttempts(),retryableExceptions);
        template.setRetryPolicy(retryPolicy);

        // 退避策略
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(retryMeta.getBackoff());
        template.setBackOffPolicy(backOffPolicy);

        // 重试日志监听器
        template.registerListener(new RetryListener() {
            @Override
            public <T, E extends Throwable> boolean open(RetryContext retryContext, RetryCallback<T, E> retryCallback) {
                return true;
            }

            @Override
            public <T, E extends Throwable> void close(RetryContext retryContext, RetryCallback<T, E> retryCallback, Throwable throwable) {
                if (retryContext.getRetryCount() > 0) {
                    SocketLogUtils.logRetry(retryContext.getAttribute("host").toString(),
                            (Integer) retryContext.getAttribute("port"),
                            retryContext.getAttribute("value").toString(),
                            retryContext.getRetryCount());
                }
            }

            @Override
            public <T, E extends Throwable> void onError(RetryContext retryContext, RetryCallback<T, E> retryCallback, Throwable throwable) {

            }
        });

        return template;
    }
}
