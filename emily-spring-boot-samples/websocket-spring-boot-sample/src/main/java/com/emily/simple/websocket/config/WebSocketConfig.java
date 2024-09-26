package com.emily.simple.websocket.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    /**
     * 配置消息代理选项
     *
     * @param config 用于配置消息代理选项的注册表
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        //启用一个简单的消息代理，并配置一个或多个前缀来过滤以代理目标的目的地（例如：前缀为“/topic”的目的地）
        config.enableSimpleBroker("/topic");
        //配置一个或多个前缀，以筛选针对应用程序注释方法的目标。例如，前缀为“/app”的目的地可以通过带注释的方法进行处理，而其它目的地可能以消息代理为目标（例如：“/topic”、“/queue”）
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * 注册STOMP端点，每个端点映射到特定的URL,并（可选）启用和配置SockJS回退选项
     *
     * @param registry 通过WebSocket端点注册STOMP协议约定
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册STOMP端点，指定端点的路径
        registry.addEndpoint("/gs-guide-websocket");
    }

}
