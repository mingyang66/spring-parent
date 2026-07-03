package com.emily.infrastructure.amqp.rabbit.annotation;


import com.emily.infrastructure.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;

/**
 * {@link org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer}
 */
@FunctionalInterface
public interface RabbitListenerConfigurer {
    void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar);
}

