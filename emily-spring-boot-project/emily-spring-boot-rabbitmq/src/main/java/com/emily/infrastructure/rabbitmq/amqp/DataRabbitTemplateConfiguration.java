package com.emily.infrastructure.rabbitmq.amqp;

import com.emily.infrastructure.rabbitmq.DataRabbitProperties;
import com.emily.infrastructure.rabbitmq.common.DataRabbitInfo;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.util.Assert;

import java.util.Map;


/**
 * RabbitTemplate配置类 参考：RabbitAutoConfiguration.RabbitTemplateConfiguration
 *
 * @author Emily
 * @since Created in 2022/6/6 10:08 上午
 */
@Configuration(proxyBeanMethods = false)
@Import(DataRabbitConnectionFactoryCreator.class)
public class DataRabbitTemplateConfiguration {

    private final DataRabbitProperties properties;
    private final DefaultListableBeanFactory beanFactory;

    public DataRabbitTemplateConfiguration(DataRabbitProperties properties, DefaultListableBeanFactory beanFactory) {
        Assert.notNull(properties.getDefaultConfig(), "RabbitMQ默认配置必须配置");
        Assert.notEmpty(properties.getConfig(), "RabbitMQ连接配置不存在");
        this.properties = properties;
        this.beanFactory = beanFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public RabbitTemplateConfigurer rabbitTemplateConfigurer(ObjectProvider<MessageConverter> messageConverter,
                                                             ObjectProvider<RabbitTemplateRetrySettingsCustomizer> retrySettingsCustomizers) {
        for (Map.Entry<String, RabbitProperties> entry : properties.getConfig().entrySet()) {
            RabbitTemplateConfigurer configurer = new RabbitTemplateConfigurer(entry.getValue());
            configurer.setMessageConverter((MessageConverter) messageConverter.getIfUnique());
            configurer.setRetrySettingsCustomizers(retrySettingsCustomizers.orderedStream().toList());
            beanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRabbitInfo.RABBIT_TEMPLATE_CONFIGURER), configurer);
        }
        return beanFactory.getBean(StringUtils.join(properties.getDefaultConfig(), DataRabbitInfo.RABBIT_TEMPLATE_CONFIGURER), RabbitTemplateConfigurer.class);
    }

    @Bean
    @ConditionalOnSingleCandidate(ConnectionFactory.class)
    @ConditionalOnMissingBean(RabbitOperations.class)
    @DependsOn(value = {DataRabbitInfo.DEFAULT_RABBIT_CONNECTION_FACTORY, DataRabbitInfo.DEFAULT_RABBIT_TEMPLATE_CONFIGURER})
    public RabbitTemplate rabbitTemplate() {
        for (Map.Entry<String, RabbitProperties> entry : properties.getConfig().entrySet()) {
            RabbitTemplate template = new RabbitTemplate();
            ConnectionFactory connectionFactory = beanFactory.getBean(StringUtils.join(entry.getKey(), DataRabbitInfo.RABBIT_CONNECTION_FACTORY), ConnectionFactory.class);
            RabbitTemplateConfigurer configurer = beanFactory.getBean(StringUtils.join(entry.getKey(), DataRabbitInfo.RABBIT_TEMPLATE_CONFIGURER), RabbitTemplateConfigurer.class);
            configurer.configure(template, connectionFactory);
            beanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRabbitInfo.RABBIT_TEMPLATE), template);
        }
        return beanFactory.getBean(StringUtils.join(properties.getDefaultConfig(), DataRabbitInfo.RABBIT_TEMPLATE), RabbitTemplate.class);
    }

    @Bean
    @ConditionalOnSingleCandidate(ConnectionFactory.class)
    @ConditionalOnBooleanProperty(
            name = {"spring.rabbitmq.dynamic"},
            matchIfMissing = true
    )
    @ConditionalOnMissingBean
    public AmqpAdmin amqpAdmin() {
        for (Map.Entry<String, RabbitProperties> entry : properties.getConfig().entrySet()) {
            ConnectionFactory connectionFactory = beanFactory.getBean(StringUtils.join(entry.getKey(), DataRabbitInfo.RABBIT_CONNECTION_FACTORY), ConnectionFactory.class);
            beanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRabbitInfo.AMQP_ADMIN), new RabbitAdmin(connectionFactory));
        }
        return beanFactory.getBean(StringUtils.join(properties.getDefaultConfig(), DataRabbitInfo.AMQP_ADMIN), RabbitAdmin.class);
    }
}
