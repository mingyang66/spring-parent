package com.emily.infrastructure.redis;

import com.emily.infrastructure.logger.LoggerFactory;
import com.emily.infrastructure.redis.common.RedisInfo;
import com.emily.infrastructure.redis.config.RedisDbLettuceConnectionConfiguration;
import com.emily.infrastructure.redis.connection.JedisDbConnectionConfiguration;
import com.emily.infrastructure.redis.connection.LettuceDbConnectionConfiguration;
import com.emily.infrastructure.redis.connection.PropertiesRedisDbConnectionDetails;
import com.emily.infrastructure.redis.connection.RedisTemplateDbConfiguration;
import com.emily.infrastructure.redis.example.RedisDbEventConsumer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Role;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

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
@Import({LettuceDbConnectionConfiguration.class, JedisDbConnectionConfiguration.class, RedisTemplateDbConfiguration.class})
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

    //@Bean
    // @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate() {
        Map<String, RedisProperties> dataMap = Objects.requireNonNull(redisDbProperties.getConfig(), "Redis连接配置不存在");
        RedisTemplate<Object, Object> redisTemplate = null;
        for (Map.Entry<String, RedisProperties> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            RedisTemplate<Object, Object> template = new RedisTemplate<>();
            if (redisDbProperties.getDefaultConfig().equals(key)) {
                RedisConnectionFactory redisConnectionFactory = defaultListableBeanFactory.getBean(RedisInfo.DEFAULT_REDIS_CONNECTION_FACTORY, RedisConnectionFactory.class);
                template.setConnectionFactory(redisConnectionFactory);
                redisTemplate = template;
            } else {
                RedisConnectionFactory redisConnectionFactory = defaultListableBeanFactory.getBean(key + RedisInfo.REDIS_CONNECTION_FACTORY, RedisConnectionFactory.class);
                template.setConnectionFactory(redisConnectionFactory);
                defaultListableBeanFactory.registerSingleton(key + RedisInfo.REDIS_TEMPLATE, template);
            }
        }

        return redisTemplate;
    }

    //@Bean
    // @ConditionalOnMissingBean
    public StringRedisTemplate stringRedisTemplate() {
        Map<String, RedisProperties> dataMap = Objects.requireNonNull(redisDbProperties.getConfig(), "Redis连接配置不存在");
        StringRedisTemplate stringRedisTemplate = null;
        for (Map.Entry<String, RedisProperties> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            StringRedisTemplate template = new StringRedisTemplate();
            if (redisDbProperties.getDefaultConfig().equals(key)) {
                RedisConnectionFactory redisConnectionFactory = defaultListableBeanFactory.getBean(RedisInfo.DEFAULT_REDIS_CONNECTION_FACTORY, RedisConnectionFactory.class);
                template.setConnectionFactory(redisConnectionFactory);
                stringRedisTemplate = template;
            } else {
                RedisConnectionFactory redisConnectionFactory = defaultListableBeanFactory.getBean(key + RedisInfo.REDIS_CONNECTION_FACTORY, RedisConnectionFactory.class);
                template.setConnectionFactory(redisConnectionFactory);
                defaultListableBeanFactory.registerSingleton(key + RedisInfo.STRING_REDIS_TEMPLATE, template);
            }
        }

        return stringRedisTemplate;
    }

    // @Bean(initMethod = "init")
    public RedisDbEventConsumer redisDbEventConsumer(ClientResources clientResources) {
        return new RedisDbEventConsumer(clientResources.eventBus());
    }

    /**
     * 策略实现类，提供所有基础设施的构建，如环境变量和线程池，以便客户端能够正确使用。
     * 如果在RedisClient客户端外部创建，则可以在多个客户端实例之间共享，ClientResources的实现类是有状态的，
     * 在不使用后必须调用shutdown方法
     * https://blog.csdn.net/zhxdick/article/details/119633581
     *
     * @return
     */
    // @Bean(destroyMethod = "shutdown")
    // @ConditionalOnMissingBean(ClientResources.class)
    DefaultClientResources lettuceClientResources(ObjectProvider<ClientResourcesBuilderCustomizer> customizers) {
        DefaultClientResources.Builder builder = DefaultClientResources.builder();
        customizers.orderedStream().forEach((customizer) -> {
            customizer.customize(builder);
        });
        return builder.build();
       /* return DefaultClientResources.builder()
                .commandLatencyRecorder(
                new DefaultCommandLatencyCollector(
                        //开启 CommandLatency 事件采集，并且配置每次采集后都清空数据
                        DefaultCommandLatencyCollectorOptions.builder().enable().resetLatenciesAfterEvent(true).build()
                )
                ).commandLatencyPublisherOptions(
                        //每 10s 采集一次命令统计
                        DefaultEventPublisherOptions.builder().eventEmitInterval(Duration.ofSeconds(10)).build()
                ).build();*/
    }

    /**
     * 初始化redis相关bean
     *
     * @param clientResources                 todo
     * @param builderCustomizers              扩展点
     * @param redisDbProperties               属性配置
     * @param clusterConfigurationProvider    todo
     * @param sentinelConfigurationProvider   todo
     * @param standaloneConfigurationProvider todo
     * @return 约定字符串对象
     */
    //@Bean
    public Object initTargetRedis(ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers, ClientResources clientResources, RedisDbProperties redisDbProperties,
                                  ObjectProvider<RedisStandaloneConfiguration> standaloneConfigurationProvider, ObjectProvider<RedisSentinelConfiguration> sentinelConfigurationProvider,
                                  ObjectProvider<RedisClusterConfiguration> clusterConfigurationProvider) {

        Assert.notNull(clientResources, "ClientResources must not be null");
        Assert.notNull(clientResources, "RedisDbProperties must not be null");

        //创建Redis数据源配置key-value映射
        Map<String, RedisProperties> dataMap = redisDbProperties.getConfig();
        if (CollectionUtils.isEmpty(dataMap)) {
            throw new IllegalArgumentException("Redis数据库字典配置不存在");
        }
        dataMap.forEach((key, properties) -> {
            //Redis连接工厂类
            RedisDbLettuceConnectionConfiguration connectionConfiguration = new RedisDbLettuceConnectionConfiguration(properties, standaloneConfigurationProvider, sentinelConfigurationProvider, clusterConfigurationProvider);
            //是否开启校验共享本地连接，校验失败则重新建立连接
            connectionConfiguration.setValidateConnection(redisDbProperties.isValidateConnection());
            //是否开启共享本地物理连接，默认：true
            connectionConfiguration.setShareNativeConnection(redisDbProperties.isShareNativeConnection());
            //创建链接工厂类
            RedisConnectionFactory redisConnectionFactory = connectionConfiguration.getRedisConnectionFactory(builderCustomizers, clientResources);
            // 获取StringRedisTemplate对象
            StringRedisTemplate stringRedisTemplate = createStringRedisTemplate(redisConnectionFactory);
            // 将StringRedisTemplate对象注入IOC容器bean
            //this.getDefaultListableBeanFactory().registerSingleton(RedisDbFactory.getStringRedisTemplateBeanName(key), stringRedisTemplate);
            // 获取RedisTemplate对象
            RedisTemplate redisTemplate = createRedisTemplate(redisConnectionFactory);
            // 将RedisTemplate对象注入IOC容器
            // this.getDefaultListableBeanFactory().registerSingleton(RedisDbFactory.getRedisTemplateBeanName(key), redisTemplate);
        });
        return "UNSET";
    }

    /**
     * 创建 StringRedisTemplate对象
     *
     * @param redisConnectionFactory 链接工厂对象
     * @return StringRedisTemplate对象
     */
    protected StringRedisTemplate createStringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {

        Assert.notNull(redisConnectionFactory, "RedisConnectionFactory must not be null");

        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory);
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
     * @return redisTemplate对象
     */
    protected RedisTemplate<Object, Object> createRedisTemplate(RedisConnectionFactory redisConnectionFactory) {

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
     * 初始化string序列化对象
     *
     * @return 字符串序列化对象
     */
    protected StringRedisSerializer stringSerializer() {
        return new StringRedisSerializer();
    }

    /**
     * 初始化jackson序列化对象
     *
     * @return jackson序列化对象
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
