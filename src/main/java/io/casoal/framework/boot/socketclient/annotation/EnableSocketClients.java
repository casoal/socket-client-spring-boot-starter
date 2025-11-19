package io.casoal.framework.boot.socketclient.annotation;

import io.casoal.framework.boot.socketclient.scanner.SocketClientScannerRegistrar;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 开启Socket客户端并配置扫描路径
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SocketClientScannerRegistrar.class)
@Documented
public @interface EnableSocketClients {
    /**
     * 扫描基础包路径
     */
    String[] basePackages() default {};

    /**
     * 直接指定扫描的类(用于推到包路径)
     */
    Class<?>[] basePackageClasses() default {};
}
