package com.emily.infrastructure.test.config.rabbit;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;


/**
 * @author Emily
 * @since Created in 2022/6/10 4:48 下午
 */
@Configuration
public class RabbitConfig {
    @RabbitListener(queues = "topic.emily.queue", containerFactory = "emilyRabbitListenerContainerFactory", priority = "2")
    public void handler(Channel channel, Message message) throws IOException {
        try {
            String contentType = message.getMessageProperties().getContentType();
            System.out.println("EMILY-" + new String(message.getBody()));
        } finally {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }
    @RabbitListener(queues = "topic.test.queue", priority = "3", containerFactory = "rabbitListenerContainerFactory")
    public void handlerEmily(Channel channel, Message message) throws IOException {
        try {
            String contentType = message.getMessageProperties().getContentType();
            System.out.println("TEST-" + new String(message.getBody()));
        } finally {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }


}

