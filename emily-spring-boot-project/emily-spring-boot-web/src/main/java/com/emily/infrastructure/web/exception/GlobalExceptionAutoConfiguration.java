package com.emily.infrastructure.web.exception;

import com.emily.infrastructure.web.exception.handler.DefaultGlobalExceptionHandler;
import com.emily.infrastructure.web.exception.handler.GlobalExceptionCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;


/**
 * 异常捕获自动化配置类
 *
 * @author Emily
 * @since 2020/10/28
 */
@AutoConfiguration
@EnableConfigurationProperties(GlobalExceptionProperties.class)
@ConditionalOnProperty(prefix = GlobalExceptionProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class GlobalExceptionAutoConfiguration implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionAutoConfiguration.class);

    /**
     * 异常抛出拦截bean初始化
     *
     * @return 全局异常捕获切面对象
     */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean(GlobalExceptionCustomizer.class)
    public DefaultGlobalExceptionHandler defaultGlobalExceptionHandler(ApplicationContext context) {
        return new DefaultGlobalExceptionHandler(context);
    }


    @Override
    public void destroy() throws Exception {
        logger.info("<== 【销毁--自动化配置】----全局异常组件【GlobalExceptionAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("==> 【初始化--自动化配置】----全局异常组件【GlobalExceptionAutoConfiguration】");
    }
}
