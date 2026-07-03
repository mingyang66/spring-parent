package com.emily.infrastructure.amqp.autoconfigure;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.amqp.autoconfigure.AbstractRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.amqp.autoconfigure.RabbitListenerRetrySettingsCustomizer;
import org.springframework.boot.amqp.autoconfigure.RabbitProperties;
import org.springframework.boot.amqp.autoconfigure.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.context.properties.PropertyMapper;

import java.util.List;

/**
 * RabbitMQ监听器工厂配置类 Simple模式 {@link SimpleRabbitListenerContainerFactoryConfigurer}
 *
 * @author Emily
 * @since Created in 2022/11/17 10:34 上午
 */
public final class DataSimpleRabbitListenerContainerFactoryConfigurer extends AbstractRabbitListenerContainerFactoryConfigurer<@NonNull SimpleRabbitListenerContainerFactory> {
    public DataSimpleRabbitListenerContainerFactoryConfigurer(RabbitProperties rabbitProperties) {
        super(rabbitProperties);
    }

    @Override
    protected void setMessageConverter(MessageConverter messageConverter) {
        super.setMessageConverter(messageConverter);
    }

    @Override
    protected void setMessageRecoverer(MessageRecoverer messageRecoverer) {
        super.setMessageRecoverer(messageRecoverer);
    }


    @Override
    protected void setRetrySettingsCustomizers(@Nullable List<RabbitListenerRetrySettingsCustomizer> retrySettingsCustomizers) {
        super.setRetrySettingsCustomizers(retrySettingsCustomizers);
    }

    @Override
    public void configure(SimpleRabbitListenerContainerFactory factory, @NonNull ConnectionFactory connectionFactory) {
        PropertyMapper map = PropertyMapper.get();
        RabbitProperties.SimpleContainer config = getRabbitProperties().getListener().getSimple();
        configure(factory, connectionFactory, config);
        map.from(config::getConcurrency).to(factory::setConcurrentConsumers);
        map.from(config::getMaxConcurrency).to(factory::setMaxConcurrentConsumers);
        map.from(config::getBatchSize).to(factory::setBatchSize);
        map.from(config::isConsumerBatchEnabled).to(factory::setConsumerBatchEnabled);
    }

}
