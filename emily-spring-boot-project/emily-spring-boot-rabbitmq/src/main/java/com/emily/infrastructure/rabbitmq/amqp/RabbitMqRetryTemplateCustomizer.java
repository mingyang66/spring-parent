package com.emily.infrastructure.rabbitmq.amqp;

import com.emily.infrastructure.rabbitmq.listener.PublisherRetryListener;
import org.springframework.core.retry.RetryTemplate;

import java.lang.annotation.Target;

/**
 * 自定义实现回调接口RabbitRetryTemplateCustomizer 作为RetryTemplate的一部分
 *
 * @author :  Emily
 * @since :  2023/9/20 20:22 PM
 */
public class RabbitMqRetryTemplateCustomizer implements RabbitRetryTemplateCustomizer {
    @Override
    public void customize(Target target, RetryTemplate retryTemplate) {
        //表示RetryTemplate应用于RabbitTemplate。这是在发送消息时使用的目标
        if (target.equals(Target.SENDER)) {
            retryTemplate.registerListener(new PublisherRetryListener());
        }
        //表示RetryTemplate应用于AbstractMessageListenerContainer。这是在接收消息时使用的目标
        if (target.equals(Target.LISTENER)) {

        }
    }
}
