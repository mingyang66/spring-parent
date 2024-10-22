package com.emily.sample.websocket.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

/**
 * 连接事件，表示服务端对客户端连接的响应，可以认为连接建立成功
 *
 * @author :  Emily
 * @since :  2024/10/17 下午2:53
 */
@Component
public class SessionConnectedListener implements ApplicationListener<SessionConnectedEvent> {
    @Override
    public void onApplicationEvent(SessionConnectedEvent event) {
        System.out.println("连接成功事件SessionConnectedEvent：" + event.getUser().getName() + "," + event.getMessage().getHeaders().get(SimpMessageHeaderAccessor.SESSION_ID_HEADER) + "," + event.getSource() + "," + event.getMessage().getPayload());
    }

    @Override
    public boolean supportsAsyncExecution() {
        return ApplicationListener.super.supportsAsyncExecution();
    }
}
