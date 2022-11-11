package com.emily.infrastructure.rabbitmq.common;

/**
 * @Description :  常量类
 * @Author :  Emily
 * @CreateDate :  Created in 2022/6/8 10:47 上午
 */
public class RabbitMqInfo {
    /**
     * 动态初始化队里、交换机、路由绑定bean名称后缀
     */
    public static final String AMQP_ADMIN = "AmqpAdmin";
    /**
     * RabbitMessagingTemplate bean名称
     */
    public static final String RABBIT_MESSAGING_TEMPLATE = "RabbitMessagingTemplate";
    /**
     * ConnectionFactory bean名称
     */
    public static final String CONNECTION_FACTORY = "ConnectionFactory";
    /**
     *
     */
    public static final String RABBIT_LISTENER_CONTAINER_FACTORY = "RabbitListenerContainerFactory";
}
