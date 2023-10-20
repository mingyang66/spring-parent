package com.emily.infrastructure.redis;

import com.emily.infrastructure.core.context.ioc.BeanFactoryUtils;
import com.emily.infrastructure.logger.LoggerFactory;
import com.emily.infrastructure.redis.common.RedisBeanNames;
import com.emily.infrastructure.redis.connection.JedisDbConnectionConfiguration;
import com.emily.infrastructure.redis.connection.LettuceDbConnectionConfiguration;
import com.emily.infrastructure.redis.connection.PropertiesRedisDbConnectionDetails;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Role;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;
import java.util.Objects;

import static com.emily.infrastructure.redis.common.SerializerBeanUtils.jackson2JsonRedisSerializer;
import static com.emily.infrastructure.redis.common.SerializerBeanUtils.stringSerializer;

/**
 * Redis多数据源配置，参考源码：LettuceConnectionConfiguration
 * {@link org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration}
 *
 * @author Emily
 * @since 2021/07/11
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@AutoConfiguration(before = RedisAutoConfiguration.class)
@EnableConfigurationProperties(RedisDbProperties.class)
@ConditionalOnProperty(prefix = RedisDbProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@Import({LettuceDbConnectionConfiguration.class, JedisDbConnectionConfiguration.class})
public class RedisDbAutoConfiguration implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(RedisDbAutoConfiguration.class);

    private final RedisDbProperties redisDbProperties;

    public RedisDbAutoConfiguration(RedisDbProperties redisDbProperties) {
        this.redisDbProperties = redisDbProperties;
    }

    @Bean
    @ConditionalOnMissingBean(RedisConnectionDetails.class)
    PropertiesRedisDbConnectionDetails redisConnectionDetails() {
        String defaultConfig = Objects.requireNonNull(redisDbProperties.getDefaultConfig(), "Redis默认标识不可为空");
        PropertiesRedisDbConnectionDetails redisConnectionDetails = null;
        for (Map.Entry<String, RedisProperties> entry : redisDbProperties.getConfig().entrySet()) {
            String key = entry.getKey();
            RedisProperties properties = entry.getValue();
            PropertiesRedisDbConnectionDetails propertiesRedisDbConnectionDetails = new PropertiesRedisDbConnectionDetails(properties);
            if (defaultConfig.equals(key)) {
                redisConnectionDetails = propertiesRedisDbConnectionDetails;
            } else {
                BeanFactoryUtils.registerSingleton(String.join("", key, RedisBeanNames.REDIS_CONNECT_DETAILS), propertiesRedisDbConnectionDetails);
            }
        }
        return redisConnectionDetails;
    }

    @Bean
    @ConditionalOnMissingBean(name = RedisBeanNames.DEFAULT_REDIS_TEMPLATE)
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        String defaultConfig = Objects.requireNonNull(redisDbProperties.getDefaultConfig(), "Redis默认标识不可为空");
        RedisTemplate<Object, Object> redisTemplate = null;
        for (Map.Entry<String, RedisProperties> entry : redisDbProperties.getConfig().entrySet()) {
            String key = entry.getKey();
            RedisTemplate<Object, Object> template = new RedisTemplate<>();
            template.setKeySerializer(stringSerializer());
            template.setValueSerializer(jackson2JsonRedisSerializer());
            template.setHashKeySerializer(stringSerializer());
            template.setHashValueSerializer(jackson2JsonRedisSerializer());
            if (defaultConfig.equals(key)) {
                template.setConnectionFactory(redisConnectionFactory);
                redisTemplate = template;
            } else {
                template.setConnectionFactory(BeanFactoryUtils.getBean(String.join("", key, RedisBeanNames.REDIS_CONNECTION_FACTORY), RedisConnectionFactory.class));
                template.afterPropertiesSet();
                BeanFactoryUtils.registerSingleton(String.join("", key, RedisBeanNames.REDIS_TEMPLATE), template);
            }
        }

        return redisTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(name = RedisBeanNames.DEFAULT_STRING_REDIS_TEMPLATE)
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        String defaultConfig = Objects.requireNonNull(redisDbProperties.getDefaultConfig(), "Redis默认标识不可为空");
        StringRedisTemplate stringRedisTemplate = null;
        for (Map.Entry<String, RedisProperties> entry : redisDbProperties.getConfig().entrySet()) {
            String key = entry.getKey();
            StringRedisTemplate template = new StringRedisTemplate();
            template.setKeySerializer(stringSerializer());
            template.setValueSerializer(stringSerializer());
            template.setHashKeySerializer(stringSerializer());
            template.setHashValueSerializer(stringSerializer());
            if (defaultConfig.equals(key)) {
                template.setConnectionFactory(redisConnectionFactory);
                stringRedisTemplate = template;
            } else {
                template.setConnectionFactory(BeanFactoryUtils.getBean(String.join("", key, RedisBeanNames.REDIS_CONNECTION_FACTORY), RedisConnectionFactory.class));
                template.afterPropertiesSet();
                BeanFactoryUtils.registerSingleton(String.join("", key, RedisBeanNames.STRING_REDIS_TEMPLATE), template);
            }
        }

        return stringRedisTemplate;
    }


    @Override
    public void destroy() {
        logger.info("<== 【销毁--自动化配置】----Redis数据库多数据源组件【RedisDbAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() {
        logger.info("==> 【初始化--自动化配置】----Redis数据库多数据源组件【RedisDbAutoConfiguration】");
    }
}
