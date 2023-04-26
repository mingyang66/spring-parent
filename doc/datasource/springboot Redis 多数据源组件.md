##### springboot Redis 多数据源组件

>
由于系统需要同一个项目需要配置多个redis数据源，并且很多项目都是同样的需求；在网上查了一波，大家的做法都是在每个需要配置多个数据源的地方添加多个配置类，需要引入几个数据源就需要写几个配置类，如果有N个项目就需要在N个项目中重复N遍相同的代码；所以就想到了自己动手对springboot提供的redis模块进行封装，使其可以无需任何多余的代码编写只需引入提前写好的组件就可以完成任意多个redis数据源的配置。

##### 组件支持哪些功能

1. 基于springboot自动化配置，实现开箱即用
2. 完全基于springboot redis对其进行二次封装
3. 基于lettuce实现连接池，不支持jedis连接池
4. 支持单节点、哨兵模式、集群模式（支持拓扑刷新能力）

先展示下属性配置方法(根据不同的标识区分不同的数据源)：

```
spring.emily.redis.config.default.client-type=lettuce
spring.emily.redis.config.default.database=15
spring.emily.redis.config.default.password=123
spring.emily.redis.config.default.sentinel.master=123
spring.emily.redis.config.default.sentinel.nodes=xx.xx.xx.xx:xx,xx.xx.xx.xx:xx,xx.xx.xx.xx:xx
# 读取超时时间
spring.emily.redis.config.default.timeout=3000
# 连接超时时间
spring.emily.redis.config.default.connect-timeout=PT3S
spring.emily.redis.config.default.lettuce.pool.max-active=8
spring.emily.redis.config.default.lettuce.pool.max-idle=8
spring.emily.redis.config.default.lettuce.pool.min-idle=0
spring.emily.redis.config.default.lettuce.pool.max-wait=-1

spring.emily.redis.config.test.client-type=lettuce
spring.emily.redis.config.test.database=0
spring.emily.redis.config.test.password=123
spring.emily.redis.config.test.sentinel.master=123
spring.emily.redis.config.test.sentinel.nodes=xx.xx.xx.xx:xx,xx.xx.xx.xx:xx,xx.xx.xx.xx:xx
spring.emily.redis.config.test.timeout=300
spring.emily.redis.config.test.lettuce.pool.max-active=8
spring.emily.redis.config.test.lettuce.pool.max-idle=8
spring.emily.redis.config.test.lettuce.pool.min-idle=0
spring.emily.redis.config.test.lettuce.pool.max-wait=-1
```

##### 一、连接工厂配置类RedisDbConnectionConfiguration

```java
public class RedisDbConnectionConfiguration {

    private RedisProperties properties;

    public RedisDbConnectionConfiguration(RedisProperties properties) {
        this.properties = properties;
    }

    /**
     * 获取Redis配置
     *
     * @return
     */
    public RedisConfiguration createRedisConfiguration() {

        if (getSentinelConfig() != null) {
            return getSentinelConfig();
        }
        if (getClusterConfiguration() != null) {
            return getClusterConfiguration();
        }
        return getStandaloneConfig();
    }

    /**
     * 创建单机配置
     */
    protected final RedisStandaloneConfiguration getStandaloneConfig() {

        Assert.notNull(properties, "RedisProperties must not be null");

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        if (StringUtils.hasText(this.properties.getUrl())) {
            ConnectionInfo connectionInfo = ConnectionInfo.parseUrl(properties.getUrl());
            config.setHostName(connectionInfo.getHostName());
            config.setPort(connectionInfo.getPort());
            config.setUsername(connectionInfo.getUsername());
            config.setPassword(RedisPassword.of(connectionInfo.getPassword()));
        } else {
            config.setHostName(properties.getHost());
            config.setPort(properties.getPort());
            config.setUsername(properties.getUsername());
            config.setPassword(RedisPassword.of(properties.getPassword()));
        }
        config.setDatabase(properties.getDatabase());
        return config;
    }

    /**
     * 创建哨兵配置RedisSentinelConfiguration
     */
    private final RedisSentinelConfiguration getSentinelConfig() {

        Assert.notNull(properties, "RedisProperties must not be null");

        RedisProperties.Sentinel sentinelProperties = properties.getSentinel();
        if (sentinelProperties != null) {
            RedisSentinelConfiguration config = new RedisSentinelConfiguration();
            config.master(sentinelProperties.getMaster());
            config.setSentinels(createSentinels(sentinelProperties));
            config.setUsername(properties.getUsername());
            if (properties.getPassword() != null) {
                config.setPassword(RedisPassword.of(properties.getPassword()));
            }
            if (sentinelProperties.getPassword() != null) {
                config.setSentinelPassword(RedisPassword.of(sentinelProperties.getPassword()));
            }
            config.setDatabase(properties.getDatabase());
            return config;
        }
        return null;
    }

    /**
     * 创建RedisClusterConfiguration集群配置
     */
    private final RedisClusterConfiguration getClusterConfiguration() {

        Assert.notNull(properties, "RedisProperties must not be null");

        if (properties.getCluster() == null) {
            return null;
        }
        RedisProperties.Cluster clusterProperties = properties.getCluster();
        RedisClusterConfiguration config = new RedisClusterConfiguration(clusterProperties.getNodes());
        if (clusterProperties.getMaxRedirects() != null) {
            config.setMaxRedirects(clusterProperties.getMaxRedirects());
        }
        config.setUsername(properties.getUsername());
        if (properties.getPassword() != null) {
            config.setPassword(RedisPassword.of(properties.getPassword()));
        }
        return config;
    }

    /**
     * 哨兵节点配置转换
     *
     * @param sentinel 哨兵配置对象
     * @return
     */
    private List<RedisNode> createSentinels(RedisProperties.Sentinel sentinel) {
        List<RedisNode> nodes = new ArrayList<>();
        for (String node : sentinel.getNodes()) {
            try {
                String[] parts = StringUtils.split(node, ":");
                Assert.state(parts.length == 2, "Must be defined as 'host:port'");
                nodes.add(new RedisNode(parts[0], Integer.parseInt(parts[1])));
            } catch (RuntimeException ex) {
                throw new IllegalStateException("Invalid redis sentinel property '" + node + "'", ex);
            }
        }
        return nodes;
    }
}
```

