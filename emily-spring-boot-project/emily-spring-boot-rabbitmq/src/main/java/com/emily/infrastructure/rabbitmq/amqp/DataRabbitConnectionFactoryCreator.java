package com.emily.infrastructure.rabbitmq.amqp;

import com.emily.infrastructure.rabbitmq.DataRabbitProperties;
import com.emily.infrastructure.rabbitmq.common.DataRabbitInfo;
import com.emily.infrastructure.rabbitmq.listener.DataRabbitConnectionListener;
import com.emily.infrastructure.rabbitmq.listener.DataRabbitExceptionHandler;
import com.rabbitmq.client.impl.CredentialsProvider;
import com.rabbitmq.client.impl.CredentialsRefreshService;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

import java.util.Map;


/**
 * Rabbit工厂创建器 参考：{@link RabbitAutoConfiguration}
 *
 * @author Emily
 * @since Created in 2022/6/6 9:54 上午
 */
@Configuration(proxyBeanMethods = false)
public class DataRabbitConnectionFactoryCreator {
    private final DataRabbitProperties properties;
    private final DefaultListableBeanFactory beanFactory;

    public DataRabbitConnectionFactoryCreator(DataRabbitProperties properties, DefaultListableBeanFactory beanFactory) {
        Assert.notNull(properties.getDefaultConfig(), "RabbitMQ默认配置必须配置");
        Assert.notNull(properties.getConfig(), "RabbitMQ连接配置不存在");
        this.properties = properties;
        this.beanFactory = beanFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    RabbitConnectionDetails rabbitConnectionDetails(ObjectProvider<SslBundles> sslBundles) {
        for (Map.Entry<String, RabbitProperties> entry : properties.getConfig().entrySet()) {
            beanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRabbitInfo.RABBIT_CONNECT_DETAILS), new DataPropertiesRabbitConnectionDetails(entry.getValue(), (SslBundles) sslBundles.getIfAvailable()));
        }
        return beanFactory.getBean(StringUtils.join(properties.getDefaultConfig(), DataRabbitInfo.RABBIT_CONNECT_DETAILS), RabbitConnectionDetails.class);
    }

    @Bean
    @ConditionalOnMissingBean
    @DependsOn(value = {DataRabbitInfo.DEFAULT_RABBIT_CONNECT_DETAILS})
    public RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer(ResourceLoader resourceLoader,
                                                                                       ObjectProvider<CredentialsProvider> credentialsProvider,
                                                                                       ObjectProvider<CredentialsRefreshService> credentialsRefreshService) {
        for (Map.Entry<String, RabbitProperties> entry : properties.getConfig().entrySet()) {
            RabbitConnectionDetails connectionDetails = beanFactory.getBean(StringUtils.join(entry.getKey(), DataRabbitInfo.RABBIT_CONNECT_DETAILS), RabbitConnectionDetails.class);
            RabbitConnectionFactoryBeanConfigurer configurer = new RabbitConnectionFactoryBeanConfigurer(resourceLoader, entry.getValue(), connectionDetails);
            configurer.setCredentialsProvider((CredentialsProvider) credentialsProvider.getIfUnique());
            configurer.setCredentialsRefreshService((CredentialsRefreshService) credentialsRefreshService.getIfUnique());
            beanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRabbitInfo.RABBIT_CONNECTION_FACTORY_BEAN_CONFIGURER), configurer);
        }
        return beanFactory.getBean(StringUtils.join(properties.getDefaultConfig(), DataRabbitInfo.RABBIT_CONNECTION_FACTORY_BEAN_CONFIGURER), RabbitConnectionFactoryBeanConfigurer.class);
    }

    @Bean
    @ConditionalOnMissingBean
    @DependsOn(value = {DataRabbitInfo.DEFAULT_RABBIT_CONNECT_DETAILS})
    public CachingConnectionFactoryConfigurer rabbitConnectionFactoryConfigurer(ObjectProvider<ConnectionNameStrategy> connectionNameStrategy) {
        for (Map.Entry<String, RabbitProperties> entry : properties.getConfig().entrySet()) {
            RabbitConnectionDetails connectionDetails = beanFactory.getBean(StringUtils.join(entry.getKey(), DataRabbitInfo.RABBIT_CONNECT_DETAILS), RabbitConnectionDetails.class);
            CachingConnectionFactoryConfigurer configurer = new CachingConnectionFactoryConfigurer(entry.getValue(), connectionDetails);
            configurer.setConnectionNameStrategy((ConnectionNameStrategy) connectionNameStrategy.getIfUnique());
            beanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRabbitInfo.RABBIT_CONNECTION_FACTORY_CONFIGURER), configurer);
        }
        return beanFactory.getBean(StringUtils.join(properties.getDefaultConfig(), DataRabbitInfo.RABBIT_CONNECTION_FACTORY_CONFIGURER), CachingConnectionFactoryConfigurer.class);
    }

    @Bean
    @ConditionalOnMissingBean(ConnectionFactory.class)
    @DependsOn(value = {DataRabbitInfo.DEFAULT_RABBIT_CONNECTION_FACTORY_BEAN_CONFIGURER, DataRabbitInfo.DEFAULT_RABBIT_CONNECTION_FACTORY_CONFIGURER})
    public CachingConnectionFactory rabbitConnectionFactory(ObjectProvider<ConnectionFactoryCustomizer> connectionFactoryCustomizers,
                                                            ApplicationContext context) throws Exception {
        for (Map.Entry<String, RabbitProperties> entry : properties.getConfig().entrySet()) {
            RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer = beanFactory.getBean(StringUtils.join(entry.getKey(), DataRabbitInfo.RABBIT_CONNECTION_FACTORY_BEAN_CONFIGURER), RabbitConnectionFactoryBeanConfigurer.class);
            CachingConnectionFactoryConfigurer rabbitCachingConnectionFactoryConfigurer = beanFactory.getBean(StringUtils.join(entry.getKey(), DataRabbitInfo.RABBIT_CONNECTION_FACTORY_CONFIGURER), CachingConnectionFactoryConfigurer.class);

            RabbitConnectionFactoryBean connectionFactoryBean = new DataSslBundleRabbitConnectionFactoryBean();
            rabbitConnectionFactoryBeanConfigurer.configure(connectionFactoryBean);
            connectionFactoryBean.afterPropertiesSet();
            com.rabbitmq.client.ConnectionFactory connectionFactory = (com.rabbitmq.client.ConnectionFactory) connectionFactoryBean.getObject();
            connectionFactoryCustomizers.orderedStream().forEach((customizer) -> {
                customizer.customize(connectionFactory);
            });
            CachingConnectionFactory factory = new CachingConnectionFactory(connectionFactory);
            rabbitCachingConnectionFactoryConfigurer.configure(factory);

            //设置TCP连接超时时间，默认：60000ms
            factory.getRabbitConnectionFactory().setConnectionTimeout(properties.getConnection().getConnectionTimeout());
            //启用或禁用连接自动恢复，默认：false
            factory.getRabbitConnectionFactory().setAutomaticRecoveryEnabled(properties.getConnection().isAutomaticRecovery());
            //设置连接恢复时间间隔，默认：5000ms
            factory.getRabbitConnectionFactory().setNetworkRecoveryInterval(properties.getConnection().getNetworkRecoveryInterval());
            //启用或禁用拓扑恢复，默认：true【拓扑恢复功能可以帮助消费者重新声明之前定义的队列、交换机和绑定等拓扑结构】
            factory.getRabbitConnectionFactory().setTopologyRecoveryEnabled(properties.getConnection().isTopologyRecovery());
            //替换默认异常处理DefaultExceptionHandler
            factory.getRabbitConnectionFactory().setExceptionHandler(new DataRabbitExceptionHandler(context));
            //添加连接监听器
            factory.addConnectionListener(new DataRabbitConnectionListener(factory, context));

            beanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRabbitInfo.RABBIT_CONNECTION_FACTORY), factory);
        }
        return beanFactory.getBean(StringUtils.join(properties.getDefaultConfig(), DataRabbitInfo.RABBIT_CONNECTION_FACTORY), CachingConnectionFactory.class);
    }
}
