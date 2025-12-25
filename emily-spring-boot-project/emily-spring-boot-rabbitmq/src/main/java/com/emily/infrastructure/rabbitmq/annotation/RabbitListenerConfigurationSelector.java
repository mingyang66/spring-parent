package com.emily.infrastructure.rabbitmq.annotation;

import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotationMetadata;

/**
 * {@link org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurationSelector}
 */
@Order
public class RabbitListenerConfigurationSelector implements DeferredImportSelector {
    public RabbitListenerConfigurationSelector() {
    }

    @Override
    public String[] selectImports(@NonNull AnnotationMetadata importingClassMetadata) {
        return new String[]{MultiRabbitBootstrapConfiguration.class.getName(), RabbitBootstrapConfiguration.class.getName()};
    }
}
