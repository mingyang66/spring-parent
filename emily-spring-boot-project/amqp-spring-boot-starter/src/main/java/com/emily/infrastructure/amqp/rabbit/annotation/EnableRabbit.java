package com.emily.infrastructure.amqp.rabbit.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * {@link org.springframework.amqp.rabbit.annotation.EnableRabbit}
 * @author :  Emily
 * @since :  2025/12/25 下午3:17
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({RabbitListenerConfigurationSelector.class})
public @interface EnableRabbit {
}
