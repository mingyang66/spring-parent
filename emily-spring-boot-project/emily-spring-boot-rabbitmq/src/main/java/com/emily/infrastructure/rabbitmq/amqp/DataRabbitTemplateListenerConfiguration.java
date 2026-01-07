package com.emily.infrastructure.rabbitmq.amqp;

import com.emily.infrastructure.rabbitmq.DataRabbitProperties;
import com.emily.infrastructure.rabbitmq.common.DataRabbitInfo;
import com.emily.infrastructure.rabbitmq.listener.DataRabbitMessagePostProcessor;
import com.emily.infrastructure.rabbitmq.listener.DataRabbitReturnsCallback;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.amqp.autoconfigure.RabbitProperties;
import org.springframework.boot.amqp.autoconfigure.RabbitTemplateCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 客户端发送消息监听器配置相关
 *
 * @author :  Emily
 * @since :  2025/12/21 下午2:28
 */
@Configuration(proxyBeanMethods = false)
public class DataRabbitTemplateListenerConfiguration {
    private final DataRabbitProperties properties;
    private final DefaultListableBeanFactory beanFactory;

    public DataRabbitTemplateListenerConfiguration(DataRabbitProperties properties, DefaultListableBeanFactory beanFactory) {
        this.properties = properties;
        this.beanFactory = beanFactory;
    }

    /**
     * 消息退回回调
     */
    @Bean(DataRabbitInfo.DEFAULT_RETURNS_CALLBACK)
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = DataRabbitProperties.PREFIX, name = "store-log-messages", havingValue = "true", matchIfMissing = true)
    public RabbitTemplate.ReturnsCallback returnsCallback(ApplicationContext context) {
        for (Map.Entry<String, RabbitProperties> entry : properties.getConfig().entrySet()) {
            beanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRabbitInfo.RETURNS_CALLBACK), new DataRabbitReturnsCallback(context));
        }
        return beanFactory.getBean(StringUtils.join(properties.getDefaultConfig(), DataRabbitInfo.RETURNS_CALLBACK), RabbitTemplate.ReturnsCallback.class);
    }

    /**
     * RabbitTemplate自定义设置
     */
    @Bean(DataRabbitInfo.DEFAULT_RABBIT_TEMPLATE_CUSTOMIZER)
    @ConditionalOnMissingBean
    public RabbitTemplateCustomizer rabbitTemplateCustomizer() {
        //提前初始化
        if (beanFactory.containsBean(DataRabbitInfo.DEFAULT_MESSAGE_POST_PROCESSOR)) {
            beanFactory.getBeansOfType(MessagePostProcessor.class, false, true);
        }
        if (beanFactory.containsBean(DataRabbitInfo.DEFAULT_RETURNS_CALLBACK)) {
            beanFactory.getBeansOfType(RabbitTemplate.ReturnsCallback.class, false, true);
        }
        for (Map.Entry<String, RabbitProperties> entry : properties.getConfig().entrySet()) {
            DataRabbitTemplateCustomizer dataRabbitTemplateCustomizer = new DataRabbitTemplateCustomizer(entry.getValue());
            if (beanFactory.containsBean(StringUtils.join(entry.getKey(), DataRabbitInfo.MESSAGE_POST_PROCESSOR))) {
                dataRabbitTemplateCustomizer.setMessagePostProcessor(beanFactory.getBean(StringUtils.join(entry.getKey(), DataRabbitInfo.MESSAGE_POST_PROCESSOR), MessagePostProcessor.class));
            }
            if (beanFactory.containsBean(StringUtils.join(entry.getKey(), DataRabbitInfo.RETURNS_CALLBACK))) {
                dataRabbitTemplateCustomizer.setReturnsCallback(beanFactory.getBean(StringUtils.join(entry.getKey(), DataRabbitInfo.RETURNS_CALLBACK), RabbitTemplate.ReturnsCallback.class));
            }
            beanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRabbitInfo.RABBIT_TEMPLATE_CUSTOMIZER), dataRabbitTemplateCustomizer);
        }
        return beanFactory.getBean(StringUtils.join(properties.getDefaultConfig(), DataRabbitInfo.RABBIT_TEMPLATE_CUSTOMIZER), RabbitTemplateCustomizer.class);
    }

    /**
     * 消息发送前预处理
     */
    @Bean(DataRabbitInfo.DEFAULT_MESSAGE_POST_PROCESSOR)
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = DataRabbitProperties.PREFIX, name = "store-log-messages", havingValue = "true", matchIfMissing = true)
    public MessagePostProcessor messagePostProcessor(ApplicationContext context) {
        for (Map.Entry<String, RabbitProperties> entry : properties.getConfig().entrySet()) {
            beanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRabbitInfo.MESSAGE_POST_PROCESSOR), new DataRabbitMessagePostProcessor(context));
        }
        return beanFactory.getBean(StringUtils.join(properties.getDefaultConfig(), DataRabbitInfo.MESSAGE_POST_PROCESSOR), MessagePostProcessor.class);
    }
}
