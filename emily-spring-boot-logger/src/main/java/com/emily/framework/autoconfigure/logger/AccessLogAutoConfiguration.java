package com.emily.framework.autoconfigure.logger;

import com.emily.framework.common.logger.LoggerUtils;
import com.emily.framework.common.logger.builder.AccessLogBuilder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description: LogBack日志组件
 * @create: 2020/08/08
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(AccessLogProperties.class)
@ConditionalOnProperty(prefix = "spring.emily.accesslog", name = "enable", havingValue = "true", matchIfMissing = true)
public class AccessLogAutoConfiguration implements InitializingBean, DisposableBean {

    private AccessLogBuilder builder;

    /**
     * AccessLog对象
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public AccessLogBuilder defaultAccessLog(AccessLogProperties properties) {
        builder = new AccessLogBuilder(properties);
        LoggerUtils.setBuilder(builder);
        return builder;
    }

    @Override
    public void destroy() throws Exception {
        LoggerUtils.info(AccessLogAutoConfiguration.class, "【销毁--自动化配置】----logback日志组件【AccessLogAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerUtils.info(AccessLogAutoConfiguration.class, "【初始化--自动化配置】----logback日志组件【AccessLogAutoConfiguration】");
    }
}
