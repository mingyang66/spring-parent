package com.yaomy.control.rabbitmq.amqp.ttl.config;

import com.google.common.collect.Maps;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @Description: RabbitMQ生产者交换器、绑定、队列声明
 * @Version: 1.0
 */
@SuppressWarnings("all")
@Configuration
public class RabbitConfig {
    public static final String TTL_TOPIC_EXCHANGE = "ttl.topic.exchange";
    public static final String TTL_TOPIC_QUEUE = "ttl_topic_queue";
    public static final String TTL_TOPIC_ROUTING_KEY = "*.topic.*";
    public static final String TTL_DELAY_EXCHANGE = "ttl.dlx.exchange";
    public static final String TTL_DELAY_ROUTING_KEY = "ttl.dlrk.routingkey";
    public static final String TTL_DELAY_QUEUE = "ttl_dlk_queue";
    /**
     * 声明队列
     */
    @Bean
    public Queue topicQueue(){
        Map<String, Object> args = Maps.newHashMap();
        /**
         * 设置消息发送到队列之后多久被丢弃，单位：毫秒
         */
        args.put("x-message-ttl", 10000);
        /**
         * 消息变成死信一般由以下几种情况引起：
         * 1.消息被拒绝，并且设置requeue参数为false
         * 2.消息过期
         * 3.队列达到最大长度
         * x-dead-letter-exchange参数是指消息编程死信之后重新发送的DLX
         */
        args.put("x-dead-letter-exchange", TTL_DELAY_EXCHANGE);
        /**
         * 为DLX指定路由键DLK
         */
        args.put("x-dead-letter-routing-key", TTL_DELAY_ROUTING_KEY);
        /**
         * 定义优先级队列，消息最大优先级为15，优先级范围为0-15，数字越大优先级越高
         */
        args.put("x-max-priority", 15);
        /**
         * 设置持久化队列
         */
        return QueueBuilder.durable(TTL_TOPIC_QUEUE).withArguments(args).build();
    }


    /**
     * 声明Topic类型交换器
     */
    @Bean
    public TopicExchange topicExchange(){
        TopicExchange exchange = new TopicExchange(TTL_TOPIC_EXCHANGE);
        return exchange;
    }

    /**
     * Topic交换器和队列通过bindingKey绑定
     * @return
     */
    @Bean
    public Binding bindingTopicExchangeQueue(){
        return BindingBuilder.bind(topicQueue()).to(topicExchange()).with(TTL_TOPIC_ROUTING_KEY);
    }

    //============================延迟队列及交换器定义=================================
    /**
     * 定义延迟队列
     */
    @Bean
    public Queue ttlQueue(){
        return QueueBuilder.durable(TTL_DELAY_QUEUE).build();
    }

    /**
     * 定义延迟交换器
     */
    @Bean
    public TopicExchange ttlExchange(){
        TopicExchange exchange = new TopicExchange(TTL_DELAY_EXCHANGE);
        return exchange;
    }

    /**
     * 延迟队列交换器绑定
     */
    @Bean
    public Binding bindingTtlDirectExchangeQueue(){
        return BindingBuilder.bind(ttlQueue()).to(ttlExchange()).with(TTL_DELAY_ROUTING_KEY);
    }
}
