package com.emily.infrastructure.redis;

import com.emily.infrastructure.redis.connection.LettuceDbConnectionConfiguration;
import com.emily.infrastructure.redis.connection.PropertiesDataRedisDbConnectionDetails;
import com.emily.infrastructure.redis.factory.BeanFactoryProvider;
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

import java.util.Map;
import java.util.Objects;

import static com.emily.infrastructure.redis.common.RedisBeanNames.*;
import static com.emily.infrastructure.redis.common.SerializationUtils.jackson2JsonRedisSerializer;
import static com.emily.infrastructure.redis.common.SerializationUtils.stringSerializer;

/**
 * Redis多数据源配置，参考源码：LettuceConnectionConfiguration
 * {@link DataRedisAutoConfiguration}
 *
 * @author Emily
 * @since 2021/07/11
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@AutoConfiguration(before = DataRedisAutoConfiguration.class)
@EnableConfigurationProperties(DataRedisDbProperties.class)
@ConditionalOnProperty(prefix = DataRedisDbProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@Import({LettuceDbConnectionConfiguration.class})
public class DataRedisDbAutoConfiguration implements InitializingBean, DisposableBean {

    private final DataRedisDbProperties properties;

    public DataRedisDbAutoConfiguration(DefaultListableBeanFactory defaultListableBeanFactory, DataRedisDbProperties properties) {
        BeanFactoryProvider.registerDefaultListableBeanFactory(defaultListableBeanFactory);
        this.properties = properties;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean(DataRedisConnectionDetails.class)
    PropertiesDataRedisDbConnectionDetails redisConnectionDetails(ObjectProvider<SslBundles> sslBundles) {
        String defaultConfig = Objects.requireNonNull(properties.getDefaultConfig(), "Redis默认标识不可为空");
        for (Map.Entry<String, DataRedisProperties> entry : properties.getConfig().entrySet()) {
            BeanFactoryProvider.registerSingleton(join(entry.getKey(), REDIS_CONNECT_DETAILS), new PropertiesDataRedisDbConnectionDetails(entry.getValue(), sslBundles.getIfAvailable()));
        }
        return BeanFactoryProvider.getBean(join(defaultConfig, REDIS_CONNECT_DETAILS), PropertiesDataRedisDbConnectionDetails.class);
    }

    @Bean(name = DEFAULT_REDIS_TEMPLATE)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean(name = DEFAULT_REDIS_TEMPLATE)
    @DependsOn(value = {DEFAULT_REDIS_CONNECTION_FACTORY})
    public RedisTemplate<Object, Object> redisTemplate() {
        String defaultConfig = Objects.requireNonNull(properties.getDefaultConfig(), "Redis默认标识不可为空");
        RedisTemplate<Object, Object> redisTemplate = null;
        for (Map.Entry<String, DataRedisProperties> entry : properties.getConfig().entrySet()) {
            String key = entry.getKey();
            RedisTemplate<Object, Object> template = new RedisTemplate<>();
            template.setKeySerializer(stringSerializer());
            template.setValueSerializer(jackson2JsonRedisSerializer());
            template.setHashKeySerializer(stringSerializer());
            template.setHashValueSerializer(jackson2JsonRedisSerializer());
            template.setConnectionFactory(BeanFactoryProvider.getBean(join(key, REDIS_CONNECTION_FACTORY), RedisConnectionFactory.class));
            if (defaultConfig.equals(key)) {
                redisTemplate = template;
            } else {
                template.afterPropertiesSet();
            }
            BeanFactoryProvider.registerSingleton(join(key, REDIS_TEMPLATE), template);
        }
        return redisTemplate;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean(name = DEFAULT_STRING_REDIS_TEMPLATE)
    @DependsOn(value = {DEFAULT_REDIS_CONNECTION_FACTORY})
    public StringRedisTemplate stringRedisTemplate() {
        String defaultConfig = Objects.requireNonNull(properties.getDefaultConfig(), "Redis默认标识不可为空");
        for (Map.Entry<String, DataRedisProperties> entry : properties.getConfig().entrySet()) {
            String key = entry.getKey();
            StringRedisTemplate template = new StringRedisTemplate();
            template.setKeySerializer(stringSerializer());
            template.setValueSerializer(stringSerializer());
            template.setHashKeySerializer(stringSerializer());
            template.setHashValueSerializer(stringSerializer());
            template.setConnectionFactory(BeanFactoryProvider.getBean(join(key, REDIS_CONNECTION_FACTORY), RedisConnectionFactory.class));
            if (!defaultConfig.equals(key)) {
                template.afterPropertiesSet();
            }
            BeanFactoryProvider.registerSingleton(join(key, STRING_REDIS_TEMPLATE), template);
        }
        return BeanFactoryProvider.getBean(join(defaultConfig, STRING_REDIS_TEMPLATE), StringRedisTemplate.class);
    }


    @Override
    public void destroy() {
        LogHolder.LOG.info("<== 【销毁--自动化配置】----Redis数据库多数据源组件【RedisDbAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() {
        LogHolder.LOG.info("==> 【初始化--自动化配置】----Redis数据库多数据源组件【RedisDbAutoConfiguration】");
    }

    static class LogHolder {
        private static final Logger LOG = LoggerFactory.getLogger(DataRedisDbAutoConfiguration.class);
    }
}
