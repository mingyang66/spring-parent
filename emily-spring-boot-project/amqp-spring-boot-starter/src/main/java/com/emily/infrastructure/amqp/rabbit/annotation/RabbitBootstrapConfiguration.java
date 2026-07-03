package com.emily.infrastructure.amqp.rabbit.annotation;

import com.emily.infrastructure.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.jspecify.annotations.Nullable;
import org.springframework.amqp.rabbit.config.RabbitListenerConfigUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * {@link org.springframework.amqp.rabbit.annotation.RabbitBootstrapConfiguration}
 */
public class RabbitBootstrapConfiguration implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(@Nullable AnnotationMetadata importingClassMetadata,
                                        BeanDefinitionRegistry registry) {

        if (!registry.containsBeanDefinition(
                RabbitListenerConfigUtils.RABBIT_LISTENER_ANNOTATION_PROCESSOR_BEAN_NAME)) {

            registry.registerBeanDefinition(RabbitListenerConfigUtils.RABBIT_LISTENER_ANNOTATION_PROCESSOR_BEAN_NAME,
                    new RootBeanDefinition(RabbitListenerAnnotationBeanPostProcessor.class));
        }

        if (!registry.containsBeanDefinition(RabbitListenerConfigUtils.RABBIT_LISTENER_ENDPOINT_REGISTRY_BEAN_NAME)) {
            registry.registerBeanDefinition(RabbitListenerConfigUtils.RABBIT_LISTENER_ENDPOINT_REGISTRY_BEAN_NAME,
                    new RootBeanDefinition(RabbitListenerEndpointRegistry.class));
        }
    }
}
