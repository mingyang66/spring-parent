package com.emily.infrastructure.redis.initializer;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

/**
 * RedisAutoConfiguration自动化配置关闭
 *
 * @author Emily
 * @since 2020/09/22
 */
public class RedisDbApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 2;
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        // 关闭LettuceConnectionConfiguration自动化配置类，并级联关闭RedisAutoConfiguration自动化配置
        System.getProperties().put("spring.data.redis.client-type", "");
    }
}
