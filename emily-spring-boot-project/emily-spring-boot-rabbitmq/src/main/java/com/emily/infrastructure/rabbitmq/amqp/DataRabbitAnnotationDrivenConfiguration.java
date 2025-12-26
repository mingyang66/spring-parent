package com.emily.infrastructure.rabbitmq.amqp;

import com.emily.infrastructure.rabbitmq.DataRabbitProperties;
import com.emily.infrastructure.rabbitmq.annotation.EnableRabbit;
import com.emily.infrastructure.rabbitmq.common.DataRabbitInfo;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
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
import org.springframework.context.annotation.Import;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * 配置RabbitMQ  注解驱动端点 org.springframework.boot.amqp.autoconfigure.RabbitAnnotationDrivenConfiguration
 *
 * @author Emily
 * @since Created in 2022/11/17 10:27 上午
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(EnableRabbit.class)
@Import(value = {DataRabbitAnnotationDrivenListenerConfiguration.class})
public class DataRabbitAnnotationDrivenConfiguration {

    private final ObjectProvider<MessageConverter> messageConverter;
    private final ObjectProvider<MessageRecoverer> messageRecoverer;
    private final ObjectProvider<RabbitListenerRetrySettingsCustomizer> retrySettingsCustomizers;
    private final DataRabbitProperties properties;
    private final DefaultListableBeanFactory beanFactory;

