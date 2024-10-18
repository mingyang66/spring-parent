package com.emily.simple.websocket.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

/**
 *
 * @author :  Emily
 * @since :  2024/10/17 下午2:17
 */
@Component
public class SessionSubscribeListener implements ApplicationListener<SessionSubscribeEvent> {
    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        System.out.println(event.toString());
    }

    @Override
    public boolean supportsAsyncExecution() {
        return ApplicationListener.super.supportsAsyncExecution();
    }
}
