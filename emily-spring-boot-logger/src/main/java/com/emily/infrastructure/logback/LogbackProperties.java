package com.emily.infrastructure.logback;

import com.emily.infrastructure.logger.configuration.property.LoggerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 测试配置文件
 *
 * @author Emily
 * @since :  Created in 2023/7/6 7:50 PM
 */
@ConfigurationProperties(prefix = LogbackProperties.PREFIX)
public class LogbackProperties extends LoggerConfig {
    /**
     * 前缀
     */
    public static final String PREFIX = "spring.emily.logger";
}
