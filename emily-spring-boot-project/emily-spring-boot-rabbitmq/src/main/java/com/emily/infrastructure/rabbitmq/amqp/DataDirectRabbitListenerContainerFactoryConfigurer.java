package com.emily.infrastructure.rabbitmq.amqp;

import org.jspecify.annotations.NonNull;
import org.springframework.amqp.rabbit.config.DirectRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.amqp.autoconfigure.AbstractRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.amqp.autoconfigure.RabbitListenerRetrySettingsCustomizer;
import org.springframework.boot.amqp.autoconfigure.RabbitProperties;
import org.springframework.boot.context.properties.PropertyMapper;

import java.util.List;

/**
 * RabbitMQ监听器工厂配置类 Direct模式
 *
 * @author Emily
 * @since Created in 2022/11/17 10:32 上午
 */
public final class DataDirectRabbitListenerContainerFactoryConfigurer extends AbstractRabbitListenerContainerFactoryConfigurer<@NonNull DirectRabbitListenerContainerFactory> {

    public DataDirectRabbitListenerContainerFactoryConfigurer(RabbitProperties rabbitProperties) {
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
    protected void setRetrySettingsCustomizers(List<RabbitListenerRetrySettingsCustomizer> retrySettingsCustomizers) {
        super.setRetrySettingsCustomizers(retrySettingsCustomizers);
    }

    @Override
    public void configure(DirectRabbitListenerContainerFactory factory, @NonNull ConnectionFactory connectionFactory) {
        PropertyMapper map = PropertyMapper.get();
        RabbitProperties.DirectContainer config = getRabbitProperties().getListener().getDirect();
        configure(factory, connectionFactory, config);
        map.from(config::getConsumersPerQueue).to(factory::setConsumersPerQueue);
    }
}
