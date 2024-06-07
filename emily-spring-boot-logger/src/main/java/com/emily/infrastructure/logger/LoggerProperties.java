package com.emily.infrastructure.logger;

import com.emily.infrastructure.logback.LogbackProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 测试配置文件
 *
 * @author Emily
 * @since :  Created in 2023/7/6 7:50 PM
 */
@ConfigurationProperties(prefix = LoggerProperties.PREFIX)
public class LoggerProperties extends LogbackProperties {
    /**
     * 前缀
     */
    public static final String PREFIX = "spring.emily.logger";
}
