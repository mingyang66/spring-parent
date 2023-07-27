package com.emily.infrastructure.rabbitmq.amqp;

import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * @author Emily
 * @since Created in 2022/6/8 4:24 下午
 */
public class RabbitMqMessagingTemplateConfiguration {
    public RabbitMessagingTemplate rabbitMessagingTemplate(RabbitTemplate rabbitTemplate) {
        return new RabbitMessagingTemplate(rabbitTemplate);
    }
}
