package com.emily.infrastructure.rabbitmq;

import com.emily.infrastructure.common.utils.StrUtils;
import com.emily.infrastructure.rabbitmq.amqp.RabbitMqConnectionFactoryCreator;
import com.emily.infrastructure.rabbitmq.amqp.RabbitMqMessagingTemplateConfiguration;
import com.emily.infrastructure.rabbitmq.amqp.RabbitMqTemplateConfiguration;
import com.emily.infrastructure.rabbitmq.common.RabbitMqInfo;
import com.rabbitmq.client.impl.CredentialsProvider;
import com.rabbitmq.client.impl.CredentialsRefreshService;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

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
public class RabbitMqAutoConfiguration implements InitializingBean, DisposableBean {

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
                                  RabbitMqConnectionFactoryCreator connectionFactoryCreator,
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
        Map<String, RabbitProperties> dataMap = Objects.requireNonNull(rabbitMqProperties.getConfig(), "RabbitMQ连接配置不存在");
        String defaultConfig = Objects.requireNonNull(rabbitMqProperties.getDefaultConfig(), "RabbitMQ必须指定默认标识");
        Assert.isTrue(dataMap.keySet().contains(defaultConfig), "RabbitMQ默认配置标识不存在");
        for (Map.Entry<String, RabbitProperties> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            RabbitProperties properties = entry.getValue();

            //创建连接工厂配置类
            CachingConnectionFactoryConfigurer rabbitConnectionFactoryConfigurer = connectionFactoryCreator.rabbitConnectionFactoryConfigurer(properties, connectionNameStrategy);
            if (StringUtils.equals(defaultConfig, key)) {
                defaultListableBeanFactory.registerSingleton(StrUtils.toLowerFirstCase(RabbitMqInfo.RABBIT_CONNECTION_FACTORY_CONFIGURER), rabbitConnectionFactoryConfigurer);
            } else {
                defaultListableBeanFactory.registerSingleton(MessageFormat.format("{0}{1}", key, RabbitMqInfo.RABBIT_CONNECTION_FACTORY_CONFIGURER), rabbitConnectionFactoryConfigurer);
            }

            //创建RabbitConnectionFactoryBeanConfigurer对象
            RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer = connectionFactoryCreator.createRabbitConnectionFactoryBeanConfigurer(properties, resourceLoader, credentialsProvider, credentialsRefreshService);
            if (StringUtils.equals(defaultConfig, key)) {
                defaultListableBeanFactory.registerSingleton(StrUtils.toLowerFirstCase(RabbitMqInfo.RABBIT_CONNECTION_FACTORY_BEAN_CONFIGURER), rabbitConnectionFactoryBeanConfigurer);
            } else {
                defaultListableBeanFactory.registerSingleton(MessageFormat.format("{0}{1}", key, RabbitMqInfo.RABBIT_CONNECTION_FACTORY_BEAN_CONFIGURER), rabbitConnectionFactoryBeanConfigurer);
            }

            //创建CachingConnectionFactory对象
            CachingConnectionFactory connectionFactory = connectionFactoryCreator.createRabbitConnectionFactory(rabbitConnectionFactoryBeanConfigurer, rabbitConnectionFactoryConfigurer, connectionFactoryCustomizers);
            if (StringUtils.equals(defaultConfig, key)) {
                defaultListableBeanFactory.registerSingleton(StrUtils.toLowerFirstCase(RabbitMqInfo.RABBIT_CONNECTION_FACTORY), connectionFactory);
            } else {
                defaultListableBeanFactory.registerSingleton(MessageFormat.format("{0}{1}", key, RabbitMqInfo.RABBIT_CONNECTION_FACTORY), connectionFactory);
            }

            //创建RabbitTemplate配置类
            RabbitTemplateConfigurer rabbitTemplateConfigurer = templateConfiguration.createRabbitTemplateConfigurer(properties, messageConverter, retryTemplateCustomizers);
            if (StringUtils.equals(defaultConfig, key)) {
                defaultListableBeanFactory.registerSingleton(StrUtils.toLowerFirstCase(RabbitMqInfo.RABBIT_TEMPLATE_CONFIGURER), rabbitTemplateConfigurer);
            } else {
                defaultListableBeanFactory.registerSingleton(MessageFormat.format("{0}{1}", key, RabbitMqInfo.RABBIT_TEMPLATE_CONFIGURER), rabbitTemplateConfigurer);
            }

            //创建RabbitTemplate对象
            RabbitTemplate rabbitTemplate = templateConfiguration.createRabbitTemplate(rabbitTemplateConfigurer, connectionFactory);
            if (StringUtils.equals(defaultConfig, key)) {
                defaultListableBeanFactory.registerSingleton(StrUtils.toLowerFirstCase(RabbitMqInfo.RABBIT_TEMPLATE), rabbitTemplate);
            } else {
                defaultListableBeanFactory.registerSingleton(MessageFormat.format("{0}{1}", key, RabbitMqInfo.RABBIT_TEMPLATE), rabbitTemplate);
            }

            //创建AmqpAdmin对象
            AmqpAdmin amqpAdmin = templateConfiguration.createAmqpAdmin(connectionFactory);
            if (StringUtils.equals(defaultConfig, key)) {
                defaultListableBeanFactory.registerSingleton(StrUtils.toLowerFirstCase(RabbitMqInfo.AMQP_ADMIN), amqpAdmin);
            } else {
                defaultListableBeanFactory.registerSingleton(MessageFormat.format("{0}{1}", key, RabbitMqInfo.AMQP_ADMIN), amqpAdmin);
            }

            RabbitMessagingTemplate rabbitMessagingTemplate = messagingTemplateConfiguration.rabbitMessagingTemplate(rabbitTemplate);
            if (StringUtils.equals(defaultConfig, key)) {
                defaultListableBeanFactory.registerSingleton(StrUtils.toLowerFirstCase(RabbitMqInfo.RABBIT_MESSAGING_TEMPLATE), rabbitMessagingTemplate);
            } else {
                defaultListableBeanFactory.registerSingleton(MessageFormat.format("{0}{1}", key, RabbitMqInfo.RABBIT_MESSAGING_TEMPLATE), rabbitMessagingTemplate);
            }

            BaseRabbitListenerContainerFactory rabbitListenerContainerFactory = getRabbitListenerContainerFactory(connectionFactory, properties, simpleContainerCustomizer, directContainerCustomizer);
            //默认Rabbit容器工厂类BeanName命名规则是simpleRabbitListenerContainerFactory或directRabbitListenerContainerFactory
            if (StringUtils.equals(defaultConfig, key)) {
                defaultListableBeanFactory.registerSingleton(StrUtils.toLowerFirstCase(RabbitMqInfo.RABBIT_LISTENER_CONTAINER_FACTORY), rabbitListenerContainerFactory);
            } else {
                //非默认RabbitMQ容器工厂类BeanName命名规则是 标识+RabbitListenerContainerFactory
                defaultListableBeanFactory.registerSingleton(MessageFormat.format("{0}{1}", key, RabbitMqInfo.RABBIT_LISTENER_CONTAINER_FACTORY), rabbitListenerContainerFactory);
            }
        }
        return "UNSET";
    }

    /**
     * 参考：org.springframework.boot.autoconfigure.amqp.RabbitAnnotationDrivenConfiguration
     *
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
            factory.setConnectionFactory(connectionFactory);
            factory.setAcknowledgeMode(properties.getListener().getDirect().getAcknowledgeMode());
            factory.setPrefetchCount(properties.getListener().getDirect().getPrefetch());
            this.directRabbitListenerContainerFactoryConfigurer.configure(factory, connectionFactory);
            directContainerCustomizer.ifUnique(factory::setContainerCustomizer);
            return factory;
        } else {
            SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
            factory.setConnectionFactory(connectionFactory);
            factory.setAcknowledgeMode(properties.getListener().getSimple().getAcknowledgeMode());
            factory.setPrefetchCount(properties.getListener().getSimple().getPrefetch());
            this.simpleRabbitListenerContainerFactoryConfigurer.configure(factory, connectionFactory);
            simpleContainerCustomizer.ifUnique(factory::setContainerCustomizer);
            return factory;
        }
    }

    @Bean
    public RabbitMqConnectionFactoryCreator connectionFactoryCreator() {
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
    public void destroy() throws Exception {
        logger.info("<== 【销毁--自动化配置】----RabbitMQ消息中间件【RabbitMqAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("==> 【初始化--自动化配置】----RabbitMQ消息中间件件【RabbitMqAutoConfiguration】");
    }
}
