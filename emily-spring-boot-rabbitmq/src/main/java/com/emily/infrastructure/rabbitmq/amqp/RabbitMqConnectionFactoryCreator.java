package com.emily.infrastructure.rabbitmq.amqp;

import com.emily.infrastructure.rabbitmq.RabbitMqProperties;
import com.emily.infrastructure.rabbitmq.listener.DefaultMqConnectionListener;
import com.emily.infrastructure.rabbitmq.listener.DefaultMqExceptionHandler;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.impl.CredentialsProvider;
import com.rabbitmq.client.impl.CredentialsRefreshService;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.amqp.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import java.util.Map;
import java.util.Objects;

import static com.emily.infrastructure.rabbitmq.common.RabbitMqUtils.*;

/**
 * Rabbit工厂创建器 参考：{@link RabbitAutoConfiguration}
 *
 * @author Emily
 * @since Created in 2022/6/6 9:54 上午
 */
@Configuration(proxyBeanMethods = false)
public class RabbitMqConnectionFactoryCreator {
    private final DefaultListableBeanFactory defaultListableBeanFactory;

    public RabbitMqConnectionFactoryCreator(DefaultListableBeanFactory defaultListableBeanFactory) {
        this.defaultListableBeanFactory = defaultListableBeanFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer(RabbitMqProperties rabbitMqProperties, ResourceLoader resourceLoader,
                                                                                       ObjectProvider<CredentialsProvider> credentialsProvider,
                                                                                       ObjectProvider<CredentialsRefreshService> credentialsRefreshService) {
        String defaultConfig = Objects.requireNonNull(rabbitMqProperties.getDefaultConfig(), "RabbitMQ默认配置必须配置");
        Map<String, RabbitProperties> dataMap = Objects.requireNonNull(rabbitMqProperties.getConfig(), "RabbitMQ连接配置不存在");
        RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer = null;
        for (Map.Entry<String, RabbitProperties> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            RabbitProperties properties = entry.getValue();
            RabbitConnectionFactoryBeanConfigurer configurer = new RabbitConnectionFactoryBeanConfigurer(resourceLoader, properties);
            configurer.setCredentialsProvider(credentialsProvider.getIfUnique());
            configurer.setCredentialsRefreshService(credentialsRefreshService.getIfUnique());
            if (defaultConfig.equals(key)) {
                rabbitConnectionFactoryBeanConfigurer = configurer;
            } else {
                defaultListableBeanFactory.registerSingleton(join(key, RABBIT_CONNECTION_FACTORY_BEAN_CONFIGURER), configurer);
            }
        }
        return rabbitConnectionFactoryBeanConfigurer;
    }

    @Bean
    @ConditionalOnMissingBean
    public CachingConnectionFactoryConfigurer rabbitConnectionFactoryConfigurer(RabbitMqProperties rabbitMqProperties, ObjectProvider<ConnectionNameStrategy> connectionNameStrategy) {
        String defaultConfig = Objects.requireNonNull(rabbitMqProperties.getDefaultConfig(), "RabbitMQ默认配置必须配置");
        Map<String, RabbitProperties> dataMap = Objects.requireNonNull(rabbitMqProperties.getConfig(), "RabbitMQ连接配置不存在");
        CachingConnectionFactoryConfigurer cachingConnectionFactoryConfigurer = null;
        for (Map.Entry<String, RabbitProperties> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            RabbitProperties properties = entry.getValue();
            CachingConnectionFactoryConfigurer configurer = new CachingConnectionFactoryConfigurer(properties);
            configurer.setConnectionNameStrategy(connectionNameStrategy.getIfUnique());
            if (defaultConfig.equals(key)) {
                cachingConnectionFactoryConfigurer = configurer;
            } else {
                defaultListableBeanFactory.registerSingleton(join(key, RABBIT_CONNECTION_FACTORY_CONFIGURER), configurer);
            }
        }
        return cachingConnectionFactoryConfigurer;
    }


    @Bean
    @ConditionalOnMissingBean(ConnectionFactory.class)
    public CachingConnectionFactory rabbitConnectionFactory(RabbitMqProperties rabbitMqProperties,
                                                            ObjectProvider<ConnectionFactoryCustomizer> connectionFactoryCustomizers) throws Exception {
        String defaultConfig = Objects.requireNonNull(rabbitMqProperties.getDefaultConfig(), "RabbitMQ默认配置必须配置");
        Map<String, RabbitProperties> dataMap = Objects.requireNonNull(rabbitMqProperties.getConfig(), "RabbitMQ连接配置不存在");
        CachingConnectionFactory rabbitConnectionFactory = null;
        for (Map.Entry<String, RabbitProperties> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer;
            CachingConnectionFactoryConfigurer rabbitCachingConnectionFactoryConfigurer;
            if (rabbitMqProperties.getDefaultConfig().equals(key)) {
                rabbitConnectionFactoryBeanConfigurer = defaultListableBeanFactory.getBean(DEFAULT_RABBIT_CONNECTION_FACTORY_BEAN_CONFIGURER, RabbitConnectionFactoryBeanConfigurer.class);
                rabbitCachingConnectionFactoryConfigurer = defaultListableBeanFactory.getBean(DEFAULT_RABBIT_CONNECTION_FACTORY_CONFIGURER, CachingConnectionFactoryConfigurer.class);
            } else {
                rabbitConnectionFactoryBeanConfigurer = defaultListableBeanFactory.getBean(join(key, RABBIT_CONNECTION_FACTORY_BEAN_CONFIGURER), RabbitConnectionFactoryBeanConfigurer.class);
                rabbitCachingConnectionFactoryConfigurer = defaultListableBeanFactory.getBean(join(key, RABBIT_CONNECTION_FACTORY_CONFIGURER), CachingConnectionFactoryConfigurer.class);
            }
            RabbitConnectionFactoryBean connectionFactoryBean = new RabbitConnectionFactoryBean();
            rabbitConnectionFactoryBeanConfigurer.configure(connectionFactoryBean);
            connectionFactoryBean.afterPropertiesSet();
            ConnectionFactory connectionFactory = connectionFactoryBean.getObject();
            connectionFactoryCustomizers.orderedStream()
                    .forEach((customizer) -> customizer.customize(connectionFactory));

            CachingConnectionFactory factory = new CachingConnectionFactory(connectionFactory);
            rabbitCachingConnectionFactoryConfigurer.configure(factory);

            //设置TCP连接超时时间，默认：60000ms
            factory.getRabbitConnectionFactory().setConnectionTimeout(rabbitMqProperties.getConnectionTimeout());
            //启用或禁用连接自动恢复，默认：false
            factory.getRabbitConnectionFactory().setAutomaticRecoveryEnabled(rabbitMqProperties.isAutomaticRecovery());
            //设置连接恢复时间间隔，默认：5000ms
            factory.getRabbitConnectionFactory().setNetworkRecoveryInterval(rabbitMqProperties.getNetworkRecoveryInterval());
            //启用或禁用拓扑恢复，默认：true【拓扑恢复功能可以帮助消费者重新声明之前定义的队列、交换机和绑定等拓扑结构】
            factory.getRabbitConnectionFactory().setTopologyRecoveryEnabled(rabbitMqProperties.isTopologyRecovery());
            //替换默认异常处理DefaultExceptionHandler
            factory.getRabbitConnectionFactory().setExceptionHandler(new DefaultMqExceptionHandler());
            //添加连接监听器
            factory.addConnectionListener(new DefaultMqConnectionListener(factory));
            if (defaultConfig.equals(key)) {
                rabbitConnectionFactory = factory;
            } else {
                defaultListableBeanFactory.registerSingleton(join(key, RABBIT_CONNECTION_FACTORY), factory);
            }
        }
        return rabbitConnectionFactory;
    }
}
