### springboot通过AsyncConfigurer接口实现异步线程池自动化配置组件

> springboot提供了org.springframework.scheduling.annotation.AsyncConfigurer接口让开发人员可以自定义线程池执行器；框架默认提供了一个空的实现类AsyncConfigurerSupport，不过两个方法体内部提供的都是空实现；

##### 1.首先看下AsyncConfigurer接口

```java
public interface AsyncConfigurer {

	/**
	 * 方法返回一个实际执行线程的线程池
	 */
	@Nullable
	default Executor getAsyncExecutor() {
		return null;
	}

	/**
	 * 当线程池执行异步任务时会抛出AsyncUncaughtExceptionHandler异常，
	 * 此方法会捕获该异常
	 */
	@Nullable
	default AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return null;
	}

}
```

##### 2.通过上面的接口实现异步线程池

先看下我们的目标：

- 自定义一个异步线程池；
- 自动铺货异步线程实例抛出的异常；
- 线程池的核心指标可以通过配置文件自定义配置；
- 封装成为一个组件，可以通过配置控制容器是否加载；

自动化配置类如下：

```java
package com.sgrain.boot.autoconfigure.threadpool;

import com.sgrain.boot.common.utils.LoggerUtils;
import com.sgrain.boot.common.utils.constant.CharacterUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @program: spring-parent
 * @description: 异步线程池配置
 * @create: 2020/08/21
 */
@EnableAsync
@Configuration
@EnableConfigurationProperties(AsyncThreadPoolProperties.class)
@ConditionalOnProperty(prefix = "spring.sgrain.async-thread-pool", name = "enable", havingValue = "true", matchIfMissing = false)
public class AsyncThreadPoolAutoConfiguration implements AsyncConfigurer {

    @Autowired
    private AsyncThreadPoolProperties asyncThreadPoolProperties;
    /**
     * 定义线程池
     *
     * @return
     */
    @Override
    public Executor getAsyncExecutor() {
        //Java虚拟机可用的处理器数
        int processors = Runtime.getRuntime().availableProcessors();
        //定义线程池
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //核心线程数
        taskExecutor.setCorePoolSize(Objects.nonNull(asyncThreadPoolProperties.getCorePoolSize()) ? asyncThreadPoolProperties.getCorePoolSize() : processors);
        //线程池最大线程数
        taskExecutor.setMaxPoolSize(Objects.nonNull(asyncThreadPoolProperties.getMaxPoolSize()) ? asyncThreadPoolProperties.getMaxPoolSize() : processors*100);
        //线程队列最大线程数
        taskExecutor.setQueueCapacity(Objects.nonNull(asyncThreadPoolProperties.getMaxPoolSize()) ? asyncThreadPoolProperties.getMaxPoolSize() : processors*1000);
        //线程名称前缀
        taskExecutor.setThreadNamePrefix(StringUtils.isNotEmpty(asyncThreadPoolProperties.getThreadNamePrefix()) ? asyncThreadPoolProperties.getThreadNamePrefix() : "Async-ThreadPool-");
        //线程池中线程最大空闲时间，默认：60，单位：秒
        taskExecutor.setKeepAliveSeconds(asyncThreadPoolProperties.getKeepAliveSeconds());
        /**
         * 拒绝策略，默认是AbortPolicy
         * AbortPolicy：丢弃任务并抛出RejectedExecutionException异常
         * DiscardPolicy：丢弃任务但不抛出异常
         * DiscardOldestPolicy：丢弃最旧的处理程序，然后重试，如果执行器关闭，这时丢弃任务
         * CallerRunsPolicy：执行器执行任务失败，则在策略回调方法中执行任务，如果执行器关闭，这时丢弃任务
         */
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        //初始化
        taskExecutor.initialize();

        return taskExecutor;
    }

    /**
     * 异步方法执行的过程中抛出的异常捕获
     * @return
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, objects) -> {
            LoggerUtils.error(method.getDeclaringClass(), StringUtils.join( method.getDeclaringClass().getName(), CharacterUtils.POINT_SYMBOL, method.getName(), "()发生异常：", throwable));
        };
    }
}

```

