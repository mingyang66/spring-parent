/*
package com.emily.infrastructure.test.controller.rabbit;

import com.emily.infrastructure.rabbitmq.factory.RabbitMqFactory;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

*/
/**
 * @author Emily
 * @Description: RabbitMQ消息消费者
 * @Version: 1.0
 *//*


public class RabbitConsumer {

    public static Object init() {
        Channel channel = RabbitMqFactory.getChannel(false);
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("接收消息 :   " + new String(body));
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //消息确认
                channel.basicAck(envelope.getDeliveryTag(), false);

            }
        };
        try {
            channel.basicConsume("topic.test.queue", consumer);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return "unset";
    }
}
*/
