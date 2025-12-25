package com.emily.infrastructure.rabbitmq.annotation;


@FunctionalInterface
public interface RabbitListenerConfigurer {
    void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar);
}

