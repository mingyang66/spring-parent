package com.emily.infrastructure.rabbitmq.amqp;

import com.emily.infrastructure.rabbitmq.DataRabbitProperties;
import com.emily.infrastructure.rabbitmq.common.DataRabbitInfo;
import com.emily.infrastructure.rabbitmq.listener.DataRabbitListenerMethodInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.amqp.rabbit.config.ContainerCustomizer;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.amqp.autoconfigure.RabbitProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 消费端监听器扩展配置类
 *
 * @author :  Emily
 * @since :  2025/12/21 下午2:17
 */
@Configuration(proxyBeanMethods = false)
public class DataRabbitAnnotationDrivenListenerConfiguration {
    private final DataRabbitProperties properties;
    private final DefaultListableBeanFactory beanFactory;

    public DataRabbitAnnotationDrivenListenerConfiguration(DataRabbitProperties properties, DefaultListableBeanFactory beanFactory) {
        this.properties = properties;
        this.beanFactory = beanFactory;
    }

    @Bean(DataRabbitInfo.DEFAULT_SIMPLE_CONTAINER_CUSTOMIZER)
    @ConditionalOnMissingBean
    @ConditionalOnExpression(value = "'${spring.emily.rabbit.listener-type:simple}' == 'simple' and '${spring.emily.rabbit.store-log-messages:true}' == 'true'")
    public ContainerCustomizer<@NonNull SimpleMessageListenerContainer> simpleContainerCustomizer(ApplicationContext context) {
        ContainerCustomizer<@NonNull SimpleMessageListenerContainer> containerCustomizer = null;
        for (Map.Entry<String, RabbitProperties> entry : properties.getConfig().entrySet()) {
            ContainerCustomizer<@NonNull SimpleMessageListenerContainer> customizer = container -> container.setAdviceChain(new DataRabbitListenerMethodInterceptor(context));
            if (properties.getDefaultConfig().equals(entry.getKey())) {
                containerCustomizer = customizer;
            }
            beanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRabbitInfo.SIMPLE_CONTAINER_CUSTOMIZER), customizer);
        }
        return containerCustomizer;
    }

    @Bean(DataRabbitInfo.DEFAULT_DIRECT_CONTAINER_CUSTOMIZER)
    @ConditionalOnMissingBean
    @ConditionalOnExpression(value = "'${spring.emily.rabbit.listener-type:direct}' == 'direct' and '${spring.emily.rabbit.store-log-messages:true}' == 'true'")
    public ContainerCustomizer<@NonNull DirectMessageListenerContainer> directContainerCustomizer(ApplicationContext context) {
        ContainerCustomizer<@NonNull DirectMessageListenerContainer> containerCustomizer = null;
        for (Map.Entry<String, RabbitProperties> entry : properties.getConfig().entrySet()) {
            ContainerCustomizer<@NonNull DirectMessageListenerContainer> customizer = container -> container.setAdviceChain(new DataRabbitListenerMethodInterceptor(context));
            if (properties.getDefaultConfig().equals(entry.getKey())) {
                containerCustomizer = customizer;
            }
            beanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRabbitInfo.DIRECT_CONTAINER_CUSTOMIZER), customizer);
        }
        return containerCustomizer;
    }
}
