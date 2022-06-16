package com.emily.infrastructure.rabbitmq.listener;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;

/**
 * @Description :  监听器配置类
 * @Author :  Emily
 * @CreateDate :  Created in 2022/6/14 1:36 下午
 */
public class RabbitMqListenerConfigurer implements RabbitListenerConfigurer {
    private String key;
    private RabbitListenerContainerFactory containerFactory;

    public RabbitMqListenerConfigurer(String key, RabbitListenerContainerFactory containerFactory) {
        this.key = key;
        this.containerFactory = containerFactory;
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
        //使用适配器来处理消息，设置了order，pay队列的消息处理逻辑
        SimpleRabbitListenerEndpoint endpoint = new SimpleRabbitListenerEndpoint();
        endpoint.setId(RandomUtils.nextInt(100, 200) + "");
        if (StringUtils.equals(key, "test")) {
            endpoint.setQueueNames("topic.test.queue");
        } else {
            endpoint.setQueueNames("topic.emily.queue");
        }
        System.out.println("endpoint处理消息的逻辑");
        endpoint.setMessageListener(message -> System.out.println(new String(message.getBody())));
        //注册二个endpoint
        registrar.registerEndpoint(endpoint, containerFactory);
    }
}
