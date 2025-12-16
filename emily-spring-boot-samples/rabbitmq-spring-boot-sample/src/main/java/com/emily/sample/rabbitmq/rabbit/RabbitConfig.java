package com.emily.sample.rabbitmq.rabbit;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.io.IOException;


/**
 * @author Emily
 * @since Created in 2022/6/10 4:48 下午
 */

@Configuration
@DependsOn(value = {"rabbitListenerContainerFactory"})
public class RabbitConfig {

    @RabbitListener(queues = "emily.test.queue", containerFactory = "emilyRabbitListenerContainerFactory", ackMode = "MANUAL")
    public void handler(Channel channel, Message message) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        //try {
        System.out.println("EMILY-" + new String(message.getBody()));
        channel.basicAck(deliveryTag, false);
        //} catch (IOException e) {
        //  throw new RuntimeException(e);
        //} finally {
        // try {
        //channel.basicReject(deliveryTag, false);
        //} catch (IOException e) {
        //   throw new RuntimeException(e);
        //}
        //}
    }

    @RabbitListener(queues = "topic.test.queue", ackMode = "MANUAL")
    public void handlerEmily(Channel channel, Message message) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        //try {
        System.out.println("TEST-" + new String(message.getBody()));
        channel.basicAck(deliveryTag, false);
        /* } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                channel.basicReject(deliveryTag, false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }*/
    }


}
