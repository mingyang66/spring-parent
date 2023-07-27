package com.emily.infrastructure.rabbitmq.amqp;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.AbstractRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;
import org.springframework.boot.context.properties.PropertyMapper;

import java.util.List;

/**
 * RabbitMQ监听器工厂配置类 Simple模式
 *
 * @author Emily
 * @since Created in 2022/11/17 10:34 上午
 */
public class SimpleRabbitMqListenerContainerFactoryConfigurer extends AbstractRabbitListenerContainerFactoryConfigurer<SimpleRabbitListenerContainerFactory> {
    public SimpleRabbitMqListenerContainerFactoryConfigurer(RabbitProperties rabbitProperties) {
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
    protected void setRetryTemplateCustomizers(List<RabbitRetryTemplateCustomizer> retryTemplateCustomizers) {
        super.setRetryTemplateCustomizers(retryTemplateCustomizers);
    }

    @Override
    public void configure(SimpleRabbitListenerContainerFactory factory, ConnectionFactory connectionFactory) {
        PropertyMapper map = PropertyMapper.get();
        RabbitProperties.SimpleContainer config = getRabbitProperties().getListener().getSimple();
        configure(factory, connectionFactory, config);
        map.from(config::getConcurrency).whenNonNull().to(factory::setConcurrentConsumers);
        map.from(config::getMaxConcurrency).whenNonNull().to(factory::setMaxConcurrentConsumers);
        map.from(config::getBatchSize).whenNonNull().to(factory::setBatchSize);
        map.from(config::isConsumerBatchEnabled).to(factory::setConsumerBatchEnabled);
    }

}
