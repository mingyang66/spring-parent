package com.emily.infrastructure.context.ioc.initializer;

import com.emily.infrastructure.context.ioc.IOCContext;
import com.emily.infrastructure.logback.factory.LogbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

/**
 * @author Emily
 * @program: spring-parent
 * @description: Emily框架初始化器
 * @create: 2020/09/22
 */
@SuppressWarnings("all")
public class IOCApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(IOCApplicationContextInitializer.class);

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        // 初始化容器上下文
        IOCContext.setApplicationContext(applicationContext);
        logger.info("==> Emily Infrastructure IOC容器上下文开始初始化...");
    }
}