##### 二、连接工厂类RedisDbConnectionFactory

```java
public class RedisDbConnectionFactory {

    private RedisProperties properties;
    private ClientResources clientResources;

    public RedisDbConnectionFactory(ClientResources clientResources, RedisProperties properties) {
        this.clientResources = clientResources;
        this.properties = properties;
    }

    /**
     * 创建连接工厂类
     *
     * @param redisConfiguration 连接配置
     * @return
     */
    public RedisConnectionFactory getRedisConnectionFactory(ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers,
                                                            RedisConfiguration redisConfiguration) {

        Assert.notNull(clientResources, "ClientResources must not be null");
        Assert.notNull(clientResources, "RedisDbProperties must not be null");

        //获取链接池配置
        RedisProperties.Pool pool = getProperties().getLettuce().getPool();
        LettuceClientConfiguration lettuceClientConfiguration = getLettuceClientConfiguration(builderCustomizers, pool);
        return createLettuceConnectionFactory(lettuceClientConfiguration, redisConfiguration);
    }

    private LettuceClientConfiguration getLettuceClientConfiguration(
            ObjectProvider<LettuceClientConfigurationBuilderCustomizer> builderCustomizers, RedisProperties.Pool pool) {
        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = createBuilder(pool);
        applyProperties(builder);
        if (StringUtils.hasText(getProperties().getUrl())) {
            customizeConfigurationFromUrl(builder);
        }
        builder.clientOptions(createClientOptions());
        builder.clientResources(getClientResources());
        builderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
        return builder.build();
    }

    private LettuceConnectionFactory createLettuceConnectionFactory(LettuceClientConfiguration clientConfig, RedisConfiguration redisConfiguration) {
        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisConfiguration, clientConfig);
        // 创建Redis连接
        factory.afterPropertiesSet();
        // 将RedisConnectionFactory丢入线程池做监控
        ThreadPoolHelper.threadPoolTaskExecutor().execute(new RedisDbRunnable(factory));
        return factory;
    }

    private LettuceClientConfiguration.LettuceClientConfigurationBuilder createBuilder(RedisProperties.Pool pool) {
        if (pool == null) {
            return LettuceClientConfiguration.builder();
        }
        return new RedisPoolBuilderFactory().createBuilder(pool);
    }

    private LettuceClientConfiguration.LettuceClientConfigurationBuilder applyProperties(
            LettuceClientConfiguration.LettuceClientConfigurationBuilder builder) {
        if (getProperties().isSsl()) {
            builder.useSsl();
        }
        // Redis客户端读取超时时间
        if (getProperties().getTimeout() != null) {
            builder.commandTimeout(getProperties().getTimeout());
        }
        // 关闭连接池超时时间
        if (getProperties().getLettuce() != null) {
            RedisProperties.Lettuce lettuce = getProperties().getLettuce();
            if (lettuce.getShutdownTimeout() != null && !lettuce.getShutdownTimeout().isZero()) {
                builder.shutdownTimeout(getProperties().getLettuce().getShutdownTimeout());
            }
        }
        if (StringUtils.hasText(getProperties().getClientName())) {
            builder.clientName(getProperties().getClientName());
        }
        return builder;
    }

    private void customizeConfigurationFromUrl(LettuceClientConfiguration.LettuceClientConfigurationBuilder builder) {
        ConnectionInfo connectionInfo = ConnectionInfo.parseUrl(getProperties().getUrl());
        if (connectionInfo.isUseSsl()) {
            builder.useSsl();
        }
    }

    /**
     * ClientOptions 用于控制客户端行为的客户端选项
     *
     * @return
     */
    private ClientOptions createClientOptions() {
        ClientOptions.Builder builder = initializeClientOptionsBuilder();
        Duration connectTimeout = getProperties().getConnectTimeout();
        if (connectTimeout != null) {
            builder.socketOptions(SocketOptions.builder().connectTimeout(connectTimeout).build());
        }
        return builder.timeoutOptions(TimeoutOptions.enabled()).build();
    }

    /**
     * 拓扑刷新
     * 开启 自适应集群拓扑刷新和周期拓扑刷新
     * https://github.com/lettuce-io/lettuce-core/wiki/Redis-Cluster#user-content-refreshing-the-cluster-topology-view
     *
     * @return
     */
    private ClientOptions.Builder initializeClientOptionsBuilder() {
        if (getProperties().getCluster() != null) {
            ClusterClientOptions.Builder builder = ClusterClientOptions.builder();
            RedisProperties.Lettuce.Cluster.Refresh refreshProperties = getProperties().getLettuce().getCluster().getRefresh();
            ClusterTopologyRefreshOptions.Builder refreshBuilder = ClusterTopologyRefreshOptions.builder()
                    .dynamicRefreshSources(refreshProperties.isDynamicRefreshSources());
            if (refreshProperties.getPeriod() != null) {
                /**
                 * 开启周期刷新
                 */
                refreshBuilder.enablePeriodicRefresh(refreshProperties.getPeriod());
            }

            if (refreshProperties.isAdaptive()) {
                /**
                 * 开启自适应刷新,自适应刷新不开启,Redis集群变更时将会导致连接异常
                 */
                refreshBuilder.enableAllAdaptiveRefreshTriggers();
            }

            return builder.topologyRefreshOptions(refreshBuilder.build());
        } else {
            return ClientOptions.builder();
        }
    }

    public RedisProperties getProperties() {
        return properties;
    }

    public void setProperties(RedisProperties properties) {
        this.properties = properties;
    }

    public ClientResources getClientResources() {
        return clientResources;
    }

    public void setClientResources(ClientResources clientResources) {
        this.clientResources = clientResources;
    }
}

```

