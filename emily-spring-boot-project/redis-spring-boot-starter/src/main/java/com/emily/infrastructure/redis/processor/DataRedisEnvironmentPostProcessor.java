package com.emily.infrastructure.redis.processor;

import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * EnvironmentPostProcessor是springboot提供的一个扩展接口，允许开发者在应用上下文刷新之前对环境配置进行自定义处理；
 *
 * @author :  Emily
 * @since :  2025/12/14 下午1:26
 */
public class DataRedisEnvironmentPostProcessor implements EnvironmentPostProcessor {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, @NonNull SpringApplication application) {
        String excludes = environment.getProperty("spring.autoconfigure.exclude", StringUtils.EMPTY);
        if (StringUtils.isNotBlank(excludes)) {
            excludes += ",";
        }
        excludes += "org.springframework.boot.data.redis.autoconfigure.DataRedisReactiveAutoConfiguration" +
                ",org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration";
        environment.getSystemProperties().put("spring.autoconfigure.exclude", excludes);

    }
}
