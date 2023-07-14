package com.emily.infrastructure.logback;

import com.emily.infrastructure.logger.configuration.property.LoggerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description :  测试配置文件
 * @Author :  姚明洋
 * @CreateDate :  Created in 2023/7/6 7:50 PM
 */
@ConfigurationProperties(prefix = LogbackProperties.PREFIX)
public class LogbackProperties extends LoggerProperties {
    /**
     * 前缀
     */
    public static final String PREFIX = "spring.emily.logger";
}
