package com.emily.infrastructure.logback;

import com.emily.infrastructure.logback.context.LogbackContext;
import com.emily.infrastructure.logback.factory.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * @author Emily
 * @description: LogBack日志组件
 * @create: 2020/08/08
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@EnableConfigurationProperties(LogbackProperties.class)
@ConditionalOnProperty(prefix = LogbackProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class LogbackAutoConfiguration implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(LogbackAutoConfiguration.class);

    /**
     * AccessLog对象
     */
    @Bean(initMethod = "init")
    public LogbackContext logbackContext(LogbackProperties properties) {
        LogbackContext context = new LogbackContext(properties);
        LoggerFactory.context = context;
        return context;
    }

    @Override
    public void destroy() {
        logger.info("<== 【销毁--自动化配置】----Logback日志组件【LogbackAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() {
        logger.info("==> 【初始化--自动化配置】----Logback日志组件【LogbackAutoConfiguration】");
    }
}
