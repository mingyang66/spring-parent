package com.emily.infrastructure.rabbitmq;

import com.emily.infrastructure.rabbitmq.amqp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.MultiRabbitBootstrapConfiguration;
import org.springframework.amqp.rabbit.annotation.RabbitBootstrapConfiguration;
import org.springframework.amqp.rabbit.annotation.RabbitListenerAnnotationBeanPostProcessor;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurationSelector;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Role;

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
@EnableConfigurationProperties(RabbitMqProperties.class)
@ConditionalOnProperty(prefix = RabbitMqProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@Import({RabbitMqAnnotationDrivenConfiguration.class, RabbitMqConnectionFactoryCreator.class, RabbitMqTemplateConfiguration.class, RabbitMqMessagingTemplateConfiguration.class})
public class RabbitMqAutoConfiguration implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMqAutoConfiguration.class);

    /**
     * 自定义实现回调接口RabbitRetryTemplateCustomizer 作为RetryTemplate的一部分
     * 只有开启重试才会启用此自定义实现类
     *
     * @return 自定义RetryTemplate钩子类对象
     */
    @Bean
    @ConditionalOnMissingBean(RabbitRetryTemplateCustomizer.class)
    public RabbitRetryTemplateCustomizer rabbitRetryTemplateCustomizer() {
        return new RabbitMqRetryTemplateCustomizer();
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
