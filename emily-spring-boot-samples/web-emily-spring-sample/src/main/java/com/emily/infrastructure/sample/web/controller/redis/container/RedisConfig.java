package com.emily.infrastructure.sample.web.controller.redis.container;

import com.emily.infrastructure.date.DateConvertUtils;
import com.emily.infrastructure.date.DatePatternInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import java.time.LocalDateTime;

/**
 * @author :  Emily
 * @since :  2024/7/1 下午3:56
 */
//@Configuration
//@EnableRedisDbRepositories(basePackages = {"com.emily.infrastructure.test.controller.redis.container"}, enableKeyspaceEvents = RedisDbKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
public class RedisConfig {

    @Bean
    public String register(RedisMessageListenerContainer factory, MessageListenerAdapter messageListenerAdapter) {
        factory.addMessageListener(messageListenerAdapter, PatternTopic.of("test"));
        factory.addMessageListener(messageListenerAdapter, ChannelTopic.of("test1"));
        return "success";
    }

    @Bean
    public String register1(@Qualifier("test1RedisMessageListenerContainer") RedisMessageListenerContainer factory, MessageListenerAdapter messageListenerAdapter) {
        factory.addMessageListener(messageListenerAdapter, PatternTopic.of("test"));
        factory.addMessageListener(messageListenerAdapter, PatternTopic.of("test1"));
        return "success";
    }

    @Bean
    public MessageListenerAdapter messageListenerAdapter() {
        return new MessageListenerAdapter(new Receiver(), "receiveMessage");
    }


    @EventListener
    public void handleRedisKeyExpiredEvent(RedisKeyExpiredEvent<String> event) {
        Object expiredSession = event.getValue();
        assert expiredSession != null;
        System.out.printf(DateConvertUtils.format(LocalDateTime.now(), DatePatternInfo.YYYY_MM_DD_HH_MM_SS_EN) + "：Channel %s,Session with key={%s} has expired%n", event.getChannel(), new String(event.getSource()));
    }
}
