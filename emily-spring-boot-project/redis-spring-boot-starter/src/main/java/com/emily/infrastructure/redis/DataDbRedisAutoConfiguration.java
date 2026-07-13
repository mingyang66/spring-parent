package com.emily.infrastructure.redis;

import com.emily.infrastructure.redis.common.DataRedisInfo;
import com.emily.infrastructure.redis.connection.DataDbLettuceConnectionConfiguration;
import com.emily.infrastructure.redis.connection.DataDbPropertiesDataRedisConnectionDetails;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.boot.data.redis.autoconfigure.DataRedisConnectionDetails;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Role;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;

import java.util.Map;

import static com.emily.infrastructure.redis.common.SerializationUtils.jackson2JsonRedisSerializer;
import static com.emily.infrastructure.redis.common.SerializationUtils.stringSerializer;

/**
 * Redis多数据源配置，参考源码：LettuceConnectionConfiguration
 * 原始配置类{@link DataRedisAutoConfiguration}
 *
 * @author Emily
 * @since 2021/07/11
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@AutoConfiguration
@EnableConfigurationProperties(DataDbRedisProperties.class)
@ConditionalOnProperty(prefix = DataDbRedisProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@Import({DataDbLettuceConnectionConfiguration.class})
public class DataDbRedisAutoConfiguration implements InitializingBean, DisposableBean {

    private final DataDbRedisProperties properties;
    private final DefaultListableBeanFactory beanFactory;

    public DataDbRedisAutoConfiguration(DataDbRedisProperties properties, DefaultListableBeanFactory beanFactory) {
        Assert.notNull(properties.getDefaultConfig(), "Redis默认标识不可为空");
        Assert.notEmpty(properties.getConfig(), "Redis连接配置不存在");
        this.properties = properties;
        this.beanFactory = beanFactory;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean(DataRedisConnectionDetails.class)
    DataDbPropertiesDataRedisConnectionDetails redisConnectionDetails(ObjectProvider<SslBundles> sslBundles) {
        for (Map.Entry<String, RedisProperties> entry : properties.getConfig().entrySet()) {
            beanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRedisInfo.REDIS_CONNECT_DETAILS), new DataDbPropertiesDataRedisConnectionDetails(entry.getValue(), sslBundles.getIfAvailable()));
        }
        return beanFactory.getBean(StringUtils.join(properties.getDefaultConfig(), DataRedisInfo.REDIS_CONNECT_DETAILS), DataDbPropertiesDataRedisConnectionDetails.class);
    }

    @Bean(name = DataRedisInfo.DEFAULT_REDIS_TEMPLATE)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean(name = DataRedisInfo.DEFAULT_REDIS_TEMPLATE)
    @DependsOn(value = {DataRedisInfo.DEFAULT_REDIS_CONNECTION_FACTORY})
    public RedisTemplate<Object, Object> redisTemplate(Map<String, RedisConnectionFactory> connectionFactories) {
        RedisTemplate<Object, Object> redisTemplate = null;
        for (Map.Entry<String, RedisProperties> entry : properties.getConfig().entrySet()) {
            RedisTemplate<Object, Object> template = new RedisTemplate<>();
            template.setKeySerializer(stringSerializer());
            template.setValueSerializer(jackson2JsonRedisSerializer());
            template.setHashKeySerializer(stringSerializer());
            template.setHashValueSerializer(jackson2JsonRedisSerializer());
            template.setConnectionFactory(connectionFactories.get(StringUtils.join(entry.getKey(), DataRedisInfo.REDIS_CONNECTION_FACTORY)));
            if (properties.getDefaultConfig().equals(entry.getKey())) {
                redisTemplate = template;
            } else {
                template.afterPropertiesSet();
            }
            beanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRedisInfo.REDIS_TEMPLATE), template);
        }
        return redisTemplate;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean(name = DataRedisInfo.DEFAULT_STRING_REDIS_TEMPLATE)
    @DependsOn(value = {DataRedisInfo.DEFAULT_REDIS_CONNECTION_FACTORY})
    public StringRedisTemplate stringRedisTemplate(Map<String, RedisConnectionFactory> connectionFactories) {
        for (Map.Entry<String, RedisProperties> entry : properties.getConfig().entrySet()) {
            StringRedisTemplate template = new StringRedisTemplate();
            template.setKeySerializer(stringSerializer());
            template.setValueSerializer(stringSerializer());
            template.setHashKeySerializer(stringSerializer());
            template.setHashValueSerializer(stringSerializer());
            template.setConnectionFactory(connectionFactories.get(StringUtils.join(entry.getKey(), DataRedisInfo.REDIS_CONNECTION_FACTORY)));
            if (!properties.getDefaultConfig().equals(entry.getKey())) {
                template.afterPropertiesSet();
            }
            beanFactory.registerSingleton(StringUtils.join(entry.getKey(), DataRedisInfo.STRING_REDIS_TEMPLATE), template);
        }
        return beanFactory.getBean(StringUtils.join(properties.getDefaultConfig(), DataRedisInfo.STRING_REDIS_TEMPLATE), StringRedisTemplate.class);
    }


    @Override
    public void destroy() {
        LogHolder.LOG.info("<== 【销毁--自动化配置】----Redis数据库多数据源组件【DataDbRedisAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() {
        LogHolder.LOG.info("==> 【初始化--自动化配置】----Redis数据库多数据源组件【DataDbRedisAutoConfiguration】");
    }

    static class LogHolder {
        private static final Logger LOG = LoggerFactory.getLogger(DataDbRedisAutoConfiguration.class);
    }
}
