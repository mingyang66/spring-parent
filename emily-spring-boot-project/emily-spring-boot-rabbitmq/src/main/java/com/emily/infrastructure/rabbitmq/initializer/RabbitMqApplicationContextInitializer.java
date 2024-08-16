package com.emily.infrastructure.rabbitmq.initializer;

import com.emily.infrastructure.rabbitmq.factory.RabbitMqFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

/**
 * @author :  姚明洋
 * @since :  2024/8/16 下午5:03
 */
public class RabbitMqApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        RabbitMqFactory.registerApplicationContext(applicationContext);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 2;
    }
}
