package com.emily.infrastructure.rabbitmq.processor;

import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author :  Emily
 * @since :  2025/12/14 下午1:26
 */
public class DataRabbitEnvironmentPostProcessor implements EnvironmentPostProcessor {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, @NonNull SpringApplication application) {
        String excludes = environment.getProperty("spring.autoconfigure.exclude", "");
        if (StringUtils.isNotBlank(excludes)) {
            excludes += ",";
        }
        excludes += "org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration";
        environment.getSystemProperties().put("spring.autoconfigure.exclude", excludes);

    }
}
