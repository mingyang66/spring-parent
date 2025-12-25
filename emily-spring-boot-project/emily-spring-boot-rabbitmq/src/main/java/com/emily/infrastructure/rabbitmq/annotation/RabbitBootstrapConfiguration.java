package com.emily.infrastructure.rabbitmq.annotation;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class RabbitBootstrapConfiguration implements ImportBeanDefinitionRegistrar {
    public RabbitBootstrapConfiguration() {
    }

    public void registerBeanDefinitions(@Nullable AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition("org.springframework.amqp.rabbit.config.internalRabbitListenerAnnotationProcessor")) {
            registry.registerBeanDefinition("org.springframework.amqp.rabbit.config.internalRabbitListenerAnnotationProcessor", new RootBeanDefinition(RabbitListenerAnnotationBeanPostProcessor.class));
        }

        if (!registry.containsBeanDefinition("org.springframework.amqp.rabbit.config.internalRabbitListenerEndpointRegistry")) {
            registry.registerBeanDefinition("org.springframework.amqp.rabbit.config.internalRabbitListenerEndpointRegistry", new RootBeanDefinition(RabbitListenerEndpointRegistry.class));
        }

    }
}
