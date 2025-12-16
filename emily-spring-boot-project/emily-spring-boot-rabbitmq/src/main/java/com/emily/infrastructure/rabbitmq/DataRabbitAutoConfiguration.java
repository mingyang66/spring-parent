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
import org.springframework.boot.amqp.autoconfigure.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@EnableConfigurationProperties(DataRabbitProperties.class)
@ConditionalOnProperty(prefix = DataRabbitProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@Import({DataRabbitAnnotationDrivenConfiguration.class, DataRabbitMessagingTemplateConfiguration.class})
public class DataRabbitAutoConfiguration implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(DataRabbitAutoConfiguration.class);

    @Override
    public void destroy() throws Exception {
        logger.info("<== 【销毁--自动化配置】----RabbitMQ消息中间件【DataRabbitAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("==> 【初始化--自动化配置】----RabbitMQ消息中间件件【DataRabbitAutoConfiguration】");
    }
}
