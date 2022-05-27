package com.yaomy.control.rabbitmq.amqp.delay;

import com.rabbitmq.client.Channel;
import com.yaomy.control.rabbitmq.amqp.delay.config.RabbitDelayConfig;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Description: RabbitMQ消息消费者（延迟队列）
 * @Version: 1.0
 */
@SuppressWarnings("all")
@Component
public class RabbitDelayReceiver {
    /**
     * @param channel 信道
     * @param message 消息
     * @throws Exception
     */
    @RabbitListener(queues = RabbitDelayConfig.DELAY_TEST_QUEUE)
    public void onMessage(Channel channel, Message message) throws Exception {
        System.out.println("--------------------------------------");
        System.out.println("消费端Payload: " + message.getPayload() + "-ID:" + message.getHeaders().getId() + "-messageId:" + message.getHeaders());
        Long deliveryTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        //手工ACK,获取deliveryTag
        channel.basicAck(deliveryTag, false);
    }

    /**
     * @param channel 信道
     * @param message 消息
     * @throws Exception
     */
    @RabbitListener(queues = RabbitDelayConfig.DELAY_TEST_QUEUE)
    public void onMessage(Channel channel, org.springframework.amqp.core.Message message) throws Exception {
        System.out.println("--------------------------------------");
        System.out.println("消费端Payload: " + new String(message.getBody()) + "-messageId:" + message.getMessageProperties().getMessageId());
        message.getMessageProperties().getHeaders().forEach((key, value) -> {
            System.out.println("header=>>" + key + "=" + value);
        });
        Long deliveryTag = message.getMessageProperties().getDeliveryTag();
        //手工ACK,获取deliveryTag
        channel.basicAck(deliveryTag, false);
    }

    /**
     * @param channel        信道
     * @param body           负载
     * @param amqp_messageId 消息唯一标识
     * @param headers        消息header
     * @throws Exception
     */
    //获取特定的消息
    @RabbitListener(queues = RabbitDelayConfig.DELAY_TEST_QUEUE)
    //@RabbitHandler
    public void handleMessage(Channel channel, @Payload byte[] body, @Header String amqp_messageId, @Headers Map<String, Object> headers) throws Exception {
        System.out.println("====消费消息===amqp_messageId:" + amqp_messageId);
        headers.keySet().forEach((key) -> {
            System.out.println("header=>>" + key + "=" + headers.get(key));
        });
        System.out.println(new String(body));
        Long deliveryTag = NumberUtils.toLong(headers.get("amqp_deliveryTag").toString());
        /**
         * 手动Ack
         */
        channel.basicAck(deliveryTag, false);
    }

    /**
     * @param channel 信道
     * @param body    负载
     * @param headers 消息header
     * @throws Exception
     */
    @RabbitListener(queues = RabbitDelayConfig.DELAY_TEST_QUEUE)
    //@RabbitHandler
    public void handleMessage(Channel channel, @Payload byte[] body, MessageHeaders headers) throws Exception {
        System.out.println("====消费消息===amqp_messageId:" + headers);
        headers.keySet().forEach((key) -> {
            System.out.println("header=>>" + key + "=" + headers.get(key));
        });
        System.out.println(new String(body));
        Long deliveryTag = NumberUtils.toLong(headers.get("amqp_deliveryTag").toString());
        /**
         * 手动Ack
         */
        channel.basicAck(deliveryTag, false);
    }
}
