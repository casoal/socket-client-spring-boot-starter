package io.casoal.framework.boot.socketclient.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class SocketClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RetryTemplate retryTemplate() {
        return new RetryTemplate();
    }
}
