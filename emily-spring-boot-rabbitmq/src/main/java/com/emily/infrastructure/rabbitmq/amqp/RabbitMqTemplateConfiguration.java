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
import org.springframework.retry.support.RetryTemplate;

import java.util.stream.Collectors;

/**
 * RabbitTemplate配置类 参考：RabbitAutoConfiguration.RabbitTemplateConfiguration
 *
 * @author Emily
 * @since Created in 2022/6/6 10:08 上午
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
     * @param properties 属性配置
     * @return 模板配置类
     */
    public RabbitTemplateConfigurer createRabbitTemplateConfigurer(RabbitProperties properties) {
        RabbitTemplateConfigurer configurer = new RabbitTemplateConfigurer(properties);
        configurer.setMessageConverter(messageConverter.getIfUnique());
        configurer.setRetryTemplateCustomizers(retryTemplateCustomizers.orderedStream().collect(Collectors.toList()));
        return configurer;
    }

    /**
     * 创建RabbitTemplate
     *
     * @param configurer        模板配置类
     * @param connectionFactory 连接工厂
     * @return RabbitTemplate对象
     */
    public RabbitTemplate createRabbitTemplate(RabbitTemplateConfigurer configurer, ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate();
        configurer.configure(template, connectionFactory);
        return template;
    }

    /**
     * 创建AmqpAdmin对象
     *
     * @param connectionFactory 连接工厂
     * @return AmqpAdmin对象
     */
    public AmqpAdmin createAmqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}
