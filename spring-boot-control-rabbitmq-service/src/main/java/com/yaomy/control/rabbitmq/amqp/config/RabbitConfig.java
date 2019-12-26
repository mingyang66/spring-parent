package com.yaomy.control.rabbitmq.amqp.config;

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
    /**
     * 声明队列
     */
    @Bean
    public Queue topicQueue(){
        Map<String, Object> args = Maps.newHashMap();
        /**
         * 设置消息发送到队列之后多久被丢弃，单位：毫秒
         */
        args.put("x-message-ttl", 60000);
        /**
         * 定义优先级队列，消息最大优先级为15，优先级范围为0-15，数字越大优先级越高
         */
        args.put("x-max-priority", 15);
        /**
         * 设置持久化队列
         */
        return QueueBuilder.durable("test_queue2").withArguments(args).build();
    }

    /**
     * 声明Topic类型交换器
     */
    @Bean
    public TopicExchange topicExchange(){
        TopicExchange exchange = new TopicExchange("test_exchange2");
        return exchange;
    }

    /**
     * Topic交换器和队列通过bindingKey绑定
     * @return
     */
    @Bean
    public Binding bindingTopicExchangeQueue(){
        return BindingBuilder.bind(topicQueue()).to(topicExchange()).with("*.topic.*");
    }
}
