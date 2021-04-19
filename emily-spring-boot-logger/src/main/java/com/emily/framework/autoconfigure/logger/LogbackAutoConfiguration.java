package com.emily.framework.autoconfigure.logger;

import com.emily.framework.common.logger.LoggerUtils;
import com.emily.framework.common.logger.builder.LogbackBuilder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * @description: LogBack日志组件
 * @create: 2020/08/08
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@EnableConfigurationProperties(LogbackProperties.class)
@ConditionalOnProperty(prefix = "spring.emily.logback", name = "enabled", havingValue = "true", matchIfMissing = true)
public class LogbackAutoConfiguration implements InitializingBean, DisposableBean {

    private LogbackBuilder builder;

    /**
     * AccessLog对象
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public LogbackBuilder defaultAccessLog(LogbackProperties properties) {
        builder = new LogbackBuilder(properties);
        LoggerUtils.setBuilder(builder);
        return builder;
    }

    @Override
    public void destroy() throws Exception {
        LoggerUtils.info(LogbackAutoConfiguration.class, "【销毁--自动化配置】----Logback日志组件【LogbackAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerUtils.info(LogbackAutoConfiguration.class, "【初始化--自动化配置】----Logback日志组件【LogbackAutoConfiguration】");
    }
}
