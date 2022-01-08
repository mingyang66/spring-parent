package com.emily.infrastructure.logback.initializer;

import com.emily.infrastructure.logger.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

/**
 * @author Emily
 * @program: spring-parent
 * @description: Logback日志组件容器上下文初始化
 * @create: 2020/09/22
 */
@SuppressWarnings("all")
public class LogbackApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 2;
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        LoggerFactory.APPLICATION_CONTEXT = applicationContext;
    }
}
