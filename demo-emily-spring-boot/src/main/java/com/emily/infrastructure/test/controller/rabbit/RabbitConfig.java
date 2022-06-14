package com.emily.infrastructure.test.controller.rabbit;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Configuration;

/**
 * @Description :
 * @Author :  Emily
 * @CreateDate :  Created in 2022/6/10 4:48 下午
 */
@Configuration
@EnableRabbit
public class RabbitConfig {

 /*   @RabbitListener(queues = "topic.test.queue", containerFactory = "testSimpleRabbitListenerContainerFactory")
    public void handler(String message) {
        System.out.println(message);
    }*/
}
