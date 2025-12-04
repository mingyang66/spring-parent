package com.emily.infrastructure.rabbitmq.amqp;

import com.emily.infrastructure.rabbitmq.RabbitMqProperties;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitOperations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.amqp.autoconfigure.RabbitProperties;
import org.springframework.boot.amqp.autoconfigure.RabbitTemplateConfigurer;
import org.springframework.boot.amqp.autoconfigure.RabbitTemplateRetrySettingsCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Map;
import java.util.Objects;

import static com.emily.infrastructure.rabbitmq.common.RabbitMqUtils.*;

/**
 * RabbitTemplate配置类 参考：RabbitAutoConfiguration.RabbitTemplateConfiguration
 *
 * @author Emily
 * @since Created in 2022/6/6 10:08 上午
 */
@Configuration(proxyBeanMethods = false)
@Import(RabbitMqConnectionFactoryCreator.class)
public class RabbitMqTemplateConfiguration {

    private final DefaultListableBeanFactory defaultListableBeanFactory;

    public RabbitMqTemplateConfiguration(DefaultListableBeanFactory defaultListableBeanFactory) {
        this.defaultListableBeanFactory = defaultListableBeanFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public RabbitTemplateConfigurer rabbitTemplateConfigurer(RabbitMqProperties rabbitMqProperties,
                                                             ObjectProvider<MessageConverter> messageConverter,
                                                             ObjectProvider<RabbitTemplateRetrySettingsCustomizer> retrySettingsCustomizers) {
        String defaultConfig = Objects.requireNonNull(rabbitMqProperties.getDefaultConfig(), "RabbitMQ默认配置必须配置");
        Map<String, RabbitProperties> dataMap = Objects.requireNonNull(rabbitMqProperties.getConfig(), "RabbitMQ连接配置不存在");
        for (Map.Entry<String, RabbitProperties> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            RabbitProperties properties = entry.getValue();
            RabbitTemplateConfigurer configurer = new RabbitTemplateConfigurer(properties);
            configurer.setMessageConverter((MessageConverter) messageConverter.getIfUnique());
            configurer.setRetrySettingsCustomizers(retrySettingsCustomizers.orderedStream().toList());
            defaultListableBeanFactory.registerSingleton(join(key, RABBIT_TEMPLATE_CONFIGURER), configurer);
        }
        return defaultListableBeanFactory.getBean(join(defaultConfig, RABBIT_TEMPLATE_CONFIGURER), RabbitTemplateConfigurer.class);
    }

    @Bean
    @ConditionalOnSingleCandidate(ConnectionFactory.class)
    @ConditionalOnMissingBean(RabbitOperations.class)
    public RabbitTemplate rabbitTemplate(RabbitMqProperties rabbitMqProperties) {
        String defaultConfig = Objects.requireNonNull(rabbitMqProperties.getDefaultConfig(), "RabbitMQ默认配置必须配置");
        Map<String, RabbitProperties> dataMap = Objects.requireNonNull(rabbitMqProperties.getConfig(), "RabbitMQ连接配置不存在");
        RabbitTemplate rabbitTemplate = null;
        for (Map.Entry<String, RabbitProperties> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            RabbitTemplate template = new RabbitTemplate();
            if (defaultConfig.equals(key)) {
                ConnectionFactory connectionFactory = defaultListableBeanFactory.getBean(DEFAULT_RABBIT_CONNECTION_FACTORY, ConnectionFactory.class);
                RabbitTemplateConfigurer configurer = defaultListableBeanFactory.getBean(DEFAULT_RABBIT_TEMPLATE_CONFIGURER, RabbitTemplateConfigurer.class);
                configurer.configure(template, connectionFactory);
                rabbitTemplate = template;
            } else {
                ConnectionFactory connectionFactory = defaultListableBeanFactory.getBean(join(key, RABBIT_CONNECTION_FACTORY), ConnectionFactory.class);
                RabbitTemplateConfigurer configurer = defaultListableBeanFactory.getBean(join(key, RABBIT_TEMPLATE_CONFIGURER), RabbitTemplateConfigurer.class);
                configurer.configure(template, connectionFactory);
                defaultListableBeanFactory.registerSingleton(join(key, RABBIT_TEMPLATE), template);
            }
        }
        return rabbitTemplate;
    }

    @Bean
    @ConditionalOnSingleCandidate(ConnectionFactory.class)
    @ConditionalOnMissingBean
    public AmqpAdmin amqpAdmin(RabbitMqProperties rabbitMqProperties) {
        String defaultConfig = Objects.requireNonNull(rabbitMqProperties.getDefaultConfig(), "RabbitMQ默认配置必须配置");
        Map<String, RabbitProperties> dataMap = Objects.requireNonNull(rabbitMqProperties.getConfig(), "RabbitMQ连接配置不存在");
        RabbitAdmin amqpAdmin = null;
        for (Map.Entry<String, RabbitProperties> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            ConnectionFactory connectionFactory;
            if (defaultConfig.equals(key)) {
                connectionFactory = defaultListableBeanFactory.getBean(DEFAULT_RABBIT_CONNECTION_FACTORY, ConnectionFactory.class);
                amqpAdmin = new RabbitAdmin(connectionFactory);
            } else {
                connectionFactory = defaultListableBeanFactory.getBean(join(key, RABBIT_CONNECTION_FACTORY), ConnectionFactory.class);
                defaultListableBeanFactory.registerSingleton(join(key, AMQP_ADMIN), new RabbitAdmin(connectionFactory));
            }
        }
        return amqpAdmin;
    }
}
