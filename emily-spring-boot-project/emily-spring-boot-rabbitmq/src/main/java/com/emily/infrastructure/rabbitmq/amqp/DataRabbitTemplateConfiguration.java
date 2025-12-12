package com.emily.infrastructure.rabbitmq.amqp;

import com.emily.infrastructure.rabbitmq.DataRabbitProperties;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
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
@Import(DataRabbitConnectionFactoryCreator.class)
public class DataRabbitTemplateConfiguration {

    private final DefaultListableBeanFactory defaultListableBeanFactory;

    public DataRabbitTemplateConfiguration(DefaultListableBeanFactory defaultListableBeanFactory) {
        this.defaultListableBeanFactory = defaultListableBeanFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public RabbitTemplateConfigurer rabbitTemplateConfigurer(DataRabbitProperties properties,
                                                             ObjectProvider<MessageConverter> messageConverter,
                                                             ObjectProvider<RabbitTemplateRetrySettingsCustomizer> retrySettingsCustomizers) {
        String defaultConfig = Objects.requireNonNull(properties.getDefaultConfig(), "RabbitMQ默认配置必须配置");
        Map<String, RabbitProperties> dataMap = Objects.requireNonNull(properties.getConfig(), "RabbitMQ连接配置不存在");
        for (Map.Entry<String, RabbitProperties> entry : dataMap.entrySet()) {
            RabbitTemplateConfigurer configurer = new RabbitTemplateConfigurer(entry.getValue());
            configurer.setMessageConverter((MessageConverter) messageConverter.getIfUnique());
            configurer.setRetrySettingsCustomizers(retrySettingsCustomizers.orderedStream().toList());
            defaultListableBeanFactory.registerSingleton(join(entry.getKey(), RABBIT_TEMPLATE_CONFIGURER), configurer);
        }
        return defaultListableBeanFactory.getBean(join(defaultConfig, RABBIT_TEMPLATE_CONFIGURER), RabbitTemplateConfigurer.class);
    }

    @Bean
    @ConditionalOnSingleCandidate(ConnectionFactory.class)
    @ConditionalOnMissingBean(RabbitOperations.class)
    public RabbitTemplate rabbitTemplate(DataRabbitProperties properties) {
        String defaultConfig = Objects.requireNonNull(properties.getDefaultConfig(), "RabbitMQ默认配置必须配置");
        Map<String, RabbitProperties> dataMap = Objects.requireNonNull(properties.getConfig(), "RabbitMQ连接配置不存在");
        for (Map.Entry<String, RabbitProperties> entry : dataMap.entrySet()) {
            RabbitTemplate template = new RabbitTemplate();
            ConnectionFactory connectionFactory = defaultListableBeanFactory.getBean(join(entry.getKey(), RABBIT_CONNECTION_FACTORY), ConnectionFactory.class);
            RabbitTemplateConfigurer configurer = defaultListableBeanFactory.getBean(join(entry.getKey(), RABBIT_TEMPLATE_CONFIGURER), RabbitTemplateConfigurer.class);
            configurer.configure(template, connectionFactory);
            defaultListableBeanFactory.registerSingleton(join(entry.getKey(), RABBIT_TEMPLATE), template);
        }
        return defaultListableBeanFactory.getBean(join(defaultConfig, RABBIT_TEMPLATE), RabbitTemplate.class);
    }

    @Bean
    @ConditionalOnSingleCandidate(ConnectionFactory.class)
    @ConditionalOnBooleanProperty(
            name = {"spring.rabbitmq.dynamic"},
            matchIfMissing = true
    )
    @ConditionalOnMissingBean
    public AmqpAdmin amqpAdmin(DataRabbitProperties properties) {
        String defaultConfig = Objects.requireNonNull(properties.getDefaultConfig(), "RabbitMQ默认配置必须配置");
        Map<String, RabbitProperties> dataMap = Objects.requireNonNull(properties.getConfig(), "RabbitMQ连接配置不存在");
        for (Map.Entry<String, RabbitProperties> entry : dataMap.entrySet()) {
            ConnectionFactory connectionFactory = defaultListableBeanFactory.getBean(join(entry.getKey(), RABBIT_CONNECTION_FACTORY), ConnectionFactory.class);
            defaultListableBeanFactory.registerSingleton(join(entry.getKey(), AMQP_ADMIN), new RabbitAdmin(connectionFactory));
        }
        return defaultListableBeanFactory.getBean(join(defaultConfig, AMQP_ADMIN), RabbitAdmin.class);
    }
}
