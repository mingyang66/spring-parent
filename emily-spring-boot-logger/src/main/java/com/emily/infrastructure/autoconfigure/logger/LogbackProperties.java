package com.emily.infrastructure.autoconfigure.logger;

import com.emily.infrastructure.autoconfigure.logger.common.properties.Logback;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Emily
 * @description: 日志配置属性
 * @create: 2020/08/08
 */
@ConfigurationProperties(prefix = "spring.emily.logback")
public class LogbackProperties extends Logback {
    /**
     * 是否开启 AccessLog日志组件
     */
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
