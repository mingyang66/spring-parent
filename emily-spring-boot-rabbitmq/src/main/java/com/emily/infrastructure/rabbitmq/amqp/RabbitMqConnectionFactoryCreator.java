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
import org.springframework.boot.autoconfigure.amqp.*;
import org.springframework.core.io.ResourceLoader;

/**
 * Rabbit工厂创建器 参考：{@link RabbitAutoConfiguration}
 *
 * @author Emily
 * @since Created in 2022/6/6 9:54 上午
 */
public class RabbitMqConnectionFactoryCreator {

    private final ResourceLoader resourceLoader;

    private final ObjectProvider<CredentialsProvider> credentialsProvider;

    private final ObjectProvider<CredentialsRefreshService> credentialsRefreshService;

    private final ObjectProvider<ConnectionNameStrategy> connectionNameStrategy;

    private final ObjectProvider<ConnectionFactoryCustomizer> connectionFactoryCustomizers;
    private final RabbitMqProperties properties;

    public RabbitMqConnectionFactoryCreator(ResourceLoader resourceLoader,
                                            ObjectProvider<CredentialsProvider> credentialsProvider,
                                            ObjectProvider<CredentialsRefreshService> credentialsRefreshService,
                                            ObjectProvider<ConnectionNameStrategy> connectionNameStrategy,
                                            ObjectProvider<ConnectionFactoryCustomizer> connectionFactoryCustomizers,
                                            RabbitMqProperties properties) {
        this.resourceLoader = resourceLoader;
        this.credentialsProvider = credentialsProvider;
        this.credentialsRefreshService = credentialsRefreshService;
        this.connectionNameStrategy = connectionNameStrategy;
        this.connectionFactoryCustomizers = connectionFactoryCustomizers;
        this.properties = properties;
    }

    /**
     * 创建RabbitConnectionFactoryBeanConfigurer对象
     *
     * @param properties 属性配置
     * @return 连接工厂配置类
     */
    public RabbitConnectionFactoryBeanConfigurer createRabbitConnectionFactoryBeanConfigurer(RabbitProperties properties) {
        RabbitConnectionFactoryBeanConfigurer configurer = new RabbitConnectionFactoryBeanConfigurer(resourceLoader,
                properties);
        configurer.setCredentialsProvider(credentialsProvider.getIfUnique());
        configurer.setCredentialsRefreshService(credentialsRefreshService.getIfUnique());
        return configurer;
    }

    public CachingConnectionFactoryConfigurer rabbitConnectionFactoryConfigurer(RabbitProperties rabbitProperties) {
        CachingConnectionFactoryConfigurer configurer = new CachingConnectionFactoryConfigurer(rabbitProperties);
        configurer.setConnectionNameStrategy(connectionNameStrategy.getIfUnique());
        return configurer;
    }

    public CachingConnectionFactory createRabbitConnectionFactory(
            RabbitConnectionFactoryBeanConfigurer rabbitConnectionFactoryBeanConfigurer,
            CachingConnectionFactoryConfigurer rabbitCachingConnectionFactoryConfigurer) throws Exception {

        RabbitConnectionFactoryBean connectionFactoryBean = new RabbitConnectionFactoryBean();
        rabbitConnectionFactoryBeanConfigurer.configure(connectionFactoryBean);
        connectionFactoryBean.afterPropertiesSet();
        ConnectionFactory connectionFactory = connectionFactoryBean.getObject();
        connectionFactoryCustomizers.orderedStream()
                .forEach((customizer) -> customizer.customize(connectionFactory));

        CachingConnectionFactory factory = new CachingConnectionFactory(connectionFactory);
        rabbitCachingConnectionFactoryConfigurer.configure(factory);

        //设置TCP连接超时时间，默认：60000ms
        factory.getRabbitConnectionFactory().setConnectionTimeout(properties.getConnectionTimeout());
        //启用或禁用连接自动恢复，默认：false
        factory.getRabbitConnectionFactory().setAutomaticRecoveryEnabled(properties.isAutomaticRecovery());
        //设置连接恢复时间间隔，默认：5000ms
        factory.getRabbitConnectionFactory().setNetworkRecoveryInterval(properties.getNetworkRecoveryInterval());
        //启用或禁用拓扑恢复，默认：true【拓扑恢复功能可以帮助消费者重新声明之前定义的队列、交换机和绑定等拓扑结构】
        factory.getRabbitConnectionFactory().setTopologyRecoveryEnabled(properties.isTopologyRecovery());
        //替换默认异常处理DefaultExceptionHandler
        factory.getRabbitConnectionFactory().setExceptionHandler(new DefaultMqExceptionHandler());
        //添加连接监听器
        factory.addConnectionListener(new DefaultMqConnectionListener(factory));
        return factory;
    }
}
