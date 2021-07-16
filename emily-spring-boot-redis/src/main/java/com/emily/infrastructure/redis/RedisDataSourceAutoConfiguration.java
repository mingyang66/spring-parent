package com.emily.infrastructure.redis;

import com.emily.infrastructure.common.utils.constant.CharacterUtils;
import com.emily.infrastructure.redis.utils.RedisDbUtils;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @program: spring-parent
 * @description: Redis多数据源配置
 * @author: Emily
 * @create: 2021/07/11
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@Configuration
@EnableConfigurationProperties(RedisDataSourceProperties.class)
public class RedisDataSourceAutoConfiguration {

    private DefaultListableBeanFactory defaultListableBeanFactory;
    private RedisDataSourceProperties redisDataSourceProperties;

    public RedisDataSourceAutoConfiguration(DefaultListableBeanFactory defaultListableBeanFactory, RedisDataSourceProperties redisDataSourceProperties) {
        this.defaultListableBeanFactory = defaultListableBeanFactory;
        this.redisDataSourceProperties = redisDataSourceProperties;
    }

    @PostConstruct
    public void stringRedisTemplate() {
        Map<String, RedisSentinelConfiguration> configs = createConfiguration(redisDataSourceProperties);
        configs.forEach((key, config) -> {
            // 获取标识对应的哨兵配置
            RedisSentinelConfiguration redisSentinelConfiguration = configs.get(key);
            // 获取StringRedisTemplate对象
            StringRedisTemplate stringRedisTemplate = createStringRedisTemplate(redisSentinelConfiguration);
            // 将StringRedisTemplate对象注入IOC容器bean
            defaultListableBeanFactory.registerSingleton(RedisDbUtils.getStringRedisTemplateBeanName(key), stringRedisTemplate);

            // 获取RedisTemplate对象
            RedisTemplate redisTemplate = createRedisTemplate(redisSentinelConfiguration);
            // 将RedisTemplate对象注入IOC容器
            defaultListableBeanFactory.registerSingleton(RedisDbUtils.getRedisTemplateBeanName(key), redisTemplate);
        });
    }

    /**
     * 创建 StringRedisTemplate对象
     *
     * @param redisSentinelConfiguration 哨兵配置类
     * @return
     */
    protected StringRedisTemplate createStringRedisTemplate(RedisSentinelConfiguration redisSentinelConfiguration) {
        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisSentinelConfiguration);
        factory.afterPropertiesSet();
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(factory);
        stringRedisTemplate.setKeySerializer(stringSerializer());
        stringRedisTemplate.setValueSerializer(jacksonSerializer());
        stringRedisTemplate.setHashKeySerializer(stringSerializer());
        stringRedisTemplate.setHashValueSerializer(jacksonSerializer());
        return stringRedisTemplate;
    }

    /**
     * 创建 RedisTemplate对象
     *
     * @param redisSentinelConfiguration 哨兵配置类
     * @return
     */
    protected RedisTemplate createRedisTemplate(RedisSentinelConfiguration redisSentinelConfiguration) {
        RedisTemplate redisTemplate = new RedisTemplate();
        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisSentinelConfiguration);
        factory.afterPropertiesSet();
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.setKeySerializer(stringSerializer());
        redisTemplate.setValueSerializer(jacksonSerializer());
        redisTemplate.setHashKeySerializer(stringSerializer());
        redisTemplate.setHashValueSerializer(jacksonSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * 配置第一个数据源的——交易中台
     */
    protected Map<String, RedisSentinelConfiguration> createConfiguration(RedisDataSourceProperties redisDataSourceProperties) {
        Map<String, RedisSentinelConfiguration> configs = Maps.newHashMap();
        Map<String, RedisProperties> redisPropertiesMap = redisDataSourceProperties.getConfig();
        redisPropertiesMap.forEach((key, properties) -> {
            RedisSentinelConfiguration result = new RedisSentinelConfiguration();
            result.setDatabase(properties.getDatabase());
            result.setMaster(properties.getSentinel().getMaster());
            result.setPassword(properties.getPassword());
            result.setSentinelPassword(properties.getSentinel().getPassword());
            result.setSentinels(toRedisNodes(properties.getSentinel().getNodes()));
            configs.put(key, result);
        });
        return configs;
    }

    /**
     * 将List转化为Iterable类型
     *
     * @param nodes
     * @return
     */
    protected Iterable<RedisNode> toRedisNodes(List<String> nodes) {
        Set<RedisNode> setRedisNode = new HashSet<>();
        nodes.forEach(node -> {
            String[] nodeInfo = node.split(CharacterUtils.COLON_EN);
            setRedisNode.add(RedisNode.newRedisNode().listeningAt(nodeInfo[0], Integer.valueOf(nodeInfo[1])).build());
        });
        return setRedisNode;
    }

    /**
     * 初始化string序列化对象
     */
    protected StringRedisSerializer stringSerializer() {
        return new StringRedisSerializer();
    }

    /**
     * 初始化jackson序列化对象
     */
    protected Jackson2JsonRedisSerializer<Object> jacksonSerializer() {
        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();

        //指定要序列化的域、field、get和set，以及修饰符范围，ANY是都有包括private和public
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        /**
         * objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
         * 第一个参数用于验证要反序列化的实际子类型是否对验证器使用的任何条件有效，在反序列化时必须设置，否则报异常
         * 第二个参数设置序列化的类型必须为非final类型，只有少数的类型（String、Boolean、Integer、Double）可以从JSON中正确推断
         */
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        // 解决jackson2无法反序列化LocalDateTime的问题
        objectMapper.registerModule(new JavaTimeModule());

        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        return jackson2JsonRedisSerializer;
    }
}
