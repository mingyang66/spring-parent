package com.emily.sample.websocket.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * 当使用简单消息协议（STOMP）作为WebSocket子协议的WebSocket客户端会话关闭时引发的事件。
 * 请注意，对于单个会话，此事件可能会被引发多次，因此事件消费者应该是幂等的，忽略重复的事件。
 *
 * @author :  Emily
 * @since :  2024/10/17 下午2:17
 */
@Component
public class SessionDisconnectListener implements ApplicationListener<SessionDisconnectEvent> {
    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        System.out.println("断开连接会话事件SessionDisconnectEvent：" + event.getUser().getName() + "," + event.getSessionId() + "," + event.getCloseStatus().getCode() + "," + event.getCloseStatus().getReason());
    }

    @Override
    public boolean supportsAsyncExecution() {
        return ApplicationListener.super.supportsAsyncExecution();
    }
}
