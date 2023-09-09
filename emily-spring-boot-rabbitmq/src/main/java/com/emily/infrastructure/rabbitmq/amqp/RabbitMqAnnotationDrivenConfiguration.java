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
import org.springframework.boot.autoconfigure.amqp.AbstractRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
public class RabbitMqAnnotationDrivenConfiguration {

    private final ObjectProvider<MessageConverter> messageConverter;
    private final ObjectProvider<MessageRecoverer> messageRecoverer;
    private final ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers;
    private final DefaultListableBeanFactory defaultListableBeanFactory;

    public RabbitMqAnnotationDrivenConfiguration(ObjectProvider<MessageConverter> messageConverter,
                                                 ObjectProvider<MessageRecoverer> messageRecoverer,
                                                 ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers,
                                                 DefaultListableBeanFactory defaultListableBeanFactory) {
        this.messageConverter = messageConverter;
        this.messageRecoverer = messageRecoverer;
        this.retryTemplateCustomizers = retryTemplateCustomizers;
        this.defaultListableBeanFactory = defaultListableBeanFactory;
    }

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
                DirectRabbitMqListenerContainerFactoryConfigurer configurer = new DirectRabbitMqListenerContainerFactoryConfigurer(properties);
                configurer.setMessageConverter(this.messageConverter.getIfUnique());
                configurer.setMessageRecoverer(this.messageRecoverer.getIfUnique());
                configurer.setRetryTemplateCustomizers(this.retryTemplateCustomizers.orderedStream().collect(Collectors.toList()));
                if (defaultConfig.equals(key)) {
                    rabbitListenerContainerFactoryConfigurer = configurer;
                } else {
                    defaultListableBeanFactory.registerSingleton(join(key, DIRECT_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER), configurer);
                }
            } else {
                SimpleRabbitMqListenerContainerFactoryConfigurer configurer = new SimpleRabbitMqListenerContainerFactoryConfigurer(properties);
                configurer.setMessageConverter(this.messageConverter.getIfUnique());
                configurer.setMessageRecoverer(this.messageRecoverer.getIfUnique());
                configurer.setRetryTemplateCustomizers(this.retryTemplateCustomizers.orderedStream().collect(Collectors.toList()));
                if (defaultConfig.equals(key)) {
                    rabbitListenerContainerFactoryConfigurer = configurer;
                } else {
                    defaultListableBeanFactory.registerSingleton(join(key, SIMPLE_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER), configurer);
                }
            }
        }
        return rabbitListenerContainerFactoryConfigurer;
    }

    @Bean(name = defaultContainerFactoryBeanName)
    @ConditionalOnMissingBean(name = defaultContainerFactoryBeanName)
    RabbitListenerContainerFactory rabbitListenerContainerFactory(RabbitMqProperties rabbitMqProperties,
                                                                  ObjectProvider<ContainerCustomizer<SimpleMessageListenerContainer>> simpleContainerCustomizer,
                                                                  ObjectProvider<ContainerCustomizer<DirectMessageListenerContainer>> directContainerCustomizer) {
        String defaultConfig = Objects.requireNonNull(rabbitMqProperties.getDefaultConfig(), "RabbitMQ默认配置必须配置");
        Map<String, RabbitProperties> dataMap = Objects.requireNonNull(rabbitMqProperties.getConfig(), "RabbitMQ连接配置不存在");
        RabbitListenerContainerFactory listenerContainerFactory = null;
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
                    configurer = defaultListableBeanFactory.getBean(join(key, DIRECT_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER), DirectRabbitMqListenerContainerFactoryConfigurer.class);
                } else {
                    configurer = defaultListableBeanFactory.getBean(join(key, SIMPLE_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER), SimpleRabbitMqListenerContainerFactoryConfigurer.class);
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
                listenerContainerFactory = factory;
            } else {
                defaultListableBeanFactory.registerSingleton(join(key, RABBIT_LISTENER_CONTAINER_FACTORY), factory);
            }
        }
        return listenerContainerFactory;
    }
}
