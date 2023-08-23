package com.emily.infrastructure.rabbitmq;

import com.emily.infrastructure.rabbitmq.amqp.RabbitMqAnnotationDrivenConfiguration;
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
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.core.io.ResourceLoader;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;

/**
 * rabbitmq自动化配置
 *
 * @author Emily
 * @since Created in 2022/6/2 4:58 下午
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@AutoConfiguration(before = RabbitAutoConfiguration.class)
@EnableConfigurationProperties(RabbitMqProperties.class)
@ConditionalOnProperty(prefix = RabbitMqProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class RabbitMqAutoConfiguration implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMqAutoConfiguration.class);

    private final ObjectProvider<MessageConverter> messageConverter;

    private final ObjectProvider<MessageRecoverer> messageRecoverer;

    private final ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers;

    private final ResourceLoader resourceLoader;

    private final ObjectProvider<ConnectionNameStrategy> connectionNameStrategy;

    private final ObjectProvider<CredentialsProvider> credentialsProvider;

    private final ObjectProvider<CredentialsRefreshService> credentialsRefreshService;

    private final ObjectProvider<ConnectionFactoryCustomizer> connectionFactoryCustomizers;

    private final ObjectProvider<ContainerCustomizer<SimpleMessageListenerContainer>> simpleContainerCustomizer;

    private final ObjectProvider<ContainerCustomizer<DirectMessageListenerContainer>> directContainerCustomizer;


    public RabbitMqAutoConfiguration(ObjectProvider<MessageConverter> messageConverter,
                                     ObjectProvider<MessageRecoverer> messageRecoverer,
                                     ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers,
                                     ResourceLoader resourceLoader,
                                     ObjectProvider<ConnectionNameStrategy> connectionNameStrategy,
                                     ObjectProvider<CredentialsProvider> credentialsProvider,
                                     ObjectProvider<CredentialsRefreshService> credentialsRefreshService,
                                     ObjectProvider<ConnectionFactoryCustomizer> connectionFactoryCustomizers,
                                     ObjectProvider<ContainerCustomizer<SimpleMessageListenerContainer>> simpleContainerCustomizer,
                                     ObjectProvider<ContainerCustomizer<DirectMessageListenerContainer>> directContainerCustomizer) {
        this.messageConverter = messageConverter;
        this.messageRecoverer = messageRecoverer;
        this.retryTemplateCustomizers = retryTemplateCustomizers;
        this.resourceLoader = resourceLoader;
        this.connectionNameStrategy = connectionNameStrategy;
        this.credentialsProvider = credentialsProvider;
        this.credentialsRefreshService = credentialsRefreshService;
        this.connectionFactoryCustomizers = connectionFactoryCustomizers;
        this.simpleContainerCustomizer = simpleContainerCustomizer;
        this.directContainerCustomizer = directContainerCustomizer;
    }

    /**
     * RabbitMQ消息中间件多元组件初始化
     *
     * @param rabbitMqProperties                    属性配置
     * @param defaultListableBeanFactory            todo
     * @param connectionFactoryCreator              todo
     * @param templateConfiguration                 todo
     * @param messagingTemplateConfiguration        todo
     * @param rabbitMqAnnotationDrivenConfiguration todo
     * @return 预定字符串
     * @throws Exception 异常
     */
    @Bean
    public Object rabbitTemplates(RabbitMqProperties rabbitMqProperties,
                                  DefaultListableBeanFactory defaultListableBeanFactory,
                                  RabbitMqConnectionFactoryCreator connectionFactoryCreator,
                                  RabbitMqTemplateConfiguration templateConfiguration,
                                  RabbitMqMessagingTemplateConfiguration messagingTemplateConfiguration,
                                  RabbitMqAnnotationDrivenConfiguration rabbitMqAnnotationDrivenConfiguration) throws Exception {
        Map<String, RabbitProperties> dataMap = Objects.requireNonNull(rabbitMqProperties.getConfig(), "RabbitMQ连接配置不存在");
        for (Map.Entry<String, RabbitProperties> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            RabbitProperties properties = entry.getValue();
            //创建连接工厂配置类
            CachingConnectionFactoryConfigurer rabbitConnectionFactoryConfigurer = connectionFactoryCreator.rabbitConnectionFactoryConfigurer(properties);
            //创建RabbitConnectionFactoryBeanConfigurer对象
            RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer = connectionFactoryCreator.createRabbitConnectionFactoryBeanConfigurer(properties);
            //创建CachingConnectionFactory对象
            CachingConnectionFactory connectionFactory = connectionFactoryCreator.createRabbitConnectionFactory(rabbitConnectionFactoryBeanConfigurer, rabbitConnectionFactoryConfigurer);
            //创建RabbitTemplate配置类
            RabbitTemplateConfigurer rabbitTemplateConfigurer = templateConfiguration.createRabbitTemplateConfigurer(properties);
            //创建RabbitTemplate对象
            RabbitTemplate rabbitTemplate = templateConfiguration.createRabbitTemplate(rabbitTemplateConfigurer, connectionFactory);
            defaultListableBeanFactory.registerSingleton(MessageFormat.format("{0}{1}", key, RabbitMqInfo.RABBIT_TEMPLATE), rabbitTemplate);
            //创建AmqpAdmin对象
            AmqpAdmin amqpAdmin = templateConfiguration.createAmqpAdmin(connectionFactory);
            defaultListableBeanFactory.registerSingleton(MessageFormat.format("{0}{1}", key, RabbitMqInfo.AMQP_ADMIN), amqpAdmin);
            //创建RabbitMessagingTemplate对象
            RabbitMessagingTemplate rabbitMessagingTemplate = messagingTemplateConfiguration.rabbitMessagingTemplate(rabbitTemplate);
            defaultListableBeanFactory.registerSingleton(MessageFormat.format("{0}{1}", key, RabbitMqInfo.RABBIT_MESSAGING_TEMPLATE), rabbitMessagingTemplate);
            //RabbitMQ监听器工厂配置类
            AbstractRabbitListenerContainerFactoryConfigurer rabbitListenerContainerFactoryConfigurer;
            if (properties.getListener().getType().equals(RabbitProperties.ContainerType.DIRECT)) {
                rabbitListenerContainerFactoryConfigurer = rabbitMqAnnotationDrivenConfiguration.createDirectRabbitListenerContainerFactoryConfigurer(properties);
                defaultListableBeanFactory.registerSingleton(MessageFormat.format("{0}{1}", key, RabbitMqInfo.DIRECT_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER), rabbitListenerContainerFactoryConfigurer);
            } else {
                rabbitListenerContainerFactoryConfigurer = rabbitMqAnnotationDrivenConfiguration.createSimpleRabbitListenerContainerFactoryConfigurer(properties);
                defaultListableBeanFactory.registerSingleton(MessageFormat.format("{0}{1}", key, RabbitMqInfo.SIMPLE_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER), rabbitListenerContainerFactoryConfigurer);
            }
            BaseRabbitListenerContainerFactory rabbitListenerContainerFactory = getRabbitListenerContainerFactory(connectionFactory, properties, rabbitListenerContainerFactoryConfigurer);
            defaultListableBeanFactory.registerSingleton(MessageFormat.format("{0}{1}", key, RabbitMqInfo.RABBIT_LISTENER_CONTAINER_FACTORY), rabbitListenerContainerFactory);
        }
        return "UNSET";
    }

    /**
     * 参考：org.springframework.boot.autoconfigure.amqp.RabbitAnnotationDrivenConfiguration
     *
     * @param connectionFactory                        工厂类
     * @param properties                               属性配置
     * @param rabbitListenerContainerFactoryConfigurer 工厂配置类
     * @return 连接工厂
     */
    protected AbstractRabbitListenerContainerFactory getRabbitListenerContainerFactory(ConnectionFactory connectionFactory, RabbitProperties properties,
                                                                                       AbstractRabbitListenerContainerFactoryConfigurer rabbitListenerContainerFactoryConfigurer) {
        if (RabbitProperties.ContainerType.DIRECT.equals(properties.getListener().getType())) {
            DirectRabbitListenerContainerFactory factory = new DirectRabbitListenerContainerFactory();
            rabbitListenerContainerFactoryConfigurer.configure(factory, connectionFactory);
            this.directContainerCustomizer.ifUnique(factory::setContainerCustomizer);
            return factory;
        } else {
            SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
            rabbitListenerContainerFactoryConfigurer.configure(factory, connectionFactory);
            this.simpleContainerCustomizer.ifUnique(factory::setContainerCustomizer);
            return factory;
        }
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public RabbitMqAnnotationDrivenConfiguration rabbitMqAnnotationDrivenConfiguration() {
        return new RabbitMqAnnotationDrivenConfiguration(messageConverter, messageRecoverer, retryTemplateCustomizers);
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public RabbitMqConnectionFactoryCreator connectionFactoryCreator(RabbitMqProperties properties) {
        return new RabbitMqConnectionFactoryCreator(resourceLoader, credentialsProvider, credentialsRefreshService, connectionNameStrategy, connectionFactoryCustomizers, properties);
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public RabbitMqTemplateConfiguration templateConfiguration() {
        return new RabbitMqTemplateConfiguration(messageConverter, retryTemplateCustomizers);
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public RabbitMqMessagingTemplateConfiguration messagingTemplateConfiguration() {
        return new RabbitMqMessagingTemplateConfiguration();
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
