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
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
                                                             ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers) {
        String defaultConfig = Objects.requireNonNull(rabbitMqProperties.getDefaultConfig(), "RabbitMQ默认配置必须配置");
        Map<String, RabbitProperties> dataMap = Objects.requireNonNull(rabbitMqProperties.getConfig(), "RabbitMQ连接配置不存在");
        RabbitTemplateConfigurer rabbitTemplateConfigurer = null;
        for (Map.Entry<String, RabbitProperties> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            RabbitProperties properties = entry.getValue();
            RabbitTemplateConfigurer configurer = new RabbitTemplateConfigurer(properties);
            configurer.setMessageConverter(messageConverter.getIfUnique());
            configurer.setRetryTemplateCustomizers(retryTemplateCustomizers.orderedStream().collect(Collectors.toList()));
            if (defaultConfig.equals(key)) {
                rabbitTemplateConfigurer = configurer;
            } else {
                defaultListableBeanFactory.registerSingleton(join(key, RABBIT_TEMPLATE_CONFIGURER), configurer);
            }
        }
        return rabbitTemplateConfigurer;
    }

    @Bean
    @ConditionalOnSingleCandidate(ConnectionFactory.class)
    @ConditionalOnMissingBean(RabbitOperations.class)
    public RabbitTemplate rabbitTemplate(RabbitMqProperties rabbitMqProperties, RabbitTemplateConfigurer configurer) {
        String defaultConfig = Objects.requireNonNull(rabbitMqProperties.getDefaultConfig(), "RabbitMQ默认配置必须配置");
        Map<String, RabbitProperties> dataMap = Objects.requireNonNull(rabbitMqProperties.getConfig(), "RabbitMQ连接配置不存在");
        RabbitTemplate rabbitTemplate = null;
        for (Map.Entry<String, RabbitProperties> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            RabbitTemplate template = new RabbitTemplate();
            if (defaultConfig.equals(key)) {
                ConnectionFactory connectionFactory = defaultListableBeanFactory.getBean(DEFAULT_RABBIT_CONNECTION_FACTORY, ConnectionFactory.class);
                configurer.configure(template, connectionFactory);
                rabbitTemplate = template;
            } else {
                ConnectionFactory connectionFactory = defaultListableBeanFactory.getBean(join(key, RABBIT_CONNECTION_FACTORY), ConnectionFactory.class);
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
