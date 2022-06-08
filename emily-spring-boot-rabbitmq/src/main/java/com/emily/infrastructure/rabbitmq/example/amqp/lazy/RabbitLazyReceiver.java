package com.emily.infrastructure.rabbitmq.example.amqp.lazy;

import com.emily.infrastructure.rabbitmq.example.amqp.lazy.config.RabbitLazyConfig;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * @Description: RabbitMQ消息消费者
 * @Version: 1.0
 */
@SuppressWarnings("all")
@Component
public class RabbitLazyReceiver {
    /**
     * @param channel 信道
     * @param message 消息
     * @throws Exception
     */
    @RabbitListener(queues = RabbitLazyConfig.LAZY_TOPIC_QUEUE)
    public void onMessage(Channel channel, Message message) throws Exception {
        System.out.println("--------------------------------------");
        System.out.println("消费端Payload: " + message.getPayload() + "-ID:" + message.getHeaders().getId() + "-messageId:" + message.getHeaders());
        Long deliveryTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        //手工ACK,获取deliveryTag
        channel.basicAck(deliveryTag, false);
    }
}
