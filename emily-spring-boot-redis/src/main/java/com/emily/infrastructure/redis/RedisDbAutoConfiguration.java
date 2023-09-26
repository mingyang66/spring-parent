package com.emily.infrastructure.redis;

import com.emily.infrastructure.logger.LoggerFactory;
import com.emily.infrastructure.redis.common.RedisInfo;
import com.emily.infrastructure.redis.connection.JedisDbConnectionConfiguration;
import com.emily.infrastructure.redis.connection.LettuceDbConnectionConfiguration;
import com.emily.infrastructure.redis.connection.PropertiesRedisDbConnectionDetails;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
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

    private final DefaultListableBeanFactory defaultListableBeanFactory;
    private final RedisDbProperties redisDbProperties;

    public RedisDbAutoConfiguration(DefaultListableBeanFactory defaultListableBeanFactory, RedisDbProperties redisDbProperties) {
        this.defaultListableBeanFactory = defaultListableBeanFactory;
        this.redisDbProperties = redisDbProperties;
    }

    @Bean
    @ConditionalOnMissingBean(RedisConnectionDetails.class)
    PropertiesRedisDbConnectionDetails redisConnectionDetails() {
        Map<String, RedisProperties> dataMap = Objects.requireNonNull(redisDbProperties.getConfig(), "Redis连接配置不存在");
        PropertiesRedisDbConnectionDetails redisConnectionDetails = null;
        for (Map.Entry<String, RedisProperties> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            RedisProperties properties = entry.getValue();
            PropertiesRedisDbConnectionDetails propertiesRedisDbConnectionDetails = new PropertiesRedisDbConnectionDetails(properties);
            if (redisDbProperties.getDefaultConfig().equals(key)) {
                redisConnectionDetails = propertiesRedisDbConnectionDetails;
            } else {
                defaultListableBeanFactory.registerSingleton(key + RedisInfo.REDIS_CONNECT_DETAILS, propertiesRedisDbConnectionDetails);
            }
        }
        return redisConnectionDetails;
    }

    @Bean
    @ConditionalOnMissingBean(name = RedisInfo.DEFAULT_REDIS_TEMPLATE)
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        Map<String, RedisProperties> dataMap = Objects.requireNonNull(redisDbProperties.getConfig(), "Redis连接配置不存在");
        RedisTemplate<Object, Object> redisTemplate = null;
        for (Map.Entry<String, RedisProperties> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            RedisTemplate<Object, Object> template = new RedisTemplate<>();
            if (redisDbProperties.getDefaultConfig().equals(key)) {
                template.setConnectionFactory(redisConnectionFactory);
                redisTemplate = template;
            } else {
                template.setConnectionFactory(defaultListableBeanFactory.getBean(key + RedisInfo.REDIS_CONNECTION_FACTORY, RedisConnectionFactory.class));
                template.afterPropertiesSet();
                defaultListableBeanFactory.registerSingleton(key + RedisInfo.REDIS_TEMPLATE, template);
            }
        }

        return redisTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(name = RedisInfo.DEFAULT_STRING_REDIS_TEMPLATE)
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        Map<String, RedisProperties> dataMap = Objects.requireNonNull(redisDbProperties.getConfig(), "Redis连接配置不存在");
        StringRedisTemplate stringRedisTemplate = null;
        for (Map.Entry<String, RedisProperties> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            StringRedisTemplate template = new StringRedisTemplate();
            if (redisDbProperties.getDefaultConfig().equals(key)) {
                template.setConnectionFactory(redisConnectionFactory);
                stringRedisTemplate = template;
            } else {
                template.setConnectionFactory(defaultListableBeanFactory.getBean(key + RedisInfo.REDIS_CONNECTION_FACTORY, RedisConnectionFactory.class));
                template.afterPropertiesSet();
                defaultListableBeanFactory.registerSingleton(key + RedisInfo.STRING_REDIS_TEMPLATE, template);
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
