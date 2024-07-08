package com.emily.infrastructure.test.controller.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * @author :  姚明洋
 * @since :  2024/7/1 下午3:56
 */
@AutoConfiguration
public class RedisConfig {
    @Bean
    public String register(@Qualifier("testRedisMessageListenerContainer") RedisMessageListenerContainer factory, MessageListenerAdapter messageListenerAdapter) {
        factory.addMessageListener(messageListenerAdapter, new PatternTopic("test"));
        factory.addMessageListener(messageListenerAdapter, new PatternTopic("test1"));
        return "success";
    }

    @Bean
    public String register1(@Qualifier("test1RedisMessageListenerContainer") RedisMessageListenerContainer factory, MessageListenerAdapter messageListenerAdapter) {
        factory.addMessageListener(messageListenerAdapter, new PatternTopic("test"));
        factory.addMessageListener(messageListenerAdapter, new PatternTopic("test1"));
        return "success";
    }

    @Bean
    public MessageListenerAdapter messageListenerAdapter() {
        return new MessageListenerAdapter(new Receiver(), "receiveMessage");
    }
}
