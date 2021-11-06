package com.emily.infrastructure.datasource.redis;

import com.emily.infrastructure.datasource.redis.factory.RedisDbConnectionConfiguration;
import com.emily.infrastructure.datasource.redis.factory.RedisDbConnectionFactory;
import com.emily.infrastructure.datasource.redis.factory.RedisDbFactory;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.Ordered;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * @program: spring-parent
 * @description: Redis多数据源配置，参考源码：LettuceConnectionConfiguration
 * @author: Emily
 * @create: 2021/07/11
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE+1)
@EnableConfigurationProperties(RedisDbProperties.class)
@ConditionalOnProperty(prefix = RedisDbProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class RedisDbAutoConfiguration implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(RedisDbAutoConfiguration.class);

    private DefaultListableBeanFactory defaultListableBeanFactory;

    public RedisDbAutoConfiguration(DefaultListableBeanFactory defaultListableBeanFactory) {
        this.defaultListableBeanFactory = defaultListableBeanFactory;
    }

    /**
     * 策略实现类，提供所有基础设施的构建，如环境变量和线程池，以便客户端能够正确使用。
     * 如果在RedisClient客户端外部创建，则可以在多个客户端实例之间共享，ClientResources的实现类是有状态的，
     * 在不使用后必须调用shutdown方法
     *
     * @return
     */
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(ClientResources.class)
    public DefaultClientResources clientResources() {
        return DefaultClientResources.create();
    }

    /**
     * Redis连接工厂类
     *
     * @return
     */
    @Bean
    public RedisDbConnectionFactory redisDbConnectionFactory() {
        return new RedisDbConnectionFactory();
    }

    /**
     * 初始化redis相关bean
     */
    @Bean
    public Object initTargetRedis(ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers, ClientResources clientResources, RedisDbProperties redisDbProperties) {

        Assert.notNull(clientResources, "ClientResources must not be null");
        Assert.notNull(clientResources, "RedisDbProperties must not be null");

        //创建Redis数据源配置key-value映射
        Table<String, RedisProperties, RedisConfiguration> table = createConfiguration(redisDbProperties);
        table.rowKeySet().stream().forEach(key -> {
            Map<RedisProperties, RedisConfiguration> dataMap = table.row(key);
            dataMap.forEach((properties, redisConfiguration) -> {
                //设置ClientResources客户端资源对象
                redisDbConnectionFactory().setClientResources(clientResources);
                //设置RedisProperties属性对象
                redisDbConnectionFactory().setProperties(properties);
                //创建链接工厂类
                RedisConnectionFactory redisConnectionFactory = redisDbConnectionFactory().getRedisConnectionFactory(builderCustomizers, redisConfiguration);
                // 获取StringRedisTemplate对象
                StringRedisTemplate stringRedisTemplate = createStringRedisTemplate(redisConnectionFactory);
                // 将StringRedisTemplate对象注入IOC容器bean
                defaultListableBeanFactory.registerSingleton(RedisDbFactory.getStringRedisTemplateBeanName(key), stringRedisTemplate);
                // 获取RedisTemplate对象
                RedisTemplate redisTemplate = createRedisTemplate(redisConnectionFactory);
                // 将RedisTemplate对象注入IOC容器
                defaultListableBeanFactory.registerSingleton(RedisDbFactory.getRedisTemplateBeanName(key), redisTemplate);
            });
        });
        return "UNSET";
    }

    /**
     * 创建 StringRedisTemplate对象
     *
     * @param redisConnectionFactory 链接工厂对象
     * @return
     */
    protected StringRedisTemplate createStringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {

        Assert.notNull(redisConnectionFactory, "RedisConnectionFactory must not be null");

        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(redisConnectionFactory);
        stringRedisTemplate.setKeySerializer(stringSerializer());
        stringRedisTemplate.setValueSerializer(stringSerializer());
        stringRedisTemplate.setHashKeySerializer(stringSerializer());
        stringRedisTemplate.setHashValueSerializer(stringSerializer());
        // bean初始化完成后调用方法，对于StringRedisTemplate可忽略，主要检查key-value序列化对象是否初始化，并标注RedisTemplate已经被初始化
        stringRedisTemplate.afterPropertiesSet();
        return stringRedisTemplate;
    }

    /**
     * 创建 RedisTemplate对象
     *
     * @param redisConnectionFactory 链接工厂对象
     * @return
     */
    protected RedisTemplate createRedisTemplate(RedisConnectionFactory redisConnectionFactory) {

        Assert.notNull(redisConnectionFactory, "RedisConnectionFactory must not be null");

        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(stringSerializer());
        redisTemplate.setValueSerializer(jacksonSerializer());
        redisTemplate.setHashKeySerializer(stringSerializer());
        redisTemplate.setHashValueSerializer(jacksonSerializer());
        // bean初始化完成后调用方法，主要检查key-value序列化对象是否初始化，并标注RedisTemplate已经被初始化，否则会报：
        // "template not initialized; call afterPropertiesSet() before using it" 异常
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * 创建Redis数据源配置key-value映射
     *
     * @param redisDbProperties 配置
     * @return
     */
    protected Table<String, RedisProperties, RedisConfiguration> createConfiguration(RedisDbProperties redisDbProperties) {

        Assert.notNull(redisDbProperties, "RedisDbProperties must not be null");

        Table<String, RedisProperties, RedisConfiguration> table = HashBasedTable.create();
        Map<String, RedisProperties> redisPropertiesMap = redisDbProperties.getConfig();
        redisPropertiesMap.forEach((key, properties) -> {
            RedisDbConnectionConfiguration redisDbConnectionConfiguration = new RedisDbConnectionConfiguration(properties);
            RedisConfiguration redisConfiguration = redisDbConnectionConfiguration.createRedisConfiguration();
            table.put(key, properties, redisConfiguration);
        });
        return table;
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
        // objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        // 第一个参数用于验证要反序列化的实际子类型是否对验证器使用的任何条件有效，在反序列化时必须设置，否则报异常
        // 第二个参数设置序列化的类型必须为非final类型，只有少数的类型（String、Boolean、Integer、Double）可以从JSON中正确推断
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        // 解决jackson2无法反序列化LocalDateTime的问题
        objectMapper.registerModule(new JavaTimeModule());

        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        return jackson2JsonRedisSerializer;
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
