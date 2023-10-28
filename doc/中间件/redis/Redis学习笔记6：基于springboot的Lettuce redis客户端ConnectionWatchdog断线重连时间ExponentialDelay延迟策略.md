#### Redis学习笔记6：基于springboot的Lettuce redis客户端ConnectionWatchdog断线重连时间ExponentialDelay延迟策略

> lettuce断线重连监视器ConnectionWatchdog#scheduleReconnect方法中的延迟重连时间是根据io.lettuce.core.resource.Delay策略来决定，默认是采用ExponentialDelay延迟策略，即2的幂次方，即时间是PT0.001S、PT0.002S、PT0.004S一直到最大PT30S。

一个对springboot redis框架进行重写，支持lettuce、jedis、连接池、同时连接多个集群、多个redis数据库、开发自定义属性配置的开源SDK

```xml
<dependency>
    <groupId>io.github.mingyang66</groupId>
    <artifactId>emily-spring-boot-redis</artifactId>
    <version>4.3.9</version>
</dependency>
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

##### 一、ConnectionWatchdog监视器属性

```java
//初始化在控制器
private final Delay reconnectDelay;
```

##### 二、ConnectionBuilder#createConnectionWatchdog创建监视器对象时通过ClientResources对象获取延迟策略

```java
    protected ConnectionWatchdog createConnectionWatchdog() {

        if (connectionWatchdog != null) {
            return connectionWatchdog;
        }

        LettuceAssert.assertState(bootstrap != null, "Bootstrap must be set for autoReconnect=true");
        LettuceAssert.assertState(socketAddressSupplier != null, "SocketAddressSupplier must be set for autoReconnect=true");
				//通过ClientResources对象的复制延迟策略
        ConnectionWatchdog watchdog = new ConnectionWatchdog(clientResources.reconnectDelay(), clientOptions, bootstrap,
                clientResources.timer(), clientResources.eventExecutorGroup(), socketAddressSupplier, reconnectionListener,
                connection, clientResources.eventBus(), endpoint);

        endpoint.registerConnectionWatchdog(watchdog);

        connectionWatchdog = watchdog;
        return watchdog;
    }
```

##### 三、LettuceConnectionConfiguration#lettuceClientResources自动化配置自动赋值默认延迟策略

```java
	@Bean(destroyMethod = "shutdown")
	@ConditionalOnMissingBean(ClientResources.class)
	DefaultClientResources lettuceClientResources(ObjectProvider<ClientResourcesBuilderCustomizer> customizers) {
		DefaultClientResources.Builder builder = DefaultClientResources.builder();
		customizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
		return builder.build();
	}
```

查看DefaultClientResources默认实现类延迟策略，实际采用的就是指数延迟策略

```java
    public static final Supplier<Delay> DEFAULT_RECONNECT_DELAY = Delay::exponential;
    public static Delay exponential() {
        return exponential(DEFAULT_LOWER_BOUND, DEFAULT_UPPER_BOUND, DEFAULT_POWER_OF, DEFAULT_TIMEUNIT);
    }
    public static Delay exponential(Duration lower, Duration upper, int powersOf, TimeUnit targetTimeUnit) {

        LettuceAssert.notNull(lower, "Lower boundary must not be null");
        LettuceAssert.isTrue(lower.toNanos() >= 0, "Lower boundary must be greater or equal to 0");
        LettuceAssert.notNull(upper, "Upper boundary must not be null");
        LettuceAssert.isTrue(upper.toNanos() > lower.toNanos(), "Upper boundary must be greater than the lower boundary");
        LettuceAssert.isTrue(powersOf > 1, "PowersOf must be greater than 1");
        LettuceAssert.notNull(targetTimeUnit, "Target TimeUnit must not be null");

        return new ExponentialDelay(lower, upper, powersOf, targetTimeUnit);
    }
```

上述常量默认值如下：

```java
public abstract class Delay {
		//最小时间间隔，实际是PT0.001S
    private static Duration DEFAULT_LOWER_BOUND = Duration.ZERO;
		//最大时间间隔
    private static Duration DEFAULT_UPPER_BOUND = Duration.ofSeconds(30);
		//底数
    private static int DEFAULT_POWER_OF = 2;
		//默认时间单位
    private static TimeUnit DEFAULT_TIMEUNIT = TimeUnit.MILLISECONDS;
    }
```

> 通过上述代码可以知道，采用的是底数为2的n次方的延迟策略，最小值0、最大值是30

##### 三、ConnectionWatchdog#scheduleReconnect

```java
public void scheduleReconnect() {
            attempts++;
            final int attempt = attempts;
  					//第一次获取延迟时间attempt为1，获取到的值是PT0.001S
            Duration delay = reconnectDelay.createDelay(attempt);
            int timeout = (int) delay.toMillis();
            logger.debug("{} Reconnect attempt {}, delay {}ms", logPrefix(), attempt, timeout);
						...
    }
```

如果一直重试，获取到的重试延迟时间是如下：

```sh
第1次：PT0.001S
第2次：PT0.002S
第3次：PT0.004S
第4次：PT0.008S
第5次：PT0.016S
第6次：PT0.032S
第7次：PT0.064S
第8次：PT0.128S
第9次：PT0.256S
第10次：PT0.512S
第11次：PT1.024S
第12次：PT2.048S
第13次：PT4.096S
第14次：PT8.192S
第15次：PT16.384S
第16次：PT30S
第17次：PT30S
第18次：PT30S
第19次：PT30S
```

##### 四、ExponentialDelay#createDelay获取延迟时间

```java
    @Override
    public Duration createDelay(long attempt) {

        long delay;
        if (attempt <= 0) { // safeguard against underflow
            delay = 0;
        } else if (powersOf == 2) {
            //默认底数为2，执行如下方法
            delay = calculatePowerOfTwo(attempt);
        } else {
           //指数为其它，执行如下方法
            delay = calculateAlternatePower(attempt);
        }
				//将获取到的延迟时间判定是否在最大和最小时间区间内，即0s<deplay<30s
        return applyBounds(Duration.ofNanos(targetTimeUnit.toNanos(delay)));
    }
```

底数为2的计算公式如下：

```java
    protected static long calculatePowerOfTwo(long attempt) {

        if (attempt <= 0) { // safeguard against underflow
            return 0L;
        } else if (attempt >= 63) { // safeguard against overflow in the bitshift operation
            return Long.MAX_VALUE - 1;
        } else {
            return 1L << (attempt - 1);
        }
    }
```

底数非2的计算如下：

```java
    private long calculateAlternatePower(long attempt) {

        // round will cap at Long.MAX_VALUE and pow should prevent overflows
        double step = Math.pow(powersOf, attempt - 1); // attempt > 0
        return Math.round(step);
    }
```

将计算结果界定在最大和最小值之间方法如下：

```java
    protected static Duration applyBounds(Duration calculatedValue, Duration lower, Duration upper) {

        if (calculatedValue.compareTo(lower) < 0) {
            return lower;
        }

        if (calculatedValue.compareTo(upper) > 0) {
            return upper;
        }

        return calculatedValue;
    }
```

