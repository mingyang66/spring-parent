package com.emily.infrastructure.rabbitmq.amqp;

import com.emily.infrastructure.rabbitmq.DataRabbitProperties;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.amqp.autoconfigure.RabbitProperties;
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
public class DataRabbitMessagingTemplateConfiguration {
    private final DefaultListableBeanFactory defaultListableBeanFactory;

    public DataRabbitMessagingTemplateConfiguration(DefaultListableBeanFactory defaultListableBeanFactory) {
        this.defaultListableBeanFactory = defaultListableBeanFactory;
    }

    @Bean
    @ConditionalOnSingleCandidate(RabbitTemplate.class)
    public RabbitMessagingTemplate rabbitMessagingTemplate(DataRabbitProperties properties) {
        String defaultConfig = Objects.requireNonNull(properties.getDefaultConfig(), "RabbitMQ默认配置必须配置");
        Map<String, RabbitProperties> dataMap = Objects.requireNonNull(properties.getConfig(), "RabbitMQ连接配置不存在");
        for (Map.Entry<String, RabbitProperties> entry : dataMap.entrySet()) {
            RabbitTemplate rabbitTemplate = defaultListableBeanFactory.getBean(join(entry.getKey(), RABBIT_TEMPLATE), RabbitTemplate.class);
            defaultListableBeanFactory.registerSingleton(join(entry.getKey(), RABBIT_MESSAGING_TEMPLATE), new RabbitMessagingTemplate(rabbitTemplate));
        }
        return defaultListableBeanFactory.getBean(join(defaultConfig, RABBIT_MESSAGING_TEMPLATE), RabbitMessagingTemplate.class);
    }
}
