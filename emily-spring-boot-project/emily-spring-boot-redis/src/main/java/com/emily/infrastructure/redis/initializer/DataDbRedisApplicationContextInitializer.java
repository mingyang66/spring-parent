package com.emily.infrastructure.redis.initializer;

import com.emily.infrastructure.redis.factory.DataRedisFactory;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

/**
 * 关闭springboot starter默认sdk LettuceConnectionConfiguration、JedisConnectionConfiguration的自动化配置
 *
 * @author Emily
 * @since 2020/09/22
 */
public class DataDbRedisApplicationContextInitializer implements ApplicationContextInitializer<@NonNull ConfigurableApplicationContext>, Ordered {
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 2;
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        // 关闭LettuceConnectionConfiguration、JedisConnectionConfiguration自动化配置类
        //System.getProperties().put("spring.data.redis.client-type", "");
        // 关闭消息监听器、仓储配置类开关
        //System.getProperties().put("spring.data.redis.repositories.enabled", false);
        // 初始化容器应用程序上下文
        DataRedisFactory.register(applicationContext);
    }
}
