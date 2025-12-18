package com.emily.infrastructure.rabbitmq.amqp;

import com.emily.infrastructure.rabbitmq.DataRabbitProperties;
import com.emily.infrastructure.rabbitmq.common.DataRabbitInfo;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.util.Assert;

import java.util.Map;


/**
 * @author Emily
 * @since Created in 2022/6/8 4:24 下午
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RabbitMessagingTemplate.class)
@ConditionalOnMissingBean(RabbitMessagingTemplate.class)
@Import(DataRabbitTemplateConfiguration.class)
public class DataRabbitMessagingTemplateConfiguration {
    private final DataRabbitProperties properties;
    private final DefaultListableBeanFactory defaultListableBeanFactory;

    public DataRabbitMessagingTemplateConfiguration(DataRabbitProperties properties, DefaultListableBeanFactory defaultListableBeanFactory) {
        Assert.notNull(properties.getDefaultConfig(), "RabbitMQ默认配置必须配置");
        Assert.notNull(properties.getConfig(), "RabbitMQ连接配置不存在");
        this.properties = properties;
        this.defaultListableBeanFactory = defaultListableBeanFactory;
    }

    @Bean
    @ConditionalOnSingleCandidate(RabbitTemplate.class)
    public RabbitMessagingTemplate rabbitMessagingTemplate() {
        for (Map.Entry<String, RabbitProperties> entry : properties.getConfig().entrySet()) {
            RabbitTemplate rabbitTemplate = defaultListableBeanFactory.getBean(StringUtils.join(entry.getKey(), DataRabbitInfo.RABBIT_TEMPLATE), RabbitTemplate.class);
            defaultListableBeanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRabbitInfo.RABBIT_MESSAGING_TEMPLATE), new RabbitMessagingTemplate(rabbitTemplate));
        }
        return defaultListableBeanFactory.getBean(StringUtils.join(properties.getDefaultConfig(), DataRabbitInfo.RABBIT_MESSAGING_TEMPLATE), RabbitMessagingTemplate.class);
    }
}
