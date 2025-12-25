package com.emily.infrastructure.rabbitmq.annotation;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

public class MultiRabbitBootstrapConfiguration implements ImportBeanDefinitionRegistrar, EnvironmentAware {
    private Environment environment;

    public MultiRabbitBootstrapConfiguration() {
    }

    public void registerBeanDefinitions(@Nullable AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if (this.isMultiRabbitEnabled() && !registry.containsBeanDefinition("org.springframework.amqp.rabbit.config.internalRabbitListenerAnnotationProcessor")) {
            registry.registerBeanDefinition("org.springframework.amqp.rabbit.config.internalRabbitListenerAnnotationProcessor", new RootBeanDefinition(MultiRabbitListenerAnnotationBeanPostProcessor.class));
        }

    }

    private boolean isMultiRabbitEnabled() {
        String isMultiEnabledStr = this.environment.getProperty("spring.multirabbitmq.enabled");
        return Boolean.parseBoolean(isMultiEnabledStr);
    }

    public void setEnvironment(final Environment environment) {
        this.environment = environment;
    }
}