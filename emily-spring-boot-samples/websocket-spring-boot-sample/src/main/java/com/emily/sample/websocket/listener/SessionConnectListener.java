package com.emily.sample.websocket.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

/**
 * 当使用简单消息协议（如：STOMP）作为WebSocket子协议的新WebSocket客户端发出连接请求时引发的事件。
 * 请注意，这与建立WebSocket会话不同，而是客户端在子协议内首次尝试连接，例如，发送STOMP connect帧
 * @author :  Emily
 * @since :  2024/10/17 下午2:53
 */
@Component
public class SessionConnectListener implements ApplicationListener<SessionConnectEvent> {
    @Override
    public void onApplicationEvent(SessionConnectEvent event) {
        System.out.println("发送连接事件SessionConnectEvent：" + event.getUser().getName() + "," + event.getMessage().getHeaders().get(SimpMessageHeaderAccessor.SESSION_ID_HEADER) + "," + event.getSource() + "," + event.getMessage().getPayload());
    }

    @Override
    public boolean supportsAsyncExecution() {
        return ApplicationListener.super.supportsAsyncExecution();
    }
}
