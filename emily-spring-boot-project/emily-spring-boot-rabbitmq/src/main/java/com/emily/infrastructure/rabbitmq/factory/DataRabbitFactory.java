package com.emily.infrastructure.rabbitmq.factory;

import com.emily.infrastructure.rabbitmq.DataRabbitProperties;
import com.emily.infrastructure.rabbitmq.common.DataRabbitInfo;
import com.rabbitmq.client.Channel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import java.util.Objects;


/**
 * RabbitMq消息中间件工厂类
 *
 * @author Emily
 * @since Created in 2022/6/6 11:31 上午
 */
public class DataRabbitFactory {
    private static ApplicationContext context;

    public static void register(ApplicationContext context) {
        DataRabbitFactory.context = context;
    }

    /**
     * 获取RabbitTemplate对象
     *
     * @return RabbitTemplate对象
     */
    public static RabbitTemplate getRabbitTemplate() {
        return context.getBean(DataRabbitInfo.DEFAULT_RABBIT_TEMPLATE, RabbitTemplate.class);
    }

    /**
     * 获取RabbitTemplate对象
     *
     * @param key 消息中间件配置标识
     * @return RabbitTemplate对象
     */
    public static RabbitTemplate getRabbitTemplate(String key) {
        Assert.hasText(key, "RabbitMQ标识不可为空");
        DataRabbitProperties properties = context.getBean(DataRabbitProperties.class);
        if (properties.getDefaultConfig().equals(key)) {
            return getRabbitTemplate();
        }
        return context.getBean(StringUtils.join(key, DataRabbitInfo.RABBIT_TEMPLATE), RabbitTemplate.class);
    }

    /**
     * 获取AmqpAdmin对象，用于动态的初始化队里、交换机、路由绑定Key
     * AmqpAdmin amqpAdmin = IOCContext.getBean("testAmqpAdmin", AmqpAdmin.class);
     * TopicExchange exchange = ExchangeBuilder.topicExchange("exchange").build();
     * Queue queue = QueueBuilder.durable("topic.emily.queue").build();
     * Binding binding = BindingBuilder.bind(queue).to(exchange).with("topic.#");
     * amqpAdmin.declareExchange(exchange);
     * amqpAdmin.declareQueue(queue);
     * amqpAdmin.declareBinding(binding);
     *
     * @return AmqpAdmin对象
     */
    public static AmqpAdmin getAmqpAdmin() {
        return context.getBean(DataRabbitInfo.DEFAULT_AMQP_ADMIN, AmqpAdmin.class);
    }

    /**
     * 获取AmqpAdmin对象，用于动态的初始化队里、交换机、路由绑定Key
     * AmqpAdmin amqpAdmin = IOCContext.getBean("testAmqpAdmin", AmqpAdmin.class);
     * TopicExchange exchange = ExchangeBuilder.topicExchange("exchange").build();
     * Queue queue = QueueBuilder.durable("topic.emily.queue").build();
     * Binding binding = BindingBuilder.bind(queue).to(exchange).with("topic.#");
     * amqpAdmin.declareExchange(exchange);
     * amqpAdmin.declareQueue(queue);
     * amqpAdmin.declareBinding(binding);
     *
     * @param key 消息中间件配置标识
     * @return AmqpAdmin对象
     */
    public static AmqpAdmin getAmqpAdmin(String key) {
        Assert.hasText(key, "RabbitMQ标识不可为空");
        DataRabbitProperties properties = context.getBean(DataRabbitProperties.class);
        if (properties.getDefaultConfig().equals(key)) {
            return getAmqpAdmin();
        }
        return context.getBean(StringUtils.join(key, DataRabbitInfo.AMQP_ADMIN), AmqpAdmin.class);
    }

    /**
     * 获取RabbitMessagingTemplate实例对象
     *
     * @return RabbitMessagingTemplate对象
     */
    public static RabbitMessagingTemplate getRabbitMessagingTemplate() {
        return context.getBean(DataRabbitInfo.DEFAULT_RABBIT_MESSAGING_TEMPLATE, RabbitMessagingTemplate.class);
    }

    /**
     * 获取RabbitMessagingTemplate实例对象
     *
     * @param key 消息中间件标识
     * @return RabbitMessagingTemplate对象
     */
    public static RabbitMessagingTemplate getRabbitMessagingTemplate(String key) {
        Assert.hasText(key, "RabbitMQ标识不可为空");
        DataRabbitProperties properties = context.getBean(DataRabbitProperties.class);
        if (properties.getDefaultConfig().equals(key)) {
            return getRabbitMessagingTemplate();
        }
        return context.getBean(StringUtils.join(key, DataRabbitInfo.RABBIT_MESSAGING_TEMPLATE), RabbitMessagingTemplate.class);
    }

    /**
     * 声明RabbitMQ消息中间件队列、交换器、绑定，并初始化
     *
     * @param queue    队列
     * @param exchange 交换器
     * @param binding  绑定
     */
    public static void declare(Queue queue, Exchange exchange, Binding binding) {
        declare(null, queue, exchange, binding);
    }

    /**
     * 声明RabbitMQ消息中间件队列、交换器、绑定，并初始化
     *
     * @param key      中间件配置标识
     * @param queue    队列
     * @param exchange 交换器
     * @param binding  绑定
     */
    public static void declare(String key, Queue queue, Exchange exchange, Binding binding) {
        if (Objects.isNull(key)) {
            key = context.getBean(DataRabbitProperties.class).getDefaultConfig();
        }
        AmqpAdmin amqpAdmin = getAmqpAdmin(key);
        amqpAdmin.declareExchange(exchange);
        amqpAdmin.declareQueue(queue);
        amqpAdmin.declareBinding(binding);
    }

    /**
     * 获取RabbitMQ消息中间件通道
     *
     * @param transactional true-支持事务，false-不支持事务
     * @return Channel对象
     */
    public static Channel getChannel(boolean transactional) {
        return getChannel(null, transactional);
    }

    /**
     * 获取RabbitMQ消息中间件通道
     *
     * @param key           中间件配置标识
     * @param transactional true-支持事务，false-不支持事务
     * @return Channel对象
     */
    public static Channel getChannel(String key, boolean transactional) {
        ConnectionFactory connectionFactory = getRabbitTemplate(key).getConnectionFactory();
        Connection connection = connectionFactory.createConnection();
        return connection.createChannel(transactional);
    }

}
