package com.emily.infrastructure.rabbitmq.amqp;

import com.emily.infrastructure.rabbitmq.RabbitMqProperties;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Map;
import java.util.Objects;

import static com.emily.infrastructure.rabbitmq.common.RabbitMqUtils.*;

/**
 * @author Emily
 * @since Created in 2022/6/8 4:24 下午
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RabbitMessagingTemplate.class)
@ConditionalOnMissingBean(RabbitMessagingTemplate.class)
@Import(DataRabbitTemplateConfiguration.class)
public class RabbitMqMessagingTemplateConfiguration {
    private final DefaultListableBeanFactory defaultListableBeanFactory;

    public RabbitMqMessagingTemplateConfiguration(DefaultListableBeanFactory defaultListableBeanFactory) {
        this.defaultListableBeanFactory = defaultListableBeanFactory;
    }

    @Bean
    @ConditionalOnSingleCandidate(RabbitTemplate.class)
    public RabbitMessagingTemplate rabbitMessagingTemplate(RabbitMqProperties rabbitMqProperties) {
        String defaultConfig = Objects.requireNonNull(rabbitMqProperties.getDefaultConfig(), "RabbitMQ默认配置必须配置");
        Map<String, RabbitProperties> dataMap = Objects.requireNonNull(rabbitMqProperties.getConfig(), "RabbitMQ连接配置不存在");
        RabbitMessagingTemplate rabbitMessagingTemplate = null;
        for (Map.Entry<String, RabbitProperties> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            if (defaultConfig.equals(key)) {
                RabbitTemplate rabbitTemplate = defaultListableBeanFactory.getBean(DEFAULT_RABBIT_TEMPLATE, RabbitTemplate.class);
                rabbitMessagingTemplate = new RabbitMessagingTemplate(rabbitTemplate);
            } else {
                RabbitTemplate rabbitTemplate = defaultListableBeanFactory.getBean(join(key, RABBIT_TEMPLATE), RabbitTemplate.class);
                defaultListableBeanFactory.registerSingleton(join(key, RABBIT_MESSAGING_TEMPLATE), new RabbitMessagingTemplate(rabbitTemplate));
            }
        }
        return rabbitMessagingTemplate;
    }
}
