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
}
