package com.sgrain.boot.autoconfigure.exception;

import com.sgrain.boot.common.utils.LoggerUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: spring-parent
 * @description: 异常捕获自动化配置类
 * @create: 2020/10/28
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ExceptionProperties.class)
@ConditionalOnProperty(prefix = "spring.sgrain.exception", name = "enable", havingValue = "true", matchIfMissing = true)
public class ExceptionAutoConfiguration implements CommandLineRunner {
    /**
     * 异常抛出拦截bean初始化
     *
     * @return
     */
    @Bean
    public ExceptionAdviceHandler exceptionAdviceHandler() {
        return new ExceptionAdviceHandler();
    }

    @Override
    public void run(String... args) throws Exception {
        LoggerUtils.info(ExceptionAutoConfiguration.class, "【自动化配置】----异常捕获组件初始化完成...");
    }
}
