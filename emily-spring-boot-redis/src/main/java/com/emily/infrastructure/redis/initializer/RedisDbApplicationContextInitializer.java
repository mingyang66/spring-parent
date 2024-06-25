package com.emily.infrastructure.redis.initializer;

import jakarta.annotation.Nonnull;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNullApi;

/**
 * 关闭springboot starter默认sdk LettuceConnectionConfiguration的自动化配置
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
    public void initialize(@Nonnull ConfigurableApplicationContext applicationContext) {
        // 关闭LettuceConnectionConfiguration自动化配置类，并级联关闭RedisAutoConfiguration自动化配置
        System.getProperties().put("spring.data.redis.client-type", "");
    }
}
