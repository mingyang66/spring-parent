package com.emily.infrastructure.rabbitmq.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author :  姚明洋
 * @since :  2025/12/25 下午3:17
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({RabbitListenerConfigurationSelector.class})
public @interface EnableRabbit {
}
