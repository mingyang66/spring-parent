package com.sgrain.boot.autoconfigure.accesslog;

import com.sgrain.boot.common.accesslog.builder.AccessLogBuilder;
import com.sgrain.boot.common.utils.LoggerUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: spring-parent
 * @description: LogBack日志组件
 * @author: 姚明洋
 * @create: 2020/08/08
 */
@Configuration
@EnableConfigurationProperties(AccessLogProperties.class)
@ConditionalOnProperty(prefix = "spring.sgrain.accesslog", name = "enable", havingValue = "true", matchIfMissing = false)
public class AccessLogAutoConfiguration {

    /**
     * AccessLog对象
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(AccessLogProperties.class)
    public AccessLogBuilder defaultAccessLog(AccessLogProperties properties){
        AccessLogBuilder builder = new AccessLogBuilder(properties);
        LoggerUtils.setBuilder(builder);
        return builder;
    }

}
