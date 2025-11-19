package io.casoal.framework.boot.socketclient.annotation;

import java.lang.annotation.*;

/**
 * 方法映射注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SocketMapping {
    /**
     * 自定义命令标识
     */
    String value() default "";
}
