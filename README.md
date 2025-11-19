# Socket Client Spring Boot Starter

一个基于Spring Boot的Socket客户端 Starter，用于简化Socket客户端的开发，支持通过注解方式快速定义和使用Socket客户端接口。

## 功能特点

- 基于注解的Socket客户端接口定义
- 支持自定义序列化方式（默认JSON）
- 内置请求重试机制
- 支持配置连接超时和读取超时
- 支持长度前缀模式的数据传输
- 自动扫描并注册Socket客户端接口

## 快速开始

### 1. 添加依赖

在`pom.xml`中添加以下依赖：

```xml
<dependency>
    <groupId>io.casoal.framework.boot</groupId>
    <artifactId>socket-client-spring-boot-starter</artifactId>
    <version>2.3.7.RELEASE</version>
</dependency>
```

### 2. 定义Socket客户端接口

创建接口并使用`@SocketClient`注解标记，方法使用`@SocketMapping`注解标记：

```java
@SocketClient(host = "${socket.server.host}", port = "${socket.server.port}")
public interface DemoSocketClient {

    @SocketMapping("sendMessage")
    String sendMessage(String content);
    
    @SocketMapping("getUser")
    User getUser(Long userId);
}
```

### 3. 启用Socket客户端

在Spring Boot启动类上添加`@EnableSocketClients`注解：

```java
@SpringBootApplication
@EnableSocketClients(basePackages = "io.casoal.demo.client")
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

### 4. 使用Socket客户端

直接注入接口并使用：

```java
@Service
public class DemoService {
    
    @Autowired
    private DemoSocketClient socketClient;
    
    public String send(String message) {
        return socketClient.sendMessage(message);
    }
}
```

## 配置说明

### 全局配置

可以在`application.properties`中配置默认值：

```properties
# 默认Socket服务端地址
socket.client.default.host=localhost
# 默认端口
socket.client.default.port=8888
# 默认连接超时时间(ms)
socket.client.default.connectTimeout=3000
# 默认读取超时时间(ms)
socket.client.default.readTimeout=5000
# 默认最大重试次数(-1表示不重试)
socket.client.default.retryMaxAttempts=-1
# 默认重试间隔(ms)
socket.client.default.retryBackoff=1000
```

### 接口级别配置

通过`@SocketClient`注解的属性可以覆盖全局配置：

```java
@SocketClient(
    host = "127.0.0.1",
    port = "9090",
    connectTimeout = "5000",
    readTimeout = "10000",
    useLengthPrefix = true,
    retryMaxAttempts = "3",
    retryBackoff = "2000",
    serializer = XmlSerializer.class
)
public interface CustomSocketClient {
    // ...
}
```

## 高级特性

### 重试配置

可以通过`@SocketRetry`注解为方法单独配置重试策略：

```java
@SocketClient(host = "${socket.server.host}", port = "${socket.server.port}")
public interface DemoSocketClient {

    @SocketMapping("sendMessage")
    @SocketRetry(maxAttempts = 3, backoff = 1000)
    String sendMessage(String content);
}
```

### 自定义序列化器

实现`Serializer`接口创建自定义序列化器：

```java
public class CustomSerializer implements Serializer {
    @Override
    public String getName() {
        return "custom";
    }
    
    @Override
    public byte[] serialize(Object obj) throws IOException {
        // 实现序列化逻辑
    }
    
    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws IOException {
        // 实现反序列化逻辑
    }
}
```

然后在`@SocketClient`注解中指定：

```java
@SocketClient(
    host = "127.0.0.1",
    port = "9090",
    serializer = CustomSerializer.class
)
public interface CustomSocketClient {
    // ...
}
```

### 长度前缀模式

当启用长度前缀模式时，会在发送数据前添加4字节的int类型长度前缀：

```java
@SocketClient(
    host = "127.0.0.1",
    port = "9090",
    useLengthPrefix = true
)
public interface LengthPrefixedSocketClient {
    // ...
}
```
