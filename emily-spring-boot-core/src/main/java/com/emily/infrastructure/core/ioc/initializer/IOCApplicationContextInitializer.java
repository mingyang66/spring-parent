package com.emily.infrastructure.core.ioc.initializer;

import com.emily.infrastructure.core.ioc.IOCContext;
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

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        // 初始化容器上下文
        IOCContext.setApplicationContext(applicationContext);
    }
}
