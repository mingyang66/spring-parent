package com.emily.infrastructure.rabbitmq.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 替换{@link org.springframework.amqp.rabbit.annotation.EnableRabbit}注解
 * {@link org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurationSelector}
 *
 * @author :  Emily
 * @since :  2025/12/21 上午10:51
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({RabbitListenerConfigurationSelector.class})
public @interface EnableRabbit {
}
