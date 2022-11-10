package com.emily.infrastructure.test.controller.rabbit;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.AutoConfiguration;

/**
 * @Description :
 * @Author :  Emily
 * @CreateDate :  Created in 2022/6/10 4:48 下午
 */

@AutoConfiguration
public class RabbitConfig {

    @RabbitListener(queues = "topic.test.queue", containerFactory = "testRabbitListenerContainerFactory")
    public void handler(String message) {
        System.out.println("TEST-" + message);
    }

    @RabbitListener(queues = "topic.emily.queue", containerFactory = "emilyRabbitListenerContainerFactory")
    public void handlerEmily(String message) {
        System.out.println("EMILY-" + message);
    }
}
