package com.emily.infrastructure.rabbitmq.amqp;

import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;

import java.util.stream.Collectors;

/**
 * @Description : 配置RabbitMQ  注解驱动端点
 * @Author :  姚明洋
 * @CreateDate :  Created in 2022/11/17 10:27 上午
 */
public class RabbitMqAnnotationDrivenConfiguration {

    private final ObjectProvider<MessageConverter> messageConverter;

    private final ObjectProvider<MessageRecoverer> messageRecoverer;

    private final ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers;

    public RabbitMqAnnotationDrivenConfiguration(ObjectProvider<MessageConverter> messageConverter,
                                                 ObjectProvider<MessageRecoverer> messageRecoverer,
                                                 ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers) {
        this.messageConverter = messageConverter;
        this.messageRecoverer = messageRecoverer;
        this.retryTemplateCustomizers = retryTemplateCustomizers;
    }

    public SimpleRabbitMqListenerContainerFactoryConfigurer simpleRabbitListenerContainerFactoryConfigurer(RabbitProperties properties) {
        SimpleRabbitMqListenerContainerFactoryConfigurer configurer = new SimpleRabbitMqListenerContainerFactoryConfigurer(properties);
        configurer.setMessageConverter(this.messageConverter.getIfUnique());
        configurer.setMessageRecoverer(this.messageRecoverer.getIfUnique());
        configurer.setRetryTemplateCustomizers(
                this.retryTemplateCustomizers.orderedStream().collect(Collectors.toList()));
        return configurer;
    }

    public DirectRabbitMqListenerContainerFactoryConfigurer directRabbitListenerContainerFactoryConfigurer(RabbitProperties properties) {
        DirectRabbitMqListenerContainerFactoryConfigurer configurer = new DirectRabbitMqListenerContainerFactoryConfigurer(properties);
        configurer.setMessageConverter(this.messageConverter.getIfUnique());
        configurer.setMessageRecoverer(this.messageRecoverer.getIfUnique());
        configurer.setRetryTemplateCustomizers(
                this.retryTemplateCustomizers.orderedStream().collect(Collectors.toList()));
        return configurer;
    }
}
