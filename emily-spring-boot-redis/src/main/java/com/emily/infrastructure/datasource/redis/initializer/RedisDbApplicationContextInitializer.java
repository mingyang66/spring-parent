package com.emily.infrastructure.datasource.redis.initializer;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

/**
 * @author Emily
 * @program: spring-parent
 * @description: RedisAutoConfiguration自动化配置关闭
 * @create: 2020/09/22
 */
public class RedisDbApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 2;
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        // 关闭LettuceConnectionConfiguration自动化配置类，并级联关闭RedisAutoConfiguration自动化配置
        System.getProperties().put("spring.redis.client-type", "");
    }
}
