package io.casoal.framework.boot.socketclient.scanner;

import io.casoal.framework.boot.socketclient.annotation.SocketClient;
import io.casoal.framework.boot.socketclient.autoconfigure.ConfigResolver;
import io.casoal.framework.boot.socketclient.proxy.SocketClientProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Modifier;
import java.util.Set;

/**
 * Socket客户端接口扫描器
 */
@Slf4j
public class SocketClientScanner {
    private final String[] basePackages;

    public SocketClientScanner(String[] basePackages) {
        this.basePackages = basePackages;
        log.info("Socket客户端扫描路径：{}", StringUtils.arrayToCommaDelimitedString(basePackages));
    }

    public void scan(BeanDefinitionRegistry registry) {
        // 创建类路径扫描器
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                // 必须是接口
                if (!beanDefinition.getMetadata().isInterface()) {
                    return false;
                }

                // 排除内部接口（类名包含“$”）
                String className = beanDefinition.getMetadata().getClassName();
                if (className.contains("$")) {
                    log.debug("过滤内部接口：{}", className);
                    return false;
                }

                // 判断是否为public接口（通过反射）
                try {
                    Class<?> interfaceClass = ClassUtils.forName(className, getClass().getClassLoader());
                    if (!Modifier.isPublic(interfaceClass.getModifiers())) {
                        log.debug("过滤非public接口：{}", className);
                        return false;
                    }
                } catch (ClassNotFoundException e) {
                    log.warn("类加载失败，跳过校验：{}", className, e);
                    return false;
                }

                return true;
            }
        };
        AnnotationTypeFilter filter = new AnnotationTypeFilter(SocketClient.class);
        scanner.addIncludeFilter(filter);

        // 扫描指定包
        for (String basePackage : basePackages) {
            Set<BeanDefinition> definitions = scanner.findCandidateComponents(basePackage);

            if (definitions.isEmpty()) {
                log.warn("包[{}]中未发现@SocketClient注解接口", basePackage);
            }

            log.warn("扫描包路径[{}]，发现{}个Socket客户端接口", basePackage, definitions.size());
            for (BeanDefinition definition : definitions) {
                registerBeanDefinition(registry, definition);
            }
        }
    }

    private void registerBeanDefinition(BeanDefinitionRegistry registry, BeanDefinition definition) {
        String className = definition.getBeanClassName();
        if (className == null) {
            return;
        }

        // 注册工厂bean
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(SocketClientFactoryBean.class);
        builder.addPropertyValue("interfaceClass", className);
        registry.registerBeanDefinition(className, builder.getBeanDefinition());
    }
}
