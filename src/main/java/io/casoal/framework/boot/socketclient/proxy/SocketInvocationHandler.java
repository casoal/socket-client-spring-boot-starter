package io.casoal.framework.boot.socketclient.proxy;

import io.casoal.framework.boot.socketclient.annotation.SocketClient;
import io.casoal.framework.boot.socketclient.annotation.SocketMapping;
import io.casoal.framework.boot.socketclient.autoconfigure.ConfigResolver;
import io.casoal.framework.boot.socketclient.retry.RetryConfigResolver;
import io.casoal.framework.boot.socketclient.retry.SocketRetryTemplate;
import io.casoal.framework.boot.socketclient.serializer.Serializer;
import io.casoal.framework.boot.socketclient.util.SocketLogUtils;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

public class SocketInvocationHandler implements InvocationHandler {
    private final String host;
    private final int port;
    private final int connectTimeout;
    private final int readTimeout;
    private final Serializer serializer;
    private final boolean useLengthPrefix;
    private final SocketClient classAnnotation;
    private final ConfigResolver resolver;

    public SocketInvocationHandler(ConfigResolver resolver, SocketClient annotation, Serializer serializer) {
        this.resolver = resolver;
        this.classAnnotation = annotation;
        this.host = resolver.resolveString(annotation.host());
        this.port = resolver.resolveInt(annotation.port());
        this.connectTimeout = resolver.resolveInt(annotation.connectTimeout());
        this.readTimeout = resolver.resolveInt(annotation.readTimeout());
        this.serializer = serializer;
        this.useLengthPrefix = annotation.useLengthPrefix();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 处理Object类方法
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }

        SocketMapping mapping = method.getAnnotation(SocketMapping.class);
        String value = mapping != null ? mapping.value() : method.getName();
        long startTime = System.currentTimeMillis();

        // 解析重试配置
        RetryConfigResolver.SocketRetryMeta retryMeta = RetryConfigResolver.resolve(method, resolver,classAnnotation);

        // 不重试
        if (retryMeta.getMaxAttempts() < 1) {
            return doInvoke(method, args, value, startTime);
        }

        RetryTemplate retryTemplate = SocketRetryTemplate.create(retryMeta);
        return retryTemplate.execute((RetryCallback<Object, Throwable>) context -> {
            context.setAttribute("host", host);
            context.setAttribute("port", port);
            context.setAttribute("value", value);
            return doInvoke(method, args, value, startTime);
        });
    }

    private Object doInvoke(Method method, Object[] args, String value, long startTime) throws Exception {
        try (Socket socket = new Socket()) { // socket关闭时会自动关闭流
            // 建立连接
            socket.connect(new InetSocketAddress(host, port), connectTimeout);
            socket.setSoTimeout(readTimeout);

            // 序列化请求参数
            byte[] requestData = serializer.serialize(args);
            SocketLogUtils.logRequest(host, port, value, requestData.length, startTime);

            // 不能使用try-with-resources管理os流，会导致os关闭的时候socket一起被关闭，无法读取数据
            // 发送数据
            OutputStream os = socket.getOutputStream();
            sendData(os, requestData);

            // 读取响应
            InputStream is = socket.getInputStream();
            byte[] responseData = receiveData(is);
            SocketLogUtils.logResponse(host, port, value, responseData.length, startTime);
            return serializer.deserialize(responseData, method.getReturnType());

        } catch (Exception e) {
            SocketLogUtils.logError(host, port, value, e);
            throw e;
        }
    }

    /**
     * 发送数据（处理长度前缀）
     */
    private void sendData(OutputStream os, byte[] data) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);
        if (useLengthPrefix) {
            dos.writeInt(data.length);
        }
        dos.write(data);
        dos.flush();
    }

    /**
     * 接受数据（处理长度前缀）
     */
    private byte[] receiveData(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);
        int dataLength = -1;

        // 读取长度前缀
        if (useLengthPrefix) {
            dataLength = dis.readInt();
        }

        // 读取数据内容
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int totalRead = 0;
        int bytesRead;

        while (true) {
            bytesRead = dis.read(buffer);
            if (bytesRead == -1) {
                break;
            }

            result.write(buffer, 0, bytesRead);
            totalRead = +bytesRead;

            // 长度匹配时停止读取
            if (useLengthPrefix && totalRead >= dataLength) {
                break;
            }
        }

        // 截取有效数据
        byte[] allData = result.toByteArray();
        return useLengthPrefix ? Arrays.copyOf(allData, dataLength) : allData;
    }
}
