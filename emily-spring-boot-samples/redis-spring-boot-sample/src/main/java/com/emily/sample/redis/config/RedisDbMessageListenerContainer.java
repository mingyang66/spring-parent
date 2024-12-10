package com.emily.sample.redis.config;

import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

/**
 * @author :  Emily
 * @since :  2024/11/28 下午7:07
 */
@Component
public class RedisDbMessageListenerContainer implements SmartLifecycle {
    private boolean isRunning = false;

    @Override
    public void start() {
        System.out.println("-----------start");
        isRunning = true;
    }

    @Override
    public void stop() {
        System.out.println("------------0------------stop");
        isRunning = false;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        System.out.println("---------stop");
        isRunning = false;
        Thread.currentThread().interrupt();
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

}
