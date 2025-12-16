package com.emily.sample.rabbitmq.rabbit;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.messaging.handler.annotation.Header;

import java.io.IOException;


/**
 * @author Emily
 * @since Created in 2022/6/10 4:48 下午
 */

@Configuration
@DependsOn(value = {"rabbitListenerContainerFactory"})
public class RabbitConfig {
    public RabbitConfig() {
        System.out.println("------RabbitConfig-----");
    }

    @RabbitListener(queues = "emily.test.queue", containerFactory = "emilyRabbitListenerContainerFactory")
    public void handler(Channel channel, Message message) throws IOException {
        long deliveryTag =message.getMessageProperties().getDeliveryTag();
        try {
            String contentType = message.getMessageProperties().getContentType();
            System.out.println("EMILY-" + new String(message.getBody()));
            channel.basicAck(deliveryTag, false);
        } finally {
            channel.basicReject(deliveryTag, false);
        }
    }

    @RabbitListener(queues = "topic.test.queue")
    public void handlerEmily(Channel channel, Message message) throws IOException {
        long deliveryTag =message.getMessageProperties().getDeliveryTag();
        try {
            String contentType = message.getMessageProperties().getContentType();
            System.out.println("TEST-" + new String(message.getBody()));
            channel.basicAck(deliveryTag, false);
        } finally {
            channel.basicReject(deliveryTag, false);
        }
    }


}
