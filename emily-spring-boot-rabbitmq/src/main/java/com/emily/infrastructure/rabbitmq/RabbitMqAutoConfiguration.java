package com.emily.infrastructure.rabbitmq;

import com.emily.infrastructure.rabbitmq.amqp.RabbitMqConnectionFactoryCreator;
import com.emily.infrastructure.rabbitmq.amqp.RabbitMqMessagingTemplateConfiguration;
import com.emily.infrastructure.rabbitmq.amqp.RabbitMqTemplateConfiguration;
import com.emily.infrastructure.rabbitmq.common.RabbitMqInfo;
import com.rabbitmq.client.impl.CredentialsProvider;
import com.rabbitmq.client.impl.CredentialsRefreshService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.config.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;

/**
 * @Description :  rabbitmq自动化配置
 * @Author :  Emily
 * @CreateDate :  Created in 2022/6/2 4:58 下午
 */
@AutoConfiguration(before = RabbitAutoConfiguration.class)
@EnableConfigurationProperties(RabbitMqProperties.class)
@ConditionalOnProperty(prefix = RabbitMqProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class RabbitMqAutoConfiguration implements InitializingBean, DisposableBean, BeanFactoryAware {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMqAutoConfiguration.class);

    private final ObjectProvider<MessageConverter> messageConverter;

    /**
     * RabbitAnnotationDrivenConfiguration类中初始化
     */
    private SimpleRabbitListenerContainerFactoryConfigurer simpleRabbitListenerContainerFactoryConfigurer;

    private DirectRabbitListenerContainerFactoryConfigurer directRabbitListenerContainerFactoryConfigurer;

    RabbitMqAutoConfiguration(ObjectProvider<MessageConverter> messageConverter,
                              SimpleRabbitListenerContainerFactoryConfigurer simpleRabbitListenerContainerFactoryConfigurer,
                              DirectRabbitListenerContainerFactoryConfigurer directRabbitListenerContainerFactoryConfigurer) {
        this.messageConverter = messageConverter;
        this.simpleRabbitListenerContainerFactoryConfigurer = simpleRabbitListenerContainerFactoryConfigurer;
        this.directRabbitListenerContainerFactoryConfigurer = directRabbitListenerContainerFactoryConfigurer;
    }

    @Bean
    public Object rabbitTemplates(RabbitMqProperties rabbitMqProperties,
                                  DefaultListableBeanFactory defaultListableBeanFactory,
                                  RabbitMqConnectionFactoryCreator factoryCreator,
                                  RabbitMqTemplateConfiguration templateConfiguration,
                                  RabbitMqMessagingTemplateConfiguration messagingTemplateConfiguration,
                                  ResourceLoader resourceLoader,
                                  ObjectProvider<ConnectionNameStrategy> connectionNameStrategy,
                                  ObjectProvider<CredentialsProvider> credentialsProvider,
                                  ObjectProvider<CredentialsRefreshService> credentialsRefreshService,
                                  ObjectProvider<ConnectionFactoryCustomizer> connectionFactoryCustomizers,
                                  ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers,
                                  ObjectProvider<ContainerCustomizer<SimpleMessageListenerContainer>> simpleContainerCustomizer,
                                  ObjectProvider<ContainerCustomizer<DirectMessageListenerContainer>> directContainerCustomizer) throws Exception {
        Map<String, RabbitProperties> dataMap = Objects.requireNonNull(rabbitMqProperties.getConfig(), "RabbitMq连接配置不存在");
        for (Map.Entry<String, RabbitProperties> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            RabbitProperties properties = entry.getValue();
            RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer = factoryCreator.rabbitConnectionFactoryBeanConfigurer(properties, resourceLoader, credentialsProvider, credentialsRefreshService);
            CachingConnectionFactoryConfigurer rabbitConnectionFactoryConfigurer = factoryCreator.rabbitConnectionFactoryConfigurer(properties, connectionNameStrategy);
            RabbitTemplateConfigurer configurer = templateConfiguration.rabbitTemplateConfigurer(properties, messageConverter, retryTemplateCustomizers);

            CachingConnectionFactory connectionFactory = factoryCreator.rabbitConnectionFactory(rabbitConnectionFactoryBeanConfigurer, rabbitConnectionFactoryConfigurer, connectionFactoryCustomizers);

            RabbitTemplate rabbitTemplate = templateConfiguration.rabbitTemplate(configurer, connectionFactory);
            defaultListableBeanFactory.registerSingleton(key, rabbitTemplate);

            AmqpAdmin amqpAdmin = templateConfiguration.amqpAdmin(connectionFactory);
            defaultListableBeanFactory.registerSingleton(MessageFormat.format("{0}{1}", key, RabbitMqInfo.AMQP_ADMIN), amqpAdmin);

            RabbitMessagingTemplate rabbitMessagingTemplate = messagingTemplateConfiguration.rabbitMessagingTemplate(rabbitTemplate);
            defaultListableBeanFactory.registerSingleton(MessageFormat.format("{0}{1}", key, RabbitMqInfo.RABBIT_MESSAGING_TEMPLATE), rabbitMessagingTemplate);

            BaseRabbitListenerContainerFactory rabbitListenerContainerFactory = getRabbitListenerContainerFactory(connectionFactory, properties, simpleContainerCustomizer, directContainerCustomizer);
            defaultListableBeanFactory.registerSingleton(MessageFormat.format("{0}{1}", key, RabbitMqInfo.LISTENER_CONTAINER_FACTORY), rabbitListenerContainerFactory);
        }
        return "UNSET";
    }

    /**
     * 参考：org.springframework.boot.autoconfigure.amqp.RabbitAnnotationDrivenConfiguration
     * @param connectionFactory
     * @param properties
     * @param simpleContainerCustomizer
     * @param directContainerCustomizer
     * @return
     */
    protected AbstractRabbitListenerContainerFactory getRabbitListenerContainerFactory(ConnectionFactory connectionFactory, RabbitProperties properties,
                                                                                       ObjectProvider<ContainerCustomizer<SimpleMessageListenerContainer>> simpleContainerCustomizer,
                                                                                       ObjectProvider<ContainerCustomizer<DirectMessageListenerContainer>> directContainerCustomizer) {
        if (RabbitProperties.ContainerType.DIRECT.equals(properties.getListener().getType())) {
            DirectRabbitListenerContainerFactory factory = new DirectRabbitListenerContainerFactory();
            this.directRabbitListenerContainerFactoryConfigurer.configure(factory, connectionFactory);
            directContainerCustomizer.ifUnique(factory::setContainerCustomizer);
            return factory;
        } else {
            SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
            this.simpleRabbitListenerContainerFactoryConfigurer.configure(factory, connectionFactory);
            simpleContainerCustomizer.ifUnique(factory::setContainerCustomizer);
            return factory;
        }
    }

    @Bean
    public RabbitMqConnectionFactoryCreator factoryCreator() {
        return new RabbitMqConnectionFactoryCreator();
    }

    @Bean
    public RabbitMqTemplateConfiguration templateConfiguration() {
        return new RabbitMqTemplateConfiguration();
    }

    @Bean
    public RabbitMqMessagingTemplateConfiguration messagingTemplateConfiguration() {
        return new RabbitMqMessagingTemplateConfiguration();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void destroy() throws Exception {
        logger.info("<== 【销毁--自动化配置】----RabbitMQ消息中间件【RabbitMqAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("==> 【初始化--自动化配置】----RabbitMQ消息中间件件【RabbitMqAutoConfiguration】");
    }
}
