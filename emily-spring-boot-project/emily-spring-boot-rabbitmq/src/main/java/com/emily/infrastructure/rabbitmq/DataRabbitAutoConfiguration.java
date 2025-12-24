package com.emily.infrastructure.rabbitmq;

import com.emily.infrastructure.rabbitmq.amqp.DataRabbitAnnotationDrivenConfiguration;
import com.emily.infrastructure.rabbitmq.amqp.DataRabbitMessagingTemplateConfiguration;
import com.emily.infrastructure.rabbitmq.common.DataRabbitInfo;
import com.emily.infrastructure.rabbitmq.listener.DataRabbitMessagePostProcessor;
import com.emily.infrastructure.rabbitmq.listener.DataRabbitReturnsCallback;
import com.emily.infrastructure.rabbitmq.listener.DataRabbitTemplateCustomizer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.annotation.MultiRabbitBootstrapConfiguration;
import org.springframework.amqp.rabbit.annotation.RabbitBootstrapConfiguration;
import org.springframework.amqp.rabbit.annotation.RabbitListenerAnnotationBeanPostProcessor;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurationSelector;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration;
import org.springframework.boot.amqp.autoconfigure.RabbitProperties;
import org.springframework.boot.amqp.autoconfigure.RabbitTemplateCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Role;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * RabbitMQ自动化初始配置类
 *
 * @author Emily
 * @see RabbitListenerConfigurationSelector RabbitMQ监听器@RabbitListener相关类初始化入口
 * @see MultiRabbitBootstrapConfiguration
 * @see RabbitBootstrapConfiguration
 * @see RabbitListenerAnnotationBeanPostProcessor
 * @see RabbitListenerEndpointRegistry
 * @see RabbitAutoConfiguration
 * @since Created in 2022/6/2 4:58 下午
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@AutoConfiguration(before = RabbitAutoConfiguration.class)
@EnableConfigurationProperties(DataRabbitProperties.class)
@ConditionalOnProperty(prefix = DataRabbitProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@Import({DataRabbitAnnotationDrivenConfiguration.class, DataRabbitMessagingTemplateConfiguration.class})
public class DataRabbitAutoConfiguration implements InitializingBean, DisposableBean {

    private static final Logger LOG = LoggerFactory.getLogger(DataRabbitAutoConfiguration.class);
    private final DataRabbitProperties properties;
    private final DefaultListableBeanFactory beanFactory;

    public DataRabbitAutoConfiguration(DataRabbitProperties properties, DefaultListableBeanFactory beanFactory) {
        Assert.notNull(properties.getDefaultConfig(), "RabbitMQ默认配置必须配置");
        Assert.notEmpty(properties.getConfig(), "RabbitMQ连接配置不存在");
        this.properties = properties;
        this.beanFactory = beanFactory;
    }

    /**
     * 消息退回回调
     */
    @Bean(DataRabbitInfo.DEFAULT_RETURNS_CALLBACK)
    @ConditionalOnMissingBean
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
    @DependsOn(value = {DataRabbitInfo.DEFAULT_RETURNS_CALLBACK, DataRabbitInfo.DEFAULT_MESSAGE_POST_PROCESSOR})
    public RabbitTemplateCustomizer rabbitTemplateCustomizer() {
        for (Map.Entry<String, RabbitProperties> entry : properties.getConfig().entrySet()) {
            RabbitTemplate.ReturnsCallback returnsCallback = beanFactory.getBean(StringUtils.join(entry.getKey(), DataRabbitInfo.RETURNS_CALLBACK), RabbitTemplate.ReturnsCallback.class);
            DataRabbitTemplateCustomizer dataRabbitTemplateCustomizer = new DataRabbitTemplateCustomizer(entry.getValue(), returnsCallback);
            if (properties.isStoreLogSentMessages()) {
                dataRabbitTemplateCustomizer.setMessagePostProcessor(beanFactory.getBean(StringUtils.join(entry.getKey(), DataRabbitInfo.MESSAGE_POST_PROCESSOR), MessagePostProcessor.class));
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
    @ConditionalOnProperty(prefix = DataRabbitProperties.PREFIX, name = "post-processor", havingValue = "true", matchIfMissing = true)
    public MessagePostProcessor messagePostProcessor(ApplicationContext context) {
        for (Map.Entry<String, RabbitProperties> entry : properties.getConfig().entrySet()) {
            beanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRabbitInfo.MESSAGE_POST_PROCESSOR), new DataRabbitMessagePostProcessor(context));
        }
        return beanFactory.getBean(StringUtils.join(properties.getDefaultConfig(), DataRabbitInfo.MESSAGE_POST_PROCESSOR), MessagePostProcessor.class);
    }

    @Override
    public void destroy() throws Exception {
        LOG.info("<== 【销毁--自动化配置】----RabbitMQ消息中间件【DataRabbitAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOG.info("==> 【初始化--自动化配置】----RabbitMQ消息中间件件【DataRabbitAutoConfiguration】");
    }
}
