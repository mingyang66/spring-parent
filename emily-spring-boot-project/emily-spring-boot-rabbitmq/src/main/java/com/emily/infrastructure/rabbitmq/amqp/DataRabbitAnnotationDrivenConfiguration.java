package com.emily.infrastructure.rabbitmq.amqp;

import com.emily.infrastructure.rabbitmq.DataRabbitProperties;
import com.emily.infrastructure.rabbitmq.common.DataRabbitInfo;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.ContainerCustomizer;
import org.springframework.amqp.rabbit.config.DirectRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.RabbitListenerConfigUtils;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.amqp.autoconfigure.RabbitListenerRetrySettingsCustomizer;
import org.springframework.boot.amqp.autoconfigure.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnThreading;
import org.springframework.boot.thread.Threading;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Objects;

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
    private final DataRabbitProperties properties;

    public DataRabbitAnnotationDrivenConfiguration(ObjectProvider<MessageConverter> messageConverter,
                                                   ObjectProvider<MessageRecoverer> messageRecoverer,
                                                   ObjectProvider<RabbitListenerRetrySettingsCustomizer> retrySettingsCustomizers,
                                                   DefaultListableBeanFactory defaultListableBeanFactory,
                                                   DataRabbitProperties properties) {
        this.messageConverter = messageConverter;
        this.messageRecoverer = messageRecoverer;
        this.retrySettingsCustomizers = retrySettingsCustomizers;
        this.defaultListableBeanFactory = defaultListableBeanFactory;
        this.properties = properties;
        Assert.notNull(properties.getDefaultConfig(), "RabbitMQ默认配置必须配置");
        Assert.notNull(properties.getConfig(), "RabbitMQ连接配置不存在");
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnThreading(Threading.PLATFORM)
    DataSimpleRabbitListenerContainerFactoryConfigurer simpleRabbitListenerContainerFactoryConfigurer() {
        for (Map.Entry<String, RabbitProperties> entry : properties.getConfig().entrySet()) {
            defaultListableBeanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRabbitInfo.SIMPLE_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER), this.simpleListenerConfigurer(entry.getValue()));
        }
        return defaultListableBeanFactory.getBean(StringUtils.join(properties.getDefaultConfig(), DataRabbitInfo.SIMPLE_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER), DataSimpleRabbitListenerContainerFactoryConfigurer.class);
    }

    @Bean(
            name = {DataRabbitInfo.DEFAULT_SIMPLE_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER}
    )
    @ConditionalOnMissingBean
    @ConditionalOnThreading(Threading.VIRTUAL)
    DataSimpleRabbitListenerContainerFactoryConfigurer simpleRabbitListenerContainerFactoryConfigurerVirtualThreads() {
        for (Map.Entry<String, RabbitProperties> entry : properties.getConfig().entrySet()) {
            DataSimpleRabbitListenerContainerFactoryConfigurer configurer = this.simpleListenerConfigurer(entry.getValue());
            configurer.setTaskExecutor(new VirtualThreadTaskExecutor("rabbit-simple-"));
            defaultListableBeanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRabbitInfo.SIMPLE_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER), configurer);
        }
        return defaultListableBeanFactory.getBean(StringUtils.join(properties.getDefaultConfig(), DataRabbitInfo.SIMPLE_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER), DataSimpleRabbitListenerContainerFactoryConfigurer.class);
    }

    @Bean(
            name = {DataRabbitInfo.DEFAULT_RABBIT_LISTENER_CONTAINER_FACTORY}
    )
    @ConditionalOnMissingBean(
            name = {DataRabbitInfo.DEFAULT_RABBIT_LISTENER_CONTAINER_FACTORY}
    )
    @ConditionalOnProperty(
            name = {"spring.rabbitmq.listener.type"},
            havingValue = "simple",
            matchIfMissing = true
    )
    @DependsOn(value = {DataRabbitInfo.DEFAULT_RABBIT_CONNECTION_FACTORY, DataRabbitInfo.DEFAULT_SIMPLE_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER})
    SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(ObjectProvider<ContainerCustomizer<@NonNull SimpleMessageListenerContainer>> simpleContainerCustomizer) {
        for (Map.Entry<String, RabbitProperties> entry : properties.getConfig().entrySet()) {
            ConnectionFactory connectionFactory = defaultListableBeanFactory.getBean(StringUtils.join(entry.getKey(), DataRabbitInfo.RABBIT_CONNECTION_FACTORY), ConnectionFactory.class);
            DataSimpleRabbitListenerContainerFactoryConfigurer configurer = defaultListableBeanFactory.getBean(StringUtils.join(entry.getKey(), DataRabbitInfo.SIMPLE_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER), DataSimpleRabbitListenerContainerFactoryConfigurer.class);

            SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
            configurer.configure(factory, connectionFactory);
            Objects.requireNonNull(factory);
            simpleContainerCustomizer.ifUnique(factory::setContainerCustomizer);
            defaultListableBeanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRabbitInfo.RABBIT_LISTENER_CONTAINER_FACTORY), factory);
        }
        return defaultListableBeanFactory.getBean(StringUtils.join(properties.getDefaultConfig(), DataRabbitInfo.RABBIT_LISTENER_CONTAINER_FACTORY), SimpleRabbitListenerContainerFactory.class);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnThreading(Threading.PLATFORM)
    DataDirectRabbitListenerContainerFactoryConfigurer directRabbitListenerContainerFactoryConfigurer(DataRabbitProperties properties) {
        for (Map.Entry<String, RabbitProperties> entry : properties.getConfig().entrySet()) {
            defaultListableBeanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRabbitInfo.DIRECT_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER), this.directListenerConfigurer(entry.getValue()));
        }
        return defaultListableBeanFactory.getBean(StringUtils.join(properties.getDefaultConfig(), DataRabbitInfo.DIRECT_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER), DataDirectRabbitListenerContainerFactoryConfigurer.class);
    }

    @Bean(
            name = {DataRabbitInfo.DEFAULT_DIRECT_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER}
    )
    @ConditionalOnMissingBean
    @ConditionalOnThreading(Threading.VIRTUAL)
    DataDirectRabbitListenerContainerFactoryConfigurer directRabbitListenerContainerFactoryConfigurerVirtualThreads() {
        for (Map.Entry<String, RabbitProperties> entry : properties.getConfig().entrySet()) {
            DataDirectRabbitListenerContainerFactoryConfigurer configurer = this.directListenerConfigurer(entry.getValue());
            configurer.setTaskExecutor(new VirtualThreadTaskExecutor("rabbit-direct-"));
            defaultListableBeanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRabbitInfo.DIRECT_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER), configurer);
        }
        return defaultListableBeanFactory.getBean(StringUtils.join(properties.getDefaultConfig(), DataRabbitInfo.DIRECT_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER), DataDirectRabbitListenerContainerFactoryConfigurer.class);
    }

    @Bean(
            name = {DataRabbitInfo.DEFAULT_RABBIT_LISTENER_CONTAINER_FACTORY}
    )
    @ConditionalOnMissingBean(
            name = {DataRabbitInfo.DEFAULT_RABBIT_LISTENER_CONTAINER_FACTORY}
    )
    @ConditionalOnProperty(
            name = {"spring.rabbitmq.listener.type"},
            havingValue = "direct"
    )
    @DependsOn(value = {DataRabbitInfo.DEFAULT_RABBIT_CONNECTION_FACTORY, DataRabbitInfo.DEFAULT_DIRECT_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER})
    DirectRabbitListenerContainerFactory directRabbitListenerContainerFactory(ObjectProvider<ContainerCustomizer<@NonNull DirectMessageListenerContainer>> directContainerCustomizer) {
        for (Map.Entry<String, RabbitProperties> entry : properties.getConfig().entrySet()) {
            ConnectionFactory connectionFactory = defaultListableBeanFactory.getBean(StringUtils.join(entry.getKey(), DataRabbitInfo.RABBIT_CONNECTION_FACTORY), ConnectionFactory.class);
            DataDirectRabbitListenerContainerFactoryConfigurer configurer = defaultListableBeanFactory.getBean(StringUtils.join(entry.getKey(), DataRabbitInfo.DIRECT_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER), DataDirectRabbitListenerContainerFactoryConfigurer.class);

            DirectRabbitListenerContainerFactory factory = new DirectRabbitListenerContainerFactory();
            configurer.configure(factory, connectionFactory);
            Objects.requireNonNull(factory);
            directContainerCustomizer.ifUnique(factory::setContainerCustomizer);
            defaultListableBeanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRabbitInfo.RABBIT_LISTENER_CONTAINER_FACTORY), factory);
        }
        return defaultListableBeanFactory.getBean(StringUtils.join(properties.getDefaultConfig(), DataRabbitInfo.RABBIT_LISTENER_CONTAINER_FACTORY), DirectRabbitListenerContainerFactory.class);
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


    @Configuration(proxyBeanMethods = false)
    @EnableRabbit
    @ConditionalOnMissingBean(name = RabbitListenerConfigUtils.RABBIT_LISTENER_ANNOTATION_PROCESSOR_BEAN_NAME)
    static class EnableRabbitConfiguration {

    }
}
