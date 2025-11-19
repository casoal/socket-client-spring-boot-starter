package io.casoal.framework.boot.socketclient.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "SocketClient")
public class SocketLogUtils {

    public static void logRequest(String host, int port, String value, int dataLength, long startTime) {
        log.info("[Socket请求-{}] 地址：{}:{} 数据长度：{}B 开始时间：{}", value, host, port, dataLength, startTime);
    }

    public static void logResponse(String host, int port, String value, int dataLength, long startTime) {
        long cost = System.currentTimeMillis() - startTime;
        log.info("[Socket响应-{}] 地址：{}:{} 数据长度：{}B 耗时：{}", value, host, port, dataLength, cost);
    }

    public static void logError(String host, int port, String value, Throwable e) {
        log.error("[Socket异常-{}] 地址：{}:{}", value, host, port, e);
    }

    public static void logRetry(String host, int port, String value, int retryCount) {
        log.warn("[Socket重试-{}] 地址：{}:{} 重试次数:{}", value, host, port, retryCount);
    }
}
