package com.yaomy.control.rabbitmq.amqp.lazy.config;

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
public class RabbitLazyConfig {
    public static final String LAZY_TOPIC_EXCHANGE = "lazy.topic.exchange";
    public static final String LAZY_TOPIC_QUEUE = "lazy_topic_queue";
    public static final String LAZY_TOPIC_ROUTING_KEY = "*.topic.*";

    /**
     * 声明队列
     */
    @Bean
    public Queue topicLazyQueue() {
        Map<String, Object> args = Maps.newHashMap();

        args.put("x-queue-mode", "lazy");
        /**
         * 设置持久化队列
         */
        return QueueBuilder.durable(LAZY_TOPIC_QUEUE).withArguments(args).build();
    }


    /**
     * 声明Topic类型交换器
     */
    @Bean
    public TopicExchange topicLazyExchange() {
        TopicExchange exchange = new TopicExchange(LAZY_TOPIC_EXCHANGE);
        return exchange;
    }

    /**
     * Topic交换器和队列通过bindingKey绑定
     *
     * @return
     */
    @Bean
    public Binding bindingTopicLazyExchangeQueue() {
        return BindingBuilder.bind(topicLazyQueue()).to(topicLazyExchange()).with(LAZY_TOPIC_ROUTING_KEY);
    }

}
