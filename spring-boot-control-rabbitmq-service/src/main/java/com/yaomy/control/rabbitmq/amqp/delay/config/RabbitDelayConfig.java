package com.yaomy.control.rabbitmq.amqp.delay.config;

import com.google.common.collect.Maps;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: RabbitMQ生产者交换器、绑定、队列声明
 * @Version: 1.0
 */
@SuppressWarnings("all")
@Configuration
public class RabbitDelayConfig {
    /**
     * 交换器
     */
    public static final String DELAY_TOPIC_EXCHANGE = "delay.topic.exchange";
    /**
     * 队列
     */
    public static final String DELAY_TOPIC_QUEUE = "delay_topic_queue";
    /**
     * 路由
     */
    public static final String DELAY__TOPIC_ROUTING_KEY = "delay_routing_key";
    /**
     * 声明延时队列
     */
    @Bean
    public Queue delayQueue(){
        Map<String, Object> args = Maps.newHashMap();
        /**
         * 定义优先级队列，消息最大优先级为15，优先级范围为0-15，数字越大优先级越高
         */
        args.put("x-max-priority", 15);
        /**
         * 设置持久化队列
         */
        return QueueBuilder.durable(DELAY_TOPIC_QUEUE).withArguments(args).build();
    }


    /**
     * 延时队列交换器
     * 注意：
     * 1.交换器类是CustomExchange
     * 2.交换器类型是x-delayed-message
     */
    @Bean
    public CustomExchange delayExchange(){
        Map<String, Object> args = new HashMap<>();
        /**
         * 设置自定义交换器路由消息的类型，direct类似direct交换器路由消息的模式，也可以传递topic、fanout,或者其它插件提供的自定义的交换器类型
         */
        args.put("x-delayed-type", "direct");

        return new CustomExchange(DELAY_TOPIC_EXCHANGE, "x-delayed-message", true, false, args);
    }

    /**
     * 延迟队列绑定交换器
     */
    @Bean
    public Binding bindingDelayCustomExchangeQueue(){
        return BindingBuilder.bind(delayQueue()).to(delayExchange()).with(DELAY__TOPIC_ROUTING_KEY).noargs();
    }
}
