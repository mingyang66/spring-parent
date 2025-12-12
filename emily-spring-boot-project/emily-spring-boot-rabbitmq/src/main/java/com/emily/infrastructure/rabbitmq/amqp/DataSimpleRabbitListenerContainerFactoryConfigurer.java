package com.emily.infrastructure.rabbitmq.amqp;

import org.jspecify.annotations.Nullable;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.amqp.autoconfigure.AbstractRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.amqp.autoconfigure.RabbitListenerRetrySettingsCustomizer;
import org.springframework.boot.amqp.autoconfigure.RabbitProperties;
import org.springframework.boot.context.properties.PropertyMapper;

import java.util.List;
import java.util.Objects;

/**
 * RabbitMQ监听器工厂配置类 Simple模式
 *
 * @author Emily
 * @since Created in 2022/11/17 10:34 上午
 */
public final class DataSimpleRabbitListenerContainerFactoryConfigurer extends AbstractRabbitListenerContainerFactoryConfigurer<SimpleRabbitListenerContainerFactory> {
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

    public void configure(SimpleRabbitListenerContainerFactory factory, ConnectionFactory connectionFactory) {
        PropertyMapper map = PropertyMapper.get();
        RabbitProperties.SimpleContainer config = this.getRabbitProperties().getListener().getSimple();
        this.configure(factory, connectionFactory, config);
        Objects.requireNonNull(config);
        PropertyMapper.Source<Integer> var10000 = map.from(config::getConcurrency);
        Objects.requireNonNull(factory);
        var10000.to(factory::setConcurrentConsumers);
        Objects.requireNonNull(config);
        var10000 = map.from(config::getMaxConcurrency);
        Objects.requireNonNull(factory);
        var10000.to(factory::setMaxConcurrentConsumers);
        Objects.requireNonNull(config);
        var10000 = map.from(config::getBatchSize);
        Objects.requireNonNull(factory);
        var10000.to(factory::setBatchSize);
        Objects.requireNonNull(config);
        PropertyMapper.Source<Boolean> var100000 = map.from(config::isConsumerBatchEnabled);
        Objects.requireNonNull(factory);
        var100000.to(factory::setConsumerBatchEnabled);
    }

}