##### 三、对redis进行连接池支持的配置类

```java
public class RedisPoolBuilderFactory {
    LettuceClientConfiguration.LettuceClientConfigurationBuilder createBuilder(RedisProperties.Pool properties) {
        return LettucePoolingClientConfiguration.builder().poolConfig(getPoolConfig(properties));
    }

    private GenericObjectPoolConfig<?> getPoolConfig(RedisProperties.Pool properties) {
        GenericObjectPoolConfig<?> config = new GenericObjectPoolConfig<>();
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
}
```

##### 四、自动化配置类三件套中的配置类

```java
@ConfigurationProperties(prefix = RedisDbProperties.PREFIX)
public class RedisDbProperties {
    /**
     * 属性配置前缀
     */
    public static final String PREFIX = "spring.emily.redis";
    /**
     * 默认配置
     */
    public static final String DEFAULT_CONFIG = "default";
    /**
     * 是否开启数据源组件, 默认：true
     */
    private boolean enabled = true;
    /**
     * 是否开启客户端监控
     */
    private boolean monitorEnabled = false;
    /**
     * 监控Redis数据库固定间隔时间，默认：30s
     */
    private Duration monitorFireRate = Duration.ofSeconds(30);
    /**
     * 默认配置
     */
    private String defaultConfig = DEFAULT_CONFIG;
    /**
     * 多数据源配置
     */
    private Map<String, RedisProperties> config = new HashMap<>();

    public Map<String, RedisProperties> getConfig() {
        return config;
    }

    public String getDefaultConfig() {
        return defaultConfig;
    }

    public void setDefaultConfig(String defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    public void setConfig(Map<String, RedisProperties> config) {
        this.config = config;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public RedisProperties getDefaultDataSource() {
        return this.config.get(this.getDefaultConfig());
    }

    public Duration getMonitorFireRate() {
        return monitorFireRate;
    }

    public void setMonitorFireRate(Duration monitorFireRate) {
        this.monitorFireRate = monitorFireRate;
    }

    public boolean isMonitorEnabled() {
        return monitorEnabled;
    }

    public void setMonitorEnabled(boolean monitorEnabled) {
        this.monitorEnabled = monitorEnabled;
    }
}

```

##### 五、自动化配置类三件套中的配置类

