package com.emily.infrastructure.autoconfigure.exception;

import com.emily.infrastructure.autoconfigure.exception.handler.ExceptionAdviceHandler;
import com.emily.infrastructure.logger.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Emily
 * @program: spring-parent
 * @description: 异常捕获自动化配置类
 * @create: 2020/10/28
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ExceptionProperties.class)
@ConditionalOnProperty(prefix = ExceptionProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class ExceptionAutoConfiguration implements InitializingBean, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionAutoConfiguration.class);

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
        logger.info("<== 【销毁--自动化配置】----异常捕获组件【ExceptionAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("==> 【初始化--自动化配置】----异常捕获组件【ExceptionAutoConfiguration】");
    }
}
