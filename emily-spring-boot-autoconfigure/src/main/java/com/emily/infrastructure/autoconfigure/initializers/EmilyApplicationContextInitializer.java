package com.emily.infrastructure.autoconfigure.initializers;

import com.emily.infrastructure.context.ioc.IOCContext;
import com.emily.infrastructure.logback.utils.LoggerUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

/**
 * @author Emily
 * @program: spring-parent
 * @description: Emily框架初始化器
 * @create: 2020/09/22
 */
public class EmilyApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        // 初始化容器上下文
        IOCContext.setApplicationContext(applicationContext);
        LoggerUtils.info(EmilyApplicationContextInitializer.class, "==> Emily Infrastructure IOC容器上下文开始初始化...");
    }
}
