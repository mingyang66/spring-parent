package com.yaomy.control.rabbitmq.amqp;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * @Description: RabbitMQ生产者
 * @ProjectName: spring-parent
 * @Version: 1.0
 */
@SuppressWarnings("all")
@Component
public class RabbitSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    /**
     * 回调接口
     */
    final RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        /**
         *
         * @param correlationData 回调相关数据
         * @param ack true for ack, false for nack
         * @param cause 是一个可选的原因，对于nack，如果可用，否则为空。
         */
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            if(!ack){
                //可以进行日志记录、异常处理、补偿处理等
                System.err.println("异常ack-"+ack+",id-"+correlationData.getId()+",PayLoad-"+new String(correlationData.getReturnedMessage().getBody()));
            }else {
                //更新数据库，可靠性投递机制
                System.out.println("正常ack-"+ack+",id-"+correlationData.getId()+",PayLoad-"+new String(correlationData.getReturnedMessage().getBody()));
            }
        }
    };

    final RabbitTemplate.ReturnCallback returnCallback = new RabbitTemplate.ReturnCallback() {
        @Override
        public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
            System.err.println("return exchange: " + exchange + ", routingKey: "
                    + routingKey + ", replyCode: " + replyCode + ", replyText: " + replyText);
        }
    };

    /**
     * 发送消息
     * @param exchange 交换器
     * @param route 路由键
     * @param message 消息
     * @param properties
     */
    public void sendMsg(String exchange, String route, String message, Map<String, Object> properties){
        MessageHeaders mhs = new MessageHeaders(properties);
        MessageBuilder.createMessage(message, mhs);
        org.springframework.messaging.Message msg = MessageBuilder.createMessage(message, mhs);
        this.rabbitTemplate.setConfirmCallback(confirmCallback);
        this.rabbitTemplate.setReturnCallback(returnCallback);
        /**
         * 设置AMQP消息属性
         */
        MessageProperties messageProperties = new MessageProperties();
        /**
         * 设置消息的内容类型，默认是application/octet-stream字节类型
         */
        messageProperties.setContentType(MessageProperties.DEFAULT_CONTENT_TYPE);
        /**
         * 设置消息过期时间，单位：毫秒
         */
        messageProperties.setExpiration("10000");
        /**
         * 将消息主题和属性封装在Message类中
         */
        Message returnedMessage = new Message(message.getBytes(), messageProperties);
        /**
         * 相关数据
         */
        CorrelationData correlationData = new CorrelationData();
        /**
         * 消息ID，全局必须唯一
         */
        correlationData.setId(UUID.randomUUID().toString());

        /**
         * 设置此相关数据的返回消息
         */
        correlationData.setReturnedMessage(returnedMessage);
        this.rabbitTemplate.convertAndSend(exchange, route, msg, correlationData);
    }
}
