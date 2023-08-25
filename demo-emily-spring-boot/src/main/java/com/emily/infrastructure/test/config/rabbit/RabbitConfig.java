package com.emily.infrastructure.test.config.rabbit;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.AutoConfiguration;

import java.io.IOException;


/**
 *
 * @author  Emily
 * @since  Created in 2022/6/10 4:48 下午
 */
@AutoConfiguration
public class RabbitConfig {

    @RabbitListener(queues = "topic.test.queue", containerFactory = "testRabbitListenerContainerFactory")
    public void handler(Channel channel, Message message) throws IOException {
        try {
            System.out.println("TEST-" + new String(message.getBody()));
        } finally {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

/*    @RabbitListener(queues = "topic.emily.queue", containerFactory = "emilyRabbitListenerContainerFactory")
    public void handlerEmily(Channel channel, Message message) throws IOException {
        try {
            System.out.println("EMILY-" + new String(message.getBody()));
        } finally {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }*/
}

