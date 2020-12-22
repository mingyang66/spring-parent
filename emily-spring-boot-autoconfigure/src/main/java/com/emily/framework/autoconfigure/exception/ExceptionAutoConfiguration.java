package com.emily.framework.autoconfigure.exception;

import com.emily.framework.common.utils.log.LoggerUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
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
@ConditionalOnProperty(prefix = "spring.emily.exception", name = "enable", havingValue = "true", matchIfMissing = true)
public class ExceptionAutoConfiguration implements InitializingBean, DisposableBean {
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
    public void destroy() throws Exception {
        LoggerUtils.info(ExceptionAutoConfiguration.class, "【销毁--自动化配置】----异常捕获组件【ExceptionAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerUtils.info(ExceptionAutoConfiguration.class, "【初始化--自动化配置】----异常捕获组件【ExceptionAutoConfiguration】");
    }
}
