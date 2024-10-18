package com.emily.simple.websocket.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.stereotype.Component;

/**
 *
 * @author :  Emily
 * @since :  2024/10/17 下午2:17
 */
@Component
public class BrokerAvailabilityListener implements ApplicationListener<BrokerAvailabilityEvent> {
    @Override
    public void onApplicationEvent(BrokerAvailabilityEvent event) {
        System.out.println(event.toString());
    }

    @Override
    public boolean supportsAsyncExecution() {
        return ApplicationListener.super.supportsAsyncExecution();
    }
}
