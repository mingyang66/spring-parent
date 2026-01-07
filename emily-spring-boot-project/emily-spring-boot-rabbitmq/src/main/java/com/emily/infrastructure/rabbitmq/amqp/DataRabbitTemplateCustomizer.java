package com.emily.infrastructure.rabbitmq.amqp;

import org.jspecify.annotations.NonNull;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.amqp.autoconfigure.RabbitProperties;
import org.springframework.boot.amqp.autoconfigure.RabbitTemplateCustomizer;

/**
 * 自定义RabbitTemplate属性设置回调类
 *
 * @author :  Emily
 * @since :  2025/12/21 下午1:53
 */
public class DataRabbitTemplateCustomizer implements RabbitTemplateCustomizer {
    private final RabbitProperties rabbitProperties;
    private RabbitTemplate.ReturnsCallback returnsCallback;
    private MessagePostProcessor messagePostProcessor;

    public DataRabbitTemplateCustomizer(RabbitProperties rabbitProperties) {
        this.rabbitProperties = rabbitProperties;
    }

    @Override
    public void customize(@NonNull RabbitTemplate rabbitTemplate) {
        if (determineMandatoryFlag() && returnsCallback != null) {
            rabbitTemplate.setReturnsCallback(returnsCallback);
        }
        if (messagePostProcessor != null) {
            rabbitTemplate.setBeforePublishPostProcessors(messagePostProcessor);
        }
    }

    private boolean determineMandatoryFlag() {
        Boolean mandatory = this.rabbitProperties.getTemplate().getMandatory();
        return (mandatory != null) ? mandatory : this.rabbitProperties.isPublisherReturns();
    }

    public void setMessagePostProcessor(MessagePostProcessor messagePostProcessor) {
        this.messagePostProcessor = messagePostProcessor;
    }

    public void setReturnsCallback(RabbitTemplate.ReturnsCallback returnsCallback) {
        this.returnsCallback = returnsCallback;
    }
}
