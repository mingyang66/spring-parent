package com.emily.simple.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;

import java.util.concurrent.TimeUnit;

/**
 * @author :  Emily
 * @since :  2024/10/15 下午4:14
 */
@Configuration
public class ChatStatsAutoConfiguration {
    private final WebSocketMessageBrokerStats stats;

    public ChatStatsAutoConfiguration(WebSocketMessageBrokerStats stats) {
        this.stats = stats;
    }

    /**
     * 修改监控任务调度频率
     */
    @Bean
    public String configLoggingPeriod() {
        stats.setLoggingPeriod(TimeUnit.SECONDS.toMillis(20));
        return "LoggingPeriod";
    }
}
