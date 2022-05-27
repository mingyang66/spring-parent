package com.yaomy.control.rabbitmq.amqp.lazy;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.connection.PublisherCallbackChannel;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @Description: RabbitMQ生产者
 * @ProjectName: spring-parent
 * @Version: 1.0
 */
@SuppressWarnings("all")
@Component
public class RabbitLazySender {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    /**
     * 创建一个消息是否投递成功的回调方法
     */
    private final RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        /**
         *
         * @param correlationData 消息的附加信息
         * @param ack true for ack, false for nack
         * @param cause 是一个可选的原因，对于nack，如果可用，否则为空。
         */
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            if (!ack) {
                //可以进行日志记录、异常处理、补偿处理等
                System.err.println("异常ack-" + ack + ",id-" + correlationData.getId() + ",cause:" + cause);
            } else {
                //更新数据库，可靠性投递机制
                System.out.println("正常ack-" + ack + ",id-" + correlationData.getId());
                try {
                    System.out.println(new String(correlationData.getReturnedMessage().getBody()));

                } catch (Exception e) {

                }
            }
        }
    };
    /**
     * 创建一个消息是否被队列接收的监听对象，如果没有队列接收发送出的消息，则调用此方法进行后续处理
     */
    private final RabbitTemplate.ReturnCallback returnCallback = new RabbitTemplate.ReturnCallback() {
        /**
         *
         * @param message 被退回的消息
         * @param replyCode 错误编码
         * @param replyText 错误描述
         * @param exchange 交换器
         * @param routingKey 路由
         */
        @Override
        public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
            System.err.println("spring_returned_message_correlation:" + message.getMessageProperties().getHeaders().get(PublisherCallbackChannel.RETURNED_MESSAGE_CORRELATION_KEY)
                    + "return exchange: " + exchange
                    + ", routingKey: " + routingKey
                    + ", replyCode: " + replyCode
                    + ", replyText: " + replyText
                    + ",message:" + message);
            try {
                System.out.println(new String(message.getBody()));
            } catch (Exception e) {

            }
        }
    };
    /**
     * 扩展点，在消息转换完成之后，发送之前调用；可以修改消息属性、消息头信息
     */
    private final MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
        @Override
        public Message postProcessMessage(Message message) throws AmqpException {
            MessageProperties properties = message.getMessageProperties();
            /**
             * 设置消息发送到队列之后多久被丢弃，单位：毫秒
             * 此种方案需要每条消息都设置此属性，比较灵活；
             * 还有一种方案是在声明队列的时候指定发送到队列中的过期时间；
             * * Queue queue = new Queue("test_queue2");
             * * queue.getArguments().put("x-message-ttl", 10000);
             * 这两种方案可以同时存在，以值小的为准
             */
            //properties.setExpiration("10000");
            /**
             * 设置消息的优先级
             */
            properties.setPriority(9);
            /**
             * 设置消息发送到队列中的模式，持久化|非持久化（只存在于内存中）
             */
            properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);

            return message;
        }
    };

    /**
     * 发送消息
     *
     * @param exchange   交换器
     * @param route      路由键
     * @param message    消息
     * @param properties
     */
    public void sendMsg(String exchange, String routingKey, String message, MessageProperties properties) {
        /**
         * 设置生产者消息publish-confirm回调函数
         */
        this.rabbitTemplate.setConfirmCallback(confirmCallback);
        /**
         * 设置消息退回回调函数
         */
        this.rabbitTemplate.setReturnCallback(returnCallback);
        /**
         * 新增消息转换完成后、发送之前的扩展点
         */
        this.rabbitTemplate.setBeforePublishPostProcessors(messagePostProcessor);

        try {
            if (null == properties) {
                properties = new MessageProperties();
            }
            /**
             * 设置消息唯一标识
             */
            properties.setMessageId(UUID.randomUUID().toString());
            /**
             * 创建消息包装对象
             */
            Message msg = MessageBuilder.withBody(message.getBytes()).andProperties(properties).build();
            /**
             * 将消息主题和属性封装在Message类中
             */
            Message returnedMessage = MessageBuilder.withBody(message.getBytes()).build();
            /**
             * 相关数据
             */
            CorrelationData correlationData = new CorrelationData();
            /**
             * 消息ID，全局唯一
             */
            correlationData.setId(msg.getMessageProperties().getMessageId());

            /**
             * 设置此相关数据的返回消息
             */
            correlationData.setReturnedMessage(returnedMessage);
            /**
             * 如果msg是org.springframework.amqp.core.Message对象的实例，则直接返回，否则转化为Message对象
             */
            this.rabbitTemplate.convertAndSend(exchange, routingKey, msg, correlationData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
