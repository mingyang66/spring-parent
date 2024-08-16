package com.emily.infrastructure.tracing.ioc;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

/**
 * 应用程序上下文初始化l
 *
 * @author :  Emily
 * @see SpringApplication->prepareContext
 * @since :  2023/10/14 8:35 PM
 */
public class IocApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        IocUtils.setApplicationContext(applicationContext);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
