package com.emily.simple.websocket.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class ChatMessageBrokerAutoConfiguration implements WebSocketMessageBrokerConfigurer {
    /**
     * 注册STOMP端点，每个端点映射到特定的URL,并（可选）启用和配置SockJS回退选项
     *
     * @param registry 通过WebSocket端点注册STOMP协议约定
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册STOMP端点，指定端点的路径
        registry.addEndpoint("/chatroom", "/chatroom/{userid}")
                // 配置websocket握手请求
                .setHandshakeHandler(new ChatHandshakeHandler())
                .setAllowedOriginPatterns("*");
    }

    /**
     * 配置消息代理选项
     *
     * @param registry 用于配置消息代理选项的注册表
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //定义心跳任务调度器
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10);
        taskScheduler.setThreadNamePrefix("WebSocketChat--");
        taskScheduler.setDaemon(true);
        taskScheduler.initialize();

        //启用一个简单的消息代理，并配置一个或多个前缀来过滤以代理目标的目的地（例如：前缀为“/topic”的目的地）
        registry.enableSimpleBroker("/topic", "/queue")
                //配置心跳，第一个数字表示服务器写入或发送心跳的频率，第二个数字代表客户端应该写入的频率；0代表没有心跳，默认：“0，0”，设置taskScheduler后默认是“10000，10000”
                .setHeartbeatValue(new long[]{10000, 10000})
                //设置心跳任务调度器
                .setTaskScheduler(taskScheduler);
        //指定用户（一对一）的前缀
        registry.setUserDestinationPrefix("/user");
        //设置订阅destination的缓存最大数量，默认：1024
        registry.setCacheLimit(1024);
        //配置一个或多个前缀，以筛选针对应用程序注释方法的目标。例如，前缀为“/app”的目的地可以通过带注释的方法进行处理，而其它目的地可能以消息代理为目标（例如：“/topic”、“/queue”）
        registry.setApplicationDestinationPrefixes("/app");
    }

}
