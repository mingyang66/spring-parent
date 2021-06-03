package com.emily.infrastructure.logback;

import com.emily.infrastructure.logback.builder.LogbackBuilder;
import com.emily.infrastructure.logback.utils.LoggerUtils;
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
 * @author Emily
 * @description: LogBack日志组件
 * @create: 2020/08/08
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@EnableConfigurationProperties(LogbackProperties.class)
@ConditionalOnProperty(prefix = "spring.emily.logback", name = "enabled", havingValue = "true", matchIfMissing = true)
public class LogbackAutoConfiguration implements InitializingBean, DisposableBean {

    /**
     * AccessLog对象
     */
    @Bean
    @ConditionalOnMissingBean
    public LogbackBuilder logbackBuilder(LogbackProperties properties) {
        LogbackBuilder builder = new LogbackBuilder(properties);
        //开启logback日志组件
        LoggerUtils.setBuilder(builder);
        return builder;
    }

    @Override
    public void destroy() {
        LoggerUtils.info(LogbackAutoConfiguration.class, "<== 【销毁--自动化配置】----Logback日志组件【LogbackAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() {
        LoggerUtils.info(LogbackAutoConfiguration.class, "==> 【初始化--自动化配置】----Logback日志组件【LogbackAutoConfiguration】");
    }
}
