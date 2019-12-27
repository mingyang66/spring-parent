package com.yaomy.control.rabbitmq.amqp;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * @Description: RabbitMQ消息消费者
 * @Version: 1.0
 */
@Component
public class RabbitReceiver {

    @RabbitListener(queues = "test_queue2")
    public void onMessage(Message message, Channel channel) throws Exception {
        System.err.println("--------------------------------------");
        System.err.println("消费端Payload: " + message.getPayload()+"-ID:"+message.getHeaders().getId()+"-messageId:"+message.getHeaders());
        Long deliveryTag = (Long)message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        //手工ACK,获取deliveryTag
        channel.basicAck(deliveryTag, false);
    }
}
