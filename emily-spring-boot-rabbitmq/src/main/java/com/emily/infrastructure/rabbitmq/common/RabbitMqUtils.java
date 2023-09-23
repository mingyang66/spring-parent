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
    public static final String DEFAULT_AMQP_ADMIN = "amqpAdmin";
    /**
     * RabbitMessagingTemplate bean名称
     */
    public static final String RABBIT_MESSAGING_TEMPLATE = "RabbitMessagingTemplate";
    public static final String DEFAULT_RABBIT_MESSAGING_TEMPLATE = "rabbitMessagingTemplate";
    /**
     * ConnectionFactory bean名称
     */
    public static final String RABBIT_CONNECTION_FACTORY = "RabbitConnectionFactory";
    public static final String DEFAULT_RABBIT_CONNECTION_FACTORY = "rabbitConnectionFactory";
    /**
     * RabbitListenerAnnotationBeanPostProcessor.DEFAULT_RABBIT_LISTENER_CONTAINER_FACTORY_BEAN_NAME
     */
    public static final String RABBIT_LISTENER_CONTAINER_FACTORY = "RabbitListenerContainerFactory";
    /**
     * 默认容器工厂bean名称
     */
    public static final String DEFAULT_RABBIT_LISTENER_CONTAINER_FACTORY = "rabbitListenerContainerFactory";
    /**
     * RabbitTemplate Bean名称后缀
     */
    public static final String RABBIT_TEMPLATE = "RabbitTemplate";
    public static final String DEFAULT_RABBIT_TEMPLATE = "rabbitTemplate";
    /**
     * Rabbit连接工厂Bean配置类
     */
    public static final String RABBIT_CONNECTION_FACTORY_BEAN_CONFIGURER = "RabbitConnectionFactoryBeanConfigurer";
    public static final String DEFAULT_RABBIT_CONNECTION_FACTORY_BEAN_CONFIGURER = "rabbitConnectionFactoryBeanConfigurer";
    /**
     * Rabbit连接工厂配置类
     */
    public static final String RABBIT_CONNECTION_FACTORY_CONFIGURER = "RabbitConnectionFactoryConfigurer";
    public static final String DEFAULT_RABBIT_CONNECTION_FACTORY_CONFIGURER = "rabbitConnectionFactoryConfigurer";
    /**
     * RabbitTemplate配置类
     */
    public static final String RABBIT_TEMPLATE_CONFIGURER = "RabbitTemplateConfigurer";
    public static final String DEFAULT_RABBIT_TEMPLATE_CONFIGURER = "rabbitTemplateConfigurer";
    /**
     * RabbitMQ监听器工厂配置类，SIMPLE
     */
    public static final String SIMPLE_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER = "SimpleRabbitListenerContainerFactoryConfigurer";
    /**
     * RabbitMQ监听器工厂配置类，DIRECT
     */
    public static final String DIRECT_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER = "DirectRabbitListenerContainerFactoryConfigurer";
    public static final String DEFAULT_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER = "rabbitListenerContainerFactoryConfigurer";

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
