package com.emily.infrastructure.rabbitmq.common;

import java.text.MessageFormat;

/**
 * 常量类
 *
 * @author Emily
 * @since Created in 2022/6/8 10:47 上午
 */
public class RabbitMqUtils {
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
    public static final String RABBIT_CONNECTION_FACTORY = "RabbitConnectionFactory";
    /**
     * RabbitListenerAnnotationBeanPostProcessor.DEFAULT_RABBIT_LISTENER_CONTAINER_FACTORY_BEAN_NAME
     */
    public static final String RABBIT_LISTENER_CONTAINER_FACTORY = "RabbitListenerContainerFactory";
    /**
     * RabbitTemplate Bean名称后缀
     */
    public static final String RABBIT_TEMPLATE = "RabbitTemplate";
    /**
     * Rabbit连接工厂Bean配置类
     */
    public static final String RABBIT_CONNECTION_FACTORY_BEAN_CONFIGURER = "RabbitConnectionFactoryBeanConfigurer";
    /**
     * Rabbit连接工厂配置类
     */
    public static final String RABBIT_CONNECTION_FACTORY_CONFIGURER = "RabbitConnectionFactoryConfigurer";
    /**
     * RabbitTemplate配置类
     */
    public static final String RABBIT_TEMPLATE_CONFIGURER = "RabbitTemplateConfigurer";
    /**
     * RabbitMQ监听器工厂配置类，SIMPLE
     */
    public static final String SIMPLE_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER = "SimpleRabbitListenerContainerFactoryConfigurer";
    /**
     * RabbitMQ监听器工厂配置类，DIRECT
     */
    public static final String DIRECT_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER = "DirectRabbitListenerContainerFactoryConfigurer";

    /**
     * 拼接字符串
     *
     * @param key    RabbitMQ配置标识
     * @param suffix 后缀
     * @return 拼接后的beanName
     */
    public static String join(String key, String suffix) {
        return MessageFormat.format("{0}{1}", key, suffix);
    }
}
