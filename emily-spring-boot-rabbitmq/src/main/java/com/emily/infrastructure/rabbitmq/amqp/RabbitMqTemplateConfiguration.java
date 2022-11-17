package com.emily.infrastructure.rabbitmq.amqp;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;

import java.util.stream.Collectors;

/**
 * @Description :  RabbitTemplate配置类 参考：{@link RabbitAutoConfiguration.RabbitTemplateConfiguration}
 * @Author :  Emily
 * @CreateDate :  Created in 2022/6/6 10:08 上午
 */
public class RabbitMqTemplateConfiguration {

    private final ObjectProvider<MessageConverter> messageConverter;

    private final ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers;

    public RabbitMqTemplateConfiguration(ObjectProvider<MessageConverter> messageConverter,
                                         ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers) {
        this.messageConverter = messageConverter;
        this.retryTemplateCustomizers = retryTemplateCustomizers;
    }

    /**
     * 创建RabbitTemplateConfigurer配置类
     *
     * @param properties
     * @return
     */
    public RabbitTemplateConfigurer createRabbitTemplateConfigurer(RabbitProperties properties) {
        RabbitTemplateConfigurer configurer = new RabbitTemplateConfigurer(properties);
        configurer.setMessageConverter(messageConverter.getIfUnique());
        configurer
                .setRetryTemplateCustomizers(retryTemplateCustomizers.orderedStream().collect(Collectors.toList()));
        return configurer;
    }

    /**
     * 创建RabbitTemplate
     *
     * @param configurer
     * @param connectionFactory
     * @return
     */
    public RabbitTemplate createRabbitTemplate(RabbitTemplateConfigurer configurer, ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate();
        configurer.configure(template, connectionFactory);
        return template;
    }

    /**
     * 创建AmqpAdmin对象
     *
     * @param connectionFactory
     * @return
     */
    public AmqpAdmin createAmqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}
