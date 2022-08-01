package com.emily.infrastructure.rabbitmq.listener;

import org.springframework.amqp.core.Message;

/**
 * @Description :
 * @Author :  Emily
 * @CreateDate :  Created in 2022/6/14 1:37 下午
 */
public class MessageHandler {
    public void handleMessage(Message message) {
        System.out.println("消费消息");
        System.out.println(new String(message.getBody()));
    }
}
