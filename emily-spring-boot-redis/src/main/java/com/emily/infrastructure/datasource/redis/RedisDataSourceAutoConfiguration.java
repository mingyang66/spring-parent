package com.emily.infrastructure.datasource.redis;

import com.emily.infrastructure.datasource.redis.utils.RedisDbUtils;
import com.emily.infrastructure.logback.factory.LogbackFactory;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.resource.DefaultClientResources;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;

/**
 * @program: spring-parent
 * @description: Redis多数据源配置，参考源码：LettuceConnectionConfiguration
 * @author: Emily
 * @create: 2021/07/11
 */
@Configuration
@EnableConfigurationProperties(RedisDataSourceProperties.class)
@ConditionalOnProperty(prefix = "spring.emily.redis", name = "enabled", havingValue = "true", matchIfMissing = true)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class RedisDataSourceAutoConfiguration implements InitializingBean, DisposableBean {

    private DefaultListableBeanFactory defaultListableBeanFactory;
    private RedisDataSourceProperties redisDataSourceProperties;

    public RedisDataSourceAutoConfiguration(DefaultListableBeanFactory defaultListableBeanFactory, RedisDataSourceProperties redisDataSourceProperties) {
        this.defaultListableBeanFactory = defaultListableBeanFactory;
        this.redisDataSourceProperties = redisDataSourceProperties;
    }

    @PostConstruct
    public void stringRedisTemplate() {
        Table<String, RedisProperties, RedisConfiguration> table = createConfiguration(redisDataSourceProperties);
        table.rowKeySet().stream().forEach(key -> {
            Map<RedisProperties, RedisConfiguration> dataMap = table.row(key);
            dataMap.forEach((properties, redisConfiguration) -> {
                // 获取StringRedisTemplate对象
                StringRedisTemplate stringRedisTemplate = createStringRedisTemplate(redisConfiguration, properties);
                // 将StringRedisTemplate对象注入IOC容器bean
                defaultListableBeanFactory.registerSingleton(RedisDbUtils.getStringRedisTemplateBeanName(key), stringRedisTemplate);

                // 获取RedisTemplate对象
                RedisTemplate redisTemplate = createRedisTemplate(redisConfiguration, properties);
                // 将RedisTemplate对象注入IOC容器
                defaultListableBeanFactory.registerSingleton(RedisDbUtils.getRedisTemplateBeanName(key), redisTemplate);
            });
        });
    }

    /**
     * 创建 StringRedisTemplate对象
     *
     * @param redisConfiguration 配置类
     * @return
     */
    protected StringRedisTemplate createStringRedisTemplate(RedisConfiguration redisConfiguration, RedisProperties properties) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(createLettuceConnectionFactory(redisConfiguration, properties));
        stringRedisTemplate.setKeySerializer(stringSerializer());
        stringRedisTemplate.setValueSerializer(jacksonSerializer());
        stringRedisTemplate.setHashKeySerializer(stringSerializer());
        stringRedisTemplate.setHashValueSerializer(jacksonSerializer());
        // bean初始化完成后调用方法，对于StringRedisTemplate可忽略，主要检查key-value序列化对象是否初始化，并标注RedisTemplate已经被初始化
        stringRedisTemplate.afterPropertiesSet();
        return stringRedisTemplate;
    }

    /**
     * 创建 RedisTemplate对象
     *
     * @param redisConfiguration 配置类
     * @return
     */
    protected RedisTemplate createRedisTemplate(RedisConfiguration redisConfiguration, RedisProperties properties) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(createLettuceConnectionFactory(redisConfiguration, properties));
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
     * 创建连接工厂类
     *
     * @param redisConfiguration 连接配置
     * @return
     */
    protected RedisConnectionFactory createLettuceConnectionFactory(RedisConfiguration redisConfiguration, RedisProperties properties) {
        //redis客户端配置
        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder();
        // 连接池配置
        builder.poolConfig(getPoolConfig(properties.getLettuce().getPool()));
        if (properties.isSsl()) {
            builder.useSsl();
        }
        if (StringUtils.hasText(properties.getUrl())) {
            this.customizeConfigurationFromUrl(builder, properties);
        }
        // Redis客户端读取超时时间
        if (Objects.nonNull(properties.getTimeout())) {
            builder.commandTimeout(properties.getTimeout());
        }
        // 关闭连接池超时时间
        if (properties.getLettuce() != null) {
            RedisProperties.Lettuce lettuce = properties.getLettuce();
            if (lettuce.getShutdownTimeout() != null && !lettuce.getShutdownTimeout().isZero()) {
                builder.shutdownTimeout(properties.getLettuce().getShutdownTimeout());
            }
        }
        if (StringUtils.hasText(properties.getClientName())) {
            builder.clientName(properties.getClientName());
        }
        builder.clientOptions(createClientOptions(properties));
        builder.clientResources(DefaultClientResources.create());
        LettuceClientConfiguration lettuceClientConfiguration = builder.build();

        //根据配置和客户端配置创建连接
        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisConfiguration, lettuceClientConfiguration);
        factory.afterPropertiesSet();

        return factory;
    }

    private void customizeConfigurationFromUrl(LettuceClientConfiguration.LettuceClientConfigurationBuilder builder, RedisProperties properties) {
        RedisDbUtils.ConnectionInfo connectionInfo = RedisDbUtils.parseUrl(properties.getUrl());
        if (connectionInfo.isUseSsl()) {
            builder.useSsl();
        }

    }

    private ClientOptions createClientOptions(RedisProperties properties) {
        ClientOptions.Builder builder = this.initializeClientOptionsBuilder(properties);
        Duration connectTimeout = properties.getConnectTimeout();
        if (connectTimeout != null) {
            builder.socketOptions(SocketOptions.builder().connectTimeout(connectTimeout).build());
        }

        return builder.timeoutOptions(TimeoutOptions.enabled()).build();
    }

    private ClientOptions.Builder initializeClientOptionsBuilder(RedisProperties properties) {
        if (properties.getCluster() != null) {
            io.lettuce.core.cluster.ClusterClientOptions.Builder builder = ClusterClientOptions.builder();
            RedisProperties.Lettuce.Cluster.Refresh refreshProperties = properties.getLettuce().getCluster().getRefresh();
            io.lettuce.core.cluster.ClusterTopologyRefreshOptions.Builder refreshBuilder = ClusterTopologyRefreshOptions.builder().dynamicRefreshSources(refreshProperties.isDynamicRefreshSources());
            if (refreshProperties.getPeriod() != null) {
                refreshBuilder.enablePeriodicRefresh(refreshProperties.getPeriod());
            }

            if (refreshProperties.isAdaptive()) {
                refreshBuilder.enableAllAdaptiveRefreshTriggers();
            }

            return builder.topologyRefreshOptions(refreshBuilder.build());
        } else {
            return ClientOptions.builder();
        }
    }

    /**
     * 获取连接池配置
     *
     * @param properties
     * @return
     */
    private GenericObjectPoolConfig<?> getPoolConfig(RedisProperties.Pool properties) {
        GenericObjectPoolConfig<?> config = new GenericObjectPoolConfig<>();
        if (properties == null) {
            return config;
        }
        config.setMaxTotal(properties.getMaxActive());
        config.setMaxIdle(properties.getMaxIdle());
        config.setMinIdle(properties.getMinIdle());
        if (properties.getTimeBetweenEvictionRuns() != null) {
            config.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRuns().toMillis());
        }
        if (properties.getMaxWait() != null) {
            config.setMaxWaitMillis(properties.getMaxWait().toMillis());
        }
        return config;
    }

    /**
     * 创建Redis数据源配置key-value映射
     *
     * @param redisDataSourceProperties 配置
     * @return
     */
    protected Table<String, RedisProperties, RedisConfiguration> createConfiguration(RedisDataSourceProperties redisDataSourceProperties) {
        Table<String, RedisProperties, RedisConfiguration> table = HashBasedTable.create();
        Map<String, RedisProperties> redisPropertiesMap = redisDataSourceProperties.getConfig();
        redisPropertiesMap.forEach((key, properties) -> {
            RedisConfiguration redisConfiguration = RedisDbUtils.createRedisConfiguration(properties);
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
        LogbackFactory.info(RedisDataSourceAutoConfiguration.class, "<== 【销毁--自动化配置】----Redis数据库多数据源组件【RedisDataSourceAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() {
        LogbackFactory.info(RedisDataSourceAutoConfiguration.class, "==> 【初始化--自动化配置】----Redis数据库多数据源组件【RedisDataSourceAutoConfiguration】");
    }
}
