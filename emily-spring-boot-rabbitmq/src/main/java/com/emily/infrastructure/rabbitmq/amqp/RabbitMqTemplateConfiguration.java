package com.emily.infrastructure.rabbitmq.amqp;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;

import java.util.stream.Collectors;

/**
 * @Description :  RabbitTemplate配置类
 * @Author :  Emily
 * @CreateDate :  Created in 2022/6/6 10:08 上午
 */
public class RabbitMqTemplateConfiguration {

    public RabbitTemplateConfigurer rabbitTemplateConfigurer(RabbitProperties properties,
                                                             ObjectProvider<MessageConverter> messageConverter,
                                                             ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers) {
        RabbitTemplateConfigurer configurer = new RabbitTemplateConfigurer(properties);
        configurer.setMessageConverter(messageConverter.getIfUnique());
        configurer
                .setRetryTemplateCustomizers(retryTemplateCustomizers.orderedStream().collect(Collectors.toList()));
        return configurer;
    }

    public RabbitTemplate rabbitTemplate(RabbitTemplateConfigurer configurer, ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate();
        configurer.configure(template, connectionFactory);
        return template;
    }

    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}