```java
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
                //Redis连接工厂类
                RedisDbConnectionFactory redisDbConnectionFactory = new RedisDbConnectionFactory(clientResources, properties);
                //创建链接工厂类
                RedisConnectionFactory redisConnectionFactory = redisDbConnectionFactory.getRedisConnectionFactory(builderCustomizers, redisConfiguration);
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
}
```

##### 六、自动化配置类中的spring.factories属性配置请参考源码

##### 七、对外提供服务的工厂方法RedisDbFactory

```java
public class RedisDbFactory {
    /**
     * 字符串前缀
     */
    private static final String PREFIX_STRING = "ST";
    /**
     * RestTemplate对象前缀
     */
    private static final String PREFIX_REST = "RT";
    /**
     * StringRedisTemplate对象缓存
     */
    private static final Map<String, StringRedisTemplate> stringCache = new ConcurrentHashMap<>();
    /**
     * RedisTemplate对象缓存
     */
    private static final Map<String, RedisTemplate> restCache = new ConcurrentHashMap<>();

    /**
     * 获取Redis默认字符串模板
     *
     * @return
     */
    public static StringRedisTemplate getStringRedisTemplate() {
        return getStringRedisTemplate(RedisDbProperties.DEFAULT_CONFIG);
    }

    /**
     * 获取Redis模板对戏
     *
     * @param redisMark 数据源标识
     * @return
     */
    public static StringRedisTemplate getStringRedisTemplate(String redisMark) {
        /**
         * 获取缓存key
         */
        String key = getStringRedisTemplateBeanName(redisMark);
        if (stringCache.containsKey(key)) {
            return stringCache.get(key);
        }
        if (!IOCContext.containsBean(key)) {
            throw new BasicException(AppHttpStatus.DATABASE_EXCEPTION.getStatus(), "Redis数据库标识对应的数据库不存在");
        }
        StringRedisTemplate stringRedisTemplate = IOCContext.getBean(key, StringRedisTemplate.class);
        stringCache.put(key, stringRedisTemplate);
        return stringRedisTemplate;
    }

    /**
     * 获取Redis默认字符串模板
     *
     * @return
     */
    public static RedisTemplate getRedisTemplate() {
        return getRedisTemplate(RedisDbProperties.DEFAULT_CONFIG);
    }

    /**
     * 获取Redis模板对戏
     *
     * @param redisMark 数据源标识
     * @return
     */
    public static RedisTemplate getRedisTemplate(String redisMark) {
        /**
         * 获取缓存key
         */
        String key = getRedisTemplateBeanName(redisMark);
        if (restCache.containsKey(key)) {
            return restCache.get(key);
        }
        if (!IOCContext.containsBean(key)) {
            throw new BasicException(AppHttpStatus.DATABASE_EXCEPTION.getStatus(), "Redis数据库标识对应的数据库不存在");
        }
        RedisTemplate redisTemplate = IOCContext.getBean(key, RedisTemplate.class);
        restCache.put(key, redisTemplate);
        return redisTemplate;
    }

    /**
     * 获取StringRedisTemplate对象bean名称
     *
     * @param redisMark
     * @return
     */
    public static String getStringRedisTemplateBeanName(String redisMark) {
        if (Objects.isNull(redisMark)) {
            throw new BasicException(AppHttpStatus.ILLEGAL_ARGUMENT_EXCEPTION.getStatus(), "Redis数据库标识不可为空");
        }
        return MessageFormat.format("{0}_{1}", PREFIX_STRING, redisMark);
    }

    /**
     * 获取RedisTemplate对象bean名称
     *
     * @param redisMark
     * @return
     */
    public static String getRedisTemplateBeanName(String redisMark) {
        if (Objects.isNull(redisMark)) {
            throw new BasicException(AppHttpStatus.ILLEGAL_ARGUMENT_EXCEPTION.getStatus(), "Redis数据库标识不可为空");
        }
        return MessageFormat.format("{0}_{1}", PREFIX_REST, redisMark);
    }
}

```

##### 八、RedisTemplate、StringRedisTemplate的使用方法

```
    @GetMapping("get1")
    public String get1() {

        RedisDbFactory.getStringRedisTemplate().opsForValue().set("test", "123", 12, TimeUnit.MINUTES);
        Map<String, Object> dataMap = Maps.newHashMap();
        dataMap.put("te", 12);
        dataMap.put("te2", 12);
        dataMap.put("te3", "哈哈");
        RedisDbFactory.getRedisTemplate().opsForValue().set("test1", dataMap, 1, TimeUnit.MINUTES);
        RedisDbFactory.getRedisTemplate("one").opsForValue().set("one", "adf", 1, TimeUnit.MINUTES);
        return RedisDbFactory.getStringRedisTemplate("default").opsForValue().get("test");
    }
```

> 解说：文章中有部分辅助代码未展示完整，详情请参考源码

GitHub源码：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

