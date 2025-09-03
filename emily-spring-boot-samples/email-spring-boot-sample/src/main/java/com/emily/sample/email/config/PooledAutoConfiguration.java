package com.emily.sample.email.config;

import com.emily.infrastructure.logback.factory.LoggerFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * AutoConfigurationImportSelector.AutoConfigurationGroup#selectImports()
 * AutoConfigurationSorter#getInPriorityOrder
 * MailSenderPropertiesConfiguration
 *
 * @author :  Emily
 * @since :  2025/8/28 上午10:48
 */
@AutoConfiguration
@EnableConfigurationProperties(MailProperties.class)
@ConditionalOnProperty(prefix = "spring.mail.properties.mail.pool", name = "enabled", havingValue = "true", matchIfMissing = true)
public class PooledAutoConfiguration implements InitializingBean, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(PooledAutoConfiguration.class);

    @Bean
    public PooledMailConnectionFactory pooledMailConnectionFactory(MailProperties properties, ObjectProvider<SslBundles> sslBundles) {
        return new PooledMailConnectionFactory(properties, sslBundles);
    }

    @Bean
    @ConditionalOnMissingBean(JavaMailSender.class)
    public PooledJavaMailSender pooledJavaMailSender(PooledMailConnectionFactory factory) {
        GenericObjectPoolConfig<JavaMailSender> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(8);
        config.setMaxIdle(8);
        config.setMinIdle(8);
        config.setTestWhileIdle(true);
        //禁用JMX监控，避免MXBean注册
        config.setJmxEnabled(false);
        GenericObjectPool<JavaMailSender> pool = new GenericObjectPool<>(factory, config);
        return new PooledJavaMailSender(pool);
    }


    @Override
    public void destroy() {
        logger.info("<== 【销毁--自动化配置】----邮件发送组件【PooledAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() {
        logger.info("==> 【初始化--自动化配置】----邮件发送组件【PooledAutoConfiguration】");
    }
}