> 线程池的自定义属性及异常处理代码中的注解已经标的很清晰了，不在重复说明；@Configuration注解描述当前类是一个配置类，@EnableAsync注解描述启动线程池执行器开启异步执行功能；@EnableConfigurationProperties注解描述启动对@ConfigurationProperties注解标注的bean的支持，该bean可以被注入到IOC容器之中；@ConditionalOnProperty是一个条件注解，用来控制容器是否将当前的配置类注入到IOC容器之中；

线程池使用到的外部属性配置类如下：

```java
package com.sgrain.boot.autoconfigure.threadpool;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @program: spring-parent
 * @description: 异步线程池配置文件
 * @create: 2020/08/21
 */
@ConfigurationProperties(prefix = "spring.sgrain.async-thread-pool")
public class AsyncThreadPoolProperties {
    /**
     * 是否启动异步线程池，默认 false
     */
    private boolean enable;
    /**
     * 核心线程数,默认：Java虚拟机可用线程数
     */
    private Integer corePoolSize;
    /**
     * 线程池最大线程数,默认：Java虚拟机可用线程数*100
     */
    private Integer maxPoolSize;
    /**
     * 线程队列最大线程数,默认：Java虚拟机可用线程数*1000
     */
    private Integer queueCapacity;
    /**
     * 自定义线程名前缀，默认：Async-ThreadPool-
     */
    private String threadNamePrefix;
    /**
     * 线程池中线程最大空闲时间，默认：60，单位：秒
     */
    private Integer keepAliveSeconds = 60;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public Integer getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(Integer corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public Integer getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(Integer queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public String getThreadNamePrefix() {
        return threadNamePrefix;
    }

    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    public Integer getKeepAliveSeconds() {
        return keepAliveSeconds;
    }

    public void setKeepAliveSeconds(Integer keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }
}

```

properties属性配置示例如下：

```java
#异步线程池
#异步线程池组件开关，默认false
spring.sgrain.async-thread-pool.enable=true
#核心线程数,默认：Java虚拟机可用线程数
spring.sgrain.async-thread-pool.core-pool-size=4
#线程池最大线程数,默认：Java虚拟机可用线程数*100
spring.sgrain.async-thread-pool.max-pool-size=400
#线程队列最大线程数,默认：Java虚拟机可用线程数*1000
spring.sgrain.async-thread-pool.queue-capacity=4000
#自定义线程名前缀，默认：Async-ThreadPool-
spring.sgrain.async-thread-pool.thread-name-prefix=Async-ThreadPool-
#线程池中线程最大空闲时间，默认：60，单位：秒
spring.sgrain.async-thread-pool.keep-alive-seconds=60
```

上面的开发任务都做好之后还需要最后一步，将com.sgrain.boot.autoconfigure.threadpool.AsyncThreadPoolAutoConfiguration配置到resources/META-INF目录的spring.factories文件中：

```java
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  com.sgrain.boot.autoconfigure.threadpool.AsyncThreadPoolAutoConfiguration
```

看到配置的属性key是EnableAutoConfiguration大概你就可以猜测到是为了启用自动化配置功能；到现在整个异步线程池自动化配置组件已经开发完成了，那如何使用呢？我就不再举例说明了，网上有很多示例，只说几个重点；

- 使用@Async注解标注方法为异步任务
- 异步任务返回值为void
- 异步任务方法必须使用@Override标注，即是一个接口的实现方法

GitHub源码：[https://github.com/mingyang66/spring-parent/tree/master/sgrain-spring-boot-autoconfigure](https://github.com/mingyang66/spring-parent/tree/master/sgrain-spring-boot-autoconfigure)