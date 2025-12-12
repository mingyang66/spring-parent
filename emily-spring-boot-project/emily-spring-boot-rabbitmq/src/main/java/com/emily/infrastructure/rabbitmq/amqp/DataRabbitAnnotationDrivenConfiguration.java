package com.emily.infrastructure.rabbitmq.amqp;

import com.emily.infrastructure.rabbitmq.RabbitMqProperties;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.AbstractRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.ContainerCustomizer;
import org.springframework.amqp.rabbit.config.DirectRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.amqp.autoconfigure.AbstractRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.amqp.autoconfigure.RabbitListenerRetrySettingsCustomizer;
import org.springframework.boot.amqp.autoconfigure.RabbitProperties;
import org.springframework.boot.amqp.autoconfigure.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnThreading;
import org.springframework.boot.thread.Threading;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.emily.infrastructure.rabbitmq.common.RabbitMqUtils.*;

/**
 * 配置RabbitMQ  注解驱动端点
 *
 * @author Emily
 * @since Created in 2022/11/17 10:27 上午
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(EnableRabbit.class)
public class DataRabbitAnnotationDrivenConfiguration {

    private final ObjectProvider<MessageConverter> messageConverter;
    private final ObjectProvider<MessageRecoverer> messageRecoverer;
    private final ObjectProvider<RabbitListenerRetrySettingsCustomizer> retrySettingsCustomizers;
    private final DefaultListableBeanFactory defaultListableBeanFactory;

    public DataRabbitAnnotationDrivenConfiguration(ObjectProvider<MessageConverter> messageConverter,
                                                   ObjectProvider<MessageRecoverer> messageRecoverer,
                                                   ObjectProvider<RabbitListenerRetrySettingsCustomizer> retrySettingsCustomizers,
                                                   DefaultListableBeanFactory defaultListableBeanFactory) {
        this.messageConverter = messageConverter;
        this.messageRecoverer = messageRecoverer;
        this.retrySettingsCustomizers = retrySettingsCustomizers;
        this.defaultListableBeanFactory = defaultListableBeanFactory;
    }

    /**
     * 容器监听器工厂配置类
     * 返回默认配置类，将其它容器监听器工厂配置类注入IOC容器
     *
     * @param rabbitMqProperties 属性配置
     * @return 默认容器监听器工厂配置类
     */
    @Bean
    @ConditionalOnMissingBean
    public AbstractRabbitListenerContainerFactoryConfigurer rabbitListenerContainerFactoryConfigurer(RabbitMqProperties rabbitMqProperties) {
        String defaultConfig = Objects.requireNonNull(rabbitMqProperties.getDefaultConfig(), "RabbitMQ默认配置必须配置");
        Map<String, RabbitProperties> dataMap = Objects.requireNonNull(rabbitMqProperties.getConfig(), "RabbitMQ连接配置不存在");
        AbstractRabbitListenerContainerFactoryConfigurer rabbitListenerContainerFactoryConfigurer = null;
        for (Map.Entry<String, RabbitProperties> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            RabbitProperties properties = entry.getValue();
            if (properties.getListener().getType().equals(RabbitProperties.ContainerType.DIRECT)) {
                DataDirectRabbitListenerContainerFactoryConfigurer configurer = new DataDirectRabbitListenerContainerFactoryConfigurer(properties);
                configurer.setMessageConverter(this.messageConverter.getIfUnique());
                configurer.setMessageRecoverer(this.messageRecoverer.getIfUnique());
                configurer.setRetrySettingsCustomizers(this.retrySettingsCustomizers.orderedStream().collect(Collectors.toList()));
                configurer.setRetrySettingsCustomizers(this.retrySettingsCustomizers.orderedStream().toList());
                if (defaultConfig.equals(key)) {
                    rabbitListenerContainerFactoryConfigurer = configurer;
                } else {
                    defaultListableBeanFactory.registerSingleton(join(key, DIRECT_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER), configurer);
                }
            } else {
                DataSimpleRabbitListenerContainerFactoryConfigurer configurer = new DataSimpleRabbitListenerContainerFactoryConfigurer(properties);
                configurer.setMessageConverter(this.messageConverter.getIfUnique());
                configurer.setMessageRecoverer(this.messageRecoverer.getIfUnique());
                configurer.setRetrySettingsCustomizers(this.retrySettingsCustomizers.orderedStream().collect(Collectors.toList()));
                if (defaultConfig.equals(key)) {
                    rabbitListenerContainerFactoryConfigurer = configurer;
                } else {
                    defaultListableBeanFactory.registerSingleton(join(key, SIMPLE_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER), configurer);
                }
            }
        }
        return rabbitListenerContainerFactoryConfigurer;
    }

    /**
     * 获取容器监听器工厂类
     * 默认返回默认的容器监听器工厂类，其它的容器监听器工厂类注入IOC容器
     *
     * @param rabbitMqProperties        属性配置
     * @param simpleContainerCustomizer 自定义simple容器配置类
     * @param directContainerCustomizer 自定义direct容器配置类
     * @return 默认的容器监听器工厂类
     */
    @Bean(name = DEFAULT_RABBIT_LISTENER_CONTAINER_FACTORY)
    @ConditionalOnMissingBean(name = DEFAULT_RABBIT_LISTENER_CONTAINER_FACTORY)
    RabbitListenerContainerFactory rabbitListenerContainerFactory(RabbitMqProperties rabbitMqProperties,
                                                                  ObjectProvider<ContainerCustomizer<SimpleMessageListenerContainer>> simpleContainerCustomizer,
                                                                  ObjectProvider<ContainerCustomizer<DirectMessageListenerContainer>> directContainerCustomizer) {
        String defaultConfig = Objects.requireNonNull(rabbitMqProperties.getDefaultConfig(), "RabbitMQ默认配置必须配置");
        Map<String, RabbitProperties> dataMap = Objects.requireNonNull(rabbitMqProperties.getConfig(), "RabbitMQ连接配置不存在");
        RabbitListenerContainerFactory rabbitListenerContainerFactory = null;
        for (Map.Entry<String, RabbitProperties> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            RabbitProperties properties = entry.getValue();
            ConnectionFactory connectionFactory;
            AbstractRabbitListenerContainerFactoryConfigurer configurer;
            if (defaultConfig.equals(key)) {
                connectionFactory = defaultListableBeanFactory.getBean(DEFAULT_RABBIT_CONNECTION_FACTORY, ConnectionFactory.class);
                configurer = defaultListableBeanFactory.getBean(DEFAULT_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER, AbstractRabbitListenerContainerFactoryConfigurer.class);
            } else {
                connectionFactory = defaultListableBeanFactory.getBean(join(key, RABBIT_CONNECTION_FACTORY), ConnectionFactory.class);
                if (RabbitProperties.ContainerType.DIRECT.equals(properties.getListener().getType())) {
                    configurer = defaultListableBeanFactory.getBean(join(key, DIRECT_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER), DataDirectRabbitListenerContainerFactoryConfigurer.class);
                } else {
                    configurer = defaultListableBeanFactory.getBean(join(key, SIMPLE_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER), DataSimpleRabbitListenerContainerFactoryConfigurer.class);
                }
            }
            AbstractRabbitListenerContainerFactory factory;
            if (RabbitProperties.ContainerType.DIRECT.equals(properties.getListener().getType())) {
                factory = new DirectRabbitListenerContainerFactory();
                configurer.configure(factory, connectionFactory);
                directContainerCustomizer.ifUnique(factory::setContainerCustomizer);
            } else {
                factory = new SimpleRabbitListenerContainerFactory();
                configurer.configure(factory, connectionFactory);
                simpleContainerCustomizer.ifUnique(factory::setContainerCustomizer);
            }
            if (defaultConfig.equals(key)) {
                rabbitListenerContainerFactory = factory;
            } else {
                defaultListableBeanFactory.registerSingleton(join(key, RABBIT_LISTENER_CONTAINER_FACTORY), factory);
            }
        }
        return rabbitListenerContainerFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnThreading(Threading.PLATFORM)
    SimpleRabbitListenerContainerFactoryConfigurer simpleRabbitListenerContainerFactoryConfigurer() {
        return this.simpleListenerConfigurer(null);
    }

    private DataSimpleRabbitListenerContainerFactoryConfigurer simpleListenerConfigurer(RabbitProperties rabbitProperties) {
        DataSimpleRabbitListenerContainerFactoryConfigurer configurer = new DataSimpleRabbitListenerContainerFactoryConfigurer(rabbitProperties);
        configurer.setMessageConverter((MessageConverter) this.messageConverter.getIfUnique());
        configurer.setMessageRecoverer((MessageRecoverer) this.messageRecoverer.getIfUnique());
        configurer.setRetrySettingsCustomizers(this.retrySettingsCustomizers.orderedStream().toList());
        return configurer;
    }

    private DataDirectRabbitListenerContainerFactoryConfigurer directListenerConfigurer(RabbitProperties rabbitProperties) {
        DataDirectRabbitListenerContainerFactoryConfigurer configurer = new DataDirectRabbitListenerContainerFactoryConfigurer(rabbitProperties);
        configurer.setMessageConverter((MessageConverter) this.messageConverter.getIfUnique());
        configurer.setMessageRecoverer((MessageRecoverer) this.messageRecoverer.getIfUnique());
        configurer.setRetrySettingsCustomizers(this.retrySettingsCustomizers.orderedStream().toList());
        return configurer;
    }
}
