package com.emily.infrastructure.rabbitmq.initializer;

import com.emily.infrastructure.rabbitmq.factory.DataRabbitFactory;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

/**
 * @author :  Emily
 * @since :  2024/8/16 下午5:03
 */
public class DataRabbitApplicationContextInitializer implements ApplicationContextInitializer<@NonNull ConfigurableApplicationContext>, Ordered {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        DataRabbitFactory.registerApplicationContext(applicationContext);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 2;
    }
}
