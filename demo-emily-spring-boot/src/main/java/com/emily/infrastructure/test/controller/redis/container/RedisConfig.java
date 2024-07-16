package com.emily.infrastructure.test.controller.redis.container;

import com.emily.infrastructure.redis.repository.EnableRedisDbRepositories;
import com.emily.infrastructure.redis.repository.RedisDbKeyValueAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.data.redis.core.convert.RedisConverter;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * @author :  姚明洋
 * @since :  2024/7/1 下午3:56
 */
@Configuration
//@EnableRedisDbRepositories(basePackages = {"com.emily.infrastructure.test.controller.redis.container"}, enableKeyspaceEvents = RedisDbKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
public class RedisConfig {

   // @Bean
    public String register(RedisMessageListenerContainer factory, MessageListenerAdapter messageListenerAdapter) {
        factory.addMessageListener(messageListenerAdapter, PatternTopic.of("test"));
        factory.addMessageListener(messageListenerAdapter, ChannelTopic.of("test1"));
        return "success";
    }

    //@Bean
    public String register1(@Qualifier("test1RedisMessageListenerContainer") RedisMessageListenerContainer factory, MessageListenerAdapter messageListenerAdapter) {
        factory.addMessageListener(messageListenerAdapter, PatternTopic.of("test"));
        factory.addMessageListener(messageListenerAdapter, PatternTopic.of("test1"));
        return "success";
    }

    //@Bean
    public MessageListenerAdapter messageListenerAdapter() {
        return new MessageListenerAdapter(new Receiver(), "receiveMessage");
    }

    //@Bean
    public KeyExpirationEventMessageListener keyExpirationEventMessageListener(RedisMessageListenerContainer factory, RedisConverter redisConverter) {
        return new KeyExpirationEventMessageListener(factory);
    }


    @EventListener
    public void handleRedisKeyExpiredEvent(RedisKeyExpiredEvent<String> event) {
        Object expiredSession = event.getValue();
        assert expiredSession != null;
        System.out.printf("Channel %s,Session with key={%s} has expired%n", event.getChannel(), new String(event.getSource()));
    }
}
