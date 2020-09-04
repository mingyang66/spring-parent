package com.sgrain.boot.autoconfigure.accesslog;

import com.sgrain.boot.common.accesslog.builder.AccessLogBuilder;
import com.sgrain.boot.common.utils.LoggerUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * @description: LogBack日志组件
 * @create: 2020/08/08
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(AccessLogProperties.class)
@ConditionalOnProperty(prefix = "spring.sgrain.accesslog", name = "enable", havingValue = "true", matchIfMissing = false)
public class AccessLogAutoConfiguration implements CommandLineRunner {

    private AccessLogBuilder builder;

    /**
     * AccessLog对象
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(AccessLogBuilder.class)
    public AccessLogBuilder defaultAccessLog(AccessLogProperties properties) {
        builder = new AccessLogBuilder(properties);
        return builder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (Objects.nonNull(builder)) {
            LoggerUtils.setBuilder(builder);
            LoggerUtils.info(AccessLogAutoConfiguration.class, "【自动化配置】----logback日志组件初始化完成...");
        }
    }
}
