package com.emily.infrastructure.logger;

import com.emily.infrastructure.logback.LogbackProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 日志配置属性。
 *
 * @author Emily
 * @since : Created in 2023/7/6 7:50 PM
 */
@ConfigurationProperties(prefix = LogProperties.PREFIX)
public class LogProperties extends LogbackProperties {
    public static final String PREFIX = "spring.emily.logger";
}
