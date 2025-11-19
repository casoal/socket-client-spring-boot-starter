package io.casoal.framework.boot.socketclient.autoconfigure;

import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

public class ConfigResolver {
    private final Environment environment;

    public ConfigResolver(Environment environment) {
        this.environment = environment;
    }

    public String resolveString(String value) {
        if (!StringUtils.hasText(value)) {
            return value;
        }
        return environment.resolvePlaceholders(value);
    }

    public int resolveInt(String value) {
        String resolved = resolveString(value);
        return Integer.parseInt(resolved);
    }

    public long resolveLong(String value) {
        String resolved = resolveString(value);
        return Long.parseLong(resolved);
    }
}