    public DataRabbitAnnotationDrivenConfiguration(ObjectProvider<MessageConverter> messageConverter,
                                                   ObjectProvider<MessageRecoverer> messageRecoverer,
                                                   ObjectProvider<RabbitListenerRetrySettingsCustomizer> retrySettingsCustomizers,
                                                   DataRabbitProperties properties,
                                                   DefaultListableBeanFactory beanFactory) {
        Assert.notNull(properties.getDefaultConfig(), "RabbitMQ默认配置必须配置");
        Assert.notEmpty(properties.getConfig(), "RabbitMQ连接配置不存在");
        this.messageConverter = messageConverter;
        this.messageRecoverer = messageRecoverer;
        this.retrySettingsCustomizers = retrySettingsCustomizers;
        this.properties = properties;
        this.beanFactory = beanFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnThreading(Threading.PLATFORM)
    DataSimpleRabbitListenerContainerFactoryConfigurer simpleRabbitListenerContainerFactoryConfigurer() {
        for (Map.Entry<String, RabbitProperties> entry : properties.getConfig().entrySet()) {
            beanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRabbitInfo.SIMPLE_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER), this.simpleListenerConfigurer(entry.getValue()));
        }
        return beanFactory.getBean(StringUtils.join(properties.getDefaultConfig(), DataRabbitInfo.SIMPLE_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER), DataSimpleRabbitListenerContainerFactoryConfigurer.class);
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
            beanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRabbitInfo.SIMPLE_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER), configurer);
        }
        return beanFactory.getBean(StringUtils.join(properties.getDefaultConfig(), DataRabbitInfo.SIMPLE_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER), DataSimpleRabbitListenerContainerFactoryConfigurer.class);
    }

    @Bean(
            name = {DataRabbitInfo.DEFAULT_RABBIT_LISTENER_CONTAINER_FACTORY}
    )
    @ConditionalOnMissingBean(
            name = {DataRabbitInfo.DEFAULT_RABBIT_LISTENER_CONTAINER_FACTORY}
    )
    @ConditionalOnProperty(
            name = {"spring.emily.rabbit.listener-type"},
            havingValue = "simple",
            matchIfMissing = true
    )
    @DependsOn(value = {DataRabbitInfo.DEFAULT_RABBIT_CONNECTION_FACTORY, DataRabbitInfo.DEFAULT_SIMPLE_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER, DataRabbitInfo.DEFAULT_SIMPLE_CONTAINER_CUSTOMIZER})
    SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory() {
        for (Map.Entry<String, RabbitProperties> entry : properties.getConfig().entrySet()) {
            DataSimpleRabbitListenerContainerFactoryConfigurer configurer = beanFactory.getBean(StringUtils.join(entry.getKey(), DataRabbitInfo.SIMPLE_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER), DataSimpleRabbitListenerContainerFactoryConfigurer.class);
            ConnectionFactory connectionFactory = beanFactory.getBean(StringUtils.join(entry.getKey(), DataRabbitInfo.RABBIT_CONNECTION_FACTORY), ConnectionFactory.class);
            @SuppressWarnings("unchecked")
            ContainerCustomizer<@NonNull SimpleMessageListenerContainer> containerCustomizer = beanFactory.getBean(StringUtils.join(entry.getKey(), DataRabbitInfo.SIMPLE_CONTAINER_CUSTOMIZER), ContainerCustomizer.class);

            SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
            configurer.configure(factory, connectionFactory);
            factory.setContainerCustomizer(containerCustomizer);
            beanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRabbitInfo.RABBIT_LISTENER_CONTAINER_FACTORY), factory);
        }
        return beanFactory.getBean(StringUtils.join(properties.getDefaultConfig(), DataRabbitInfo.RABBIT_LISTENER_CONTAINER_FACTORY), SimpleRabbitListenerContainerFactory.class);
    }

    @Bean(
            name = {DataRabbitInfo.DEFAULT_DIRECT_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER}
    )
    @ConditionalOnMissingBean
    @ConditionalOnThreading(Threading.PLATFORM)
    DataDirectRabbitListenerContainerFactoryConfigurer directRabbitListenerContainerFactoryConfigurer(DataRabbitProperties properties) {
        for (Map.Entry<String, RabbitProperties> entry : properties.getConfig().entrySet()) {
            beanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRabbitInfo.DIRECT_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER), this.directListenerConfigurer(entry.getValue()));
        }
        return beanFactory.getBean(StringUtils.join(properties.getDefaultConfig(), DataRabbitInfo.DIRECT_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER), DataDirectRabbitListenerContainerFactoryConfigurer.class);
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
            beanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRabbitInfo.DIRECT_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER), configurer);
        }
        return beanFactory.getBean(StringUtils.join(properties.getDefaultConfig(), DataRabbitInfo.DIRECT_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER), DataDirectRabbitListenerContainerFactoryConfigurer.class);
    }

    @Bean(
            name = {DataRabbitInfo.DEFAULT_RABBIT_LISTENER_CONTAINER_FACTORY}
    )
    @ConditionalOnMissingBean(
            name = {DataRabbitInfo.DEFAULT_RABBIT_LISTENER_CONTAINER_FACTORY}
    )
    @ConditionalOnProperty(
            name = {"spring.emily.rabbit.listener-type"},
            havingValue = "direct"
    )
    @DependsOn(value = {DataRabbitInfo.DEFAULT_RABBIT_CONNECTION_FACTORY, DataRabbitInfo.DEFAULT_DIRECT_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER, DataRabbitInfo.DEFAULT_DIRECT_CONTAINER_CUSTOMIZER})
    DirectRabbitListenerContainerFactory directRabbitListenerContainerFactory() {
        for (Map.Entry<String, RabbitProperties> entry : properties.getConfig().entrySet()) {
            DataDirectRabbitListenerContainerFactoryConfigurer configurer = beanFactory.getBean(StringUtils.join(entry.getKey(), DataRabbitInfo.DIRECT_RABBIT_LISTENER_CONTAINER_FACTORY_CONFIGURER), DataDirectRabbitListenerContainerFactoryConfigurer.class);
            ConnectionFactory connectionFactory = beanFactory.getBean(StringUtils.join(entry.getKey(), DataRabbitInfo.RABBIT_CONNECTION_FACTORY), ConnectionFactory.class);
            @SuppressWarnings("unchecked")
            ContainerCustomizer<@NonNull DirectMessageListenerContainer> containerCustomizer = beanFactory.getBean(StringUtils.join(entry.getKey(), DataRabbitInfo.DIRECT_CONTAINER_CUSTOMIZER), ContainerCustomizer.class);

            DirectRabbitListenerContainerFactory factory = new DirectRabbitListenerContainerFactory();
            configurer.configure(factory, connectionFactory);
            factory.setContainerCustomizer(containerCustomizer);
            beanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRabbitInfo.RABBIT_LISTENER_CONTAINER_FACTORY), factory);
        }
        return beanFactory.getBean(StringUtils.join(properties.getDefaultConfig(), DataRabbitInfo.RABBIT_LISTENER_CONTAINER_FACTORY), DirectRabbitListenerContainerFactory.class);
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
