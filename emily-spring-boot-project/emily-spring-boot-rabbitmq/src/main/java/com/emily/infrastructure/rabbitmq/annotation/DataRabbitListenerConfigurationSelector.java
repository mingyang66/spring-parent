package com.emily.infrastructure.rabbitmq.annotation;

import org.jspecify.annotations.NonNull;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurationSelector;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotationMetadata;

/**
 * {@link RabbitListenerConfigurationSelector}
 */
@Order
public class DataRabbitListenerConfigurationSelector implements DeferredImportSelector {
    public DataRabbitListenerConfigurationSelector() {
    }

    @Override
    public String[] selectImports(@NonNull AnnotationMetadata importingClassMetadata) {
        return new String[]{DataMultiRabbitBootstrapConfiguration.class.getName(), DataRabbitBootstrapConfiguration.class.getName()};
    }
}
