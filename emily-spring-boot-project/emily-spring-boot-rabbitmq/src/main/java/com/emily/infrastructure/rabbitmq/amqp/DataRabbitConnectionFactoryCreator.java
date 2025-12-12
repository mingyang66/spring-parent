package com.emily.infrastructure.rabbitmq.amqp;

import com.emily.infrastructure.rabbitmq.DataRabbitProperties;
import com.emily.infrastructure.rabbitmq.listener.DefaultMqConnectionListener;
import com.emily.infrastructure.rabbitmq.listener.DefaultMqExceptionHandler;
import com.rabbitmq.client.impl.CredentialsProvider;
import com.rabbitmq.client.impl.CredentialsRefreshService;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.amqp.autoconfigure.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.ApplicationContext;
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
public class DataRabbitConnectionFactoryCreator {
    private final DefaultListableBeanFactory defaultListableBeanFactory;

    public DataRabbitConnectionFactoryCreator(DefaultListableBeanFactory defaultListableBeanFactory) {
        this.defaultListableBeanFactory = defaultListableBeanFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    RabbitConnectionDetails rabbitConnectionDetails(DataRabbitProperties properties, ObjectProvider<SslBundles> sslBundles) {
        String defaultConfig = Objects.requireNonNull(properties.getDefaultConfig(), "RabbitMQ默认配置必须配置");
        Map<String, RabbitProperties> dataMap = Objects.requireNonNull(properties.getConfig(), "RabbitMQ连接配置不存在");
        for (Map.Entry<String, RabbitProperties> entry : dataMap.entrySet()) {
            defaultListableBeanFactory.registerSingleton(join(entry.getKey(), RABBIT_CONNECT_DETAILS), new DataPropertiesRabbitConnectionDetails(entry.getValue(), (SslBundles) sslBundles.getIfAvailable()));
        }
        return defaultListableBeanFactory.getBean(join(defaultConfig, RABBIT_CONNECT_DETAILS), RabbitConnectionDetails.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer(DataRabbitProperties properties, ResourceLoader resourceLoader,
                                                                                       ObjectProvider<CredentialsProvider> credentialsProvider,
                                                                                       ObjectProvider<CredentialsRefreshService> credentialsRefreshService) {
        String defaultConfig = Objects.requireNonNull(properties.getDefaultConfig(), "RabbitMQ默认配置必须配置");
        Map<String, RabbitProperties> dataMap = Objects.requireNonNull(properties.getConfig(), "RabbitMQ连接配置不存在");
        for (Map.Entry<String, RabbitProperties> entry : dataMap.entrySet()) {
            RabbitConnectionDetails connectionDetails = defaultListableBeanFactory.getBean(join(entry.getKey(), RABBIT_CONNECT_DETAILS), RabbitConnectionDetails.class);
            RabbitConnectionFactoryBeanConfigurer configurer = new RabbitConnectionFactoryBeanConfigurer(resourceLoader, entry.getValue(), connectionDetails);
            configurer.setCredentialsProvider((CredentialsProvider) credentialsProvider.getIfUnique());
            configurer.setCredentialsRefreshService((CredentialsRefreshService) credentialsRefreshService.getIfUnique());
            if (!defaultConfig.equals(entry.getKey())) {
                defaultListableBeanFactory.registerSingleton(join(entry.getKey(), RABBIT_CONNECTION_FACTORY_BEAN_CONFIGURER), configurer);
            }
        }
        return defaultListableBeanFactory.getBean(join(defaultConfig, RABBIT_CONNECTION_FACTORY_BEAN_CONFIGURER), RabbitConnectionFactoryBeanConfigurer.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public CachingConnectionFactoryConfigurer rabbitConnectionFactoryConfigurer(DataRabbitProperties properties, ObjectProvider<ConnectionNameStrategy> connectionNameStrategy) {
        String defaultConfig = Objects.requireNonNull(properties.getDefaultConfig(), "RabbitMQ默认配置必须配置");
        Map<String, RabbitProperties> dataMap = Objects.requireNonNull(properties.getConfig(), "RabbitMQ连接配置不存在");
        for (Map.Entry<String, RabbitProperties> entry : dataMap.entrySet()) {
            RabbitConnectionDetails connectionDetails = defaultListableBeanFactory.getBean(join(entry.getKey(), RABBIT_CONNECT_DETAILS), RabbitConnectionDetails.class);
            CachingConnectionFactoryConfigurer configurer = new CachingConnectionFactoryConfigurer(entry.getValue(), connectionDetails);
            configurer.setConnectionNameStrategy((ConnectionNameStrategy) connectionNameStrategy.getIfUnique());
            if (!defaultConfig.equals(entry.getKey())) {
                defaultListableBeanFactory.registerSingleton(join(entry.getKey(), RABBIT_CONNECTION_FACTORY_CONFIGURER), configurer);
            }
        }
        return defaultListableBeanFactory.getBean(join(defaultConfig, RABBIT_CONNECTION_FACTORY_CONFIGURER), CachingConnectionFactoryConfigurer.class);
    }

    @Bean
    @ConditionalOnMissingBean(ConnectionFactory.class)
    public CachingConnectionFactory rabbitConnectionFactory(DataRabbitProperties rabbitMqProperties,
                                                            ObjectProvider<ConnectionFactoryCustomizer> connectionFactoryCustomizers,
                                                            ApplicationContext context) throws Exception {
        String defaultConfig = Objects.requireNonNull(rabbitMqProperties.getDefaultConfig(), "RabbitMQ默认配置必须配置");
        Map<String, RabbitProperties> dataMap = Objects.requireNonNull(rabbitMqProperties.getConfig(), "RabbitMQ连接配置不存在");
        for (Map.Entry<String, RabbitProperties> entry : dataMap.entrySet()) {
            RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer = defaultListableBeanFactory.getBean(join(entry.getKey(), RABBIT_CONNECTION_FACTORY_BEAN_CONFIGURER), RabbitConnectionFactoryBeanConfigurer.class);
            CachingConnectionFactoryConfigurer rabbitCachingConnectionFactoryConfigurer = defaultListableBeanFactory.getBean(join(entry.getKey(), RABBIT_CONNECTION_FACTORY_CONFIGURER), CachingConnectionFactoryConfigurer.class);

            RabbitConnectionFactoryBean connectionFactoryBean = new DataSslBundleRabbitConnectionFactoryBean();
            rabbitConnectionFactoryBeanConfigurer.configure(connectionFactoryBean);
            connectionFactoryBean.afterPropertiesSet();
            com.rabbitmq.client.ConnectionFactory connectionFactory = (com.rabbitmq.client.ConnectionFactory)connectionFactoryBean.getObject();
            connectionFactoryCustomizers.orderedStream().forEach((customizer) -> {
                customizer.customize(connectionFactory);
            });
            CachingConnectionFactory factory = new CachingConnectionFactory(connectionFactory);
            rabbitCachingConnectionFactoryConfigurer.configure(factory);

            //设置TCP连接超时时间，默认：60000ms
            factory.getRabbitConnectionFactory().setConnectionTimeout(rabbitMqProperties.getConnection().getConnectionTimeout());
            //启用或禁用连接自动恢复，默认：false
            factory.getRabbitConnectionFactory().setAutomaticRecoveryEnabled(rabbitMqProperties.getConnection().isAutomaticRecovery());
            //设置连接恢复时间间隔，默认：5000ms
            factory.getRabbitConnectionFactory().setNetworkRecoveryInterval(rabbitMqProperties.getConnection().getNetworkRecoveryInterval());
            //启用或禁用拓扑恢复，默认：true【拓扑恢复功能可以帮助消费者重新声明之前定义的队列、交换机和绑定等拓扑结构】
            factory.getRabbitConnectionFactory().setTopologyRecoveryEnabled(rabbitMqProperties.getConnection().isTopologyRecovery());
            //替换默认异常处理DefaultExceptionHandler
            factory.getRabbitConnectionFactory().setExceptionHandler(new DefaultMqExceptionHandler(context));
            //添加连接监听器
            factory.addConnectionListener(new DefaultMqConnectionListener(factory, context));

            defaultListableBeanFactory.registerSingleton(join(entry.getKey(), RABBIT_CONNECTION_FACTORY), factory);
        }
        return defaultListableBeanFactory.getBean(join(defaultConfig, RABBIT_CONNECTION_FACTORY), CachingConnectionFactory.class);
    }
}
