package io.casoal.framework.boot.socketclient.scanner;

import io.casoal.framework.boot.socketclient.annotation.EnableSocketClients;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 扫描器注册器，处理@EnableSocketClients注解
 */
public class SocketClientScannerRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Map<String, Object> attrs = importingClassMetadata.getAnnotationAttributes(EnableSocketClients.class.getName());

        if (attrs == null) {
            return;
        }

        List<String> basePackages = new ArrayList<>();

        // 处理basePackages
        String[] packages = (String[]) attrs.get("basePackages");
        for (String pkg : packages) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg.trim());
            }
        }

        // 处理basePackageClasses
        Class<?>[] classes = (Class<?>[]) attrs.get("basePackageClasses");
        for (Class<?> clazz : classes) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }

        // 若未指定任何扫描路径，默认扫描朱姐所在类的包
        if (basePackages.isEmpty()) {
            String defaultPackage = ClassUtils.getPackageName(importingClassMetadata.getClassName());
            basePackages.add(defaultPackage);
        }

        // 注册扫描器
        SocketClientScanner scanner = new SocketClientScanner(StringUtils.toStringArray(basePackages));
        scanner.scan(registry);
    }
}
