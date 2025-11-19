package io.casoal.framework.boot.socketclient.scanner;


import io.casoal.framework.boot.socketclient.autoconfigure.ConfigResolver;
import io.casoal.framework.boot.socketclient.proxy.SocketClientProxy;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;

/**
 * 工厂Bean，用于创建Socket客户端代理对象
 *
 * @author casoal
 * @version 1.0
 * @since 2025/11/19
 */
public class SocketClientFactoryBean<T> implements FactoryBean<T>, EnvironmentAware {

    private Environment environment;

    private Class<T> interfaceClass;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public T getObject() throws Exception {
        return SocketClientProxy.create(interfaceClass, new ConfigResolver(environment));
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    public void setInterfaceClass(String className) throws ClassNotFoundException {
        this.interfaceClass = (Class<T>) ClassUtils.forName(className, SocketClientFactoryBean.class.getClassLoader());
    }
}
