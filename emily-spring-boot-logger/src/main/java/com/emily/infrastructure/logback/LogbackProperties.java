package com.emily.infrastructure.logback;

import com.emily.infrastructure.logger.configuration.property.LoggerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 测试配置文件
 *
 * @author :  姚明洋
 * @since :  Created in 2023/7/6 7:50 PM
 */
@ConfigurationProperties(prefix = LogbackProperties.PREFIX)
public class LogbackProperties extends LoggerProperties {
    /**
     * 前缀
     */
    public static final String PREFIX = "spring.emily.logger";
}
