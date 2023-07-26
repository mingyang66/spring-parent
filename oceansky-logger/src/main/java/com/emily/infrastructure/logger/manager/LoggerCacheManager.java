package com.emily.infrastructure.logger.manager;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存管理器
 *
 * @author :  Emily
 * @since :  Created in 2023/7/2 5:31 PM
 */
public class LoggerCacheManager {
    /**
     * Logger对象容器
     */
    public static final Map<String, Logger> LOGGER = new ConcurrentHashMap<>();
    /**
     * Appender实例对象缓存
     */
    public static final Map<String, Appender<ILoggingEvent>> APPENDER = new ConcurrentHashMap<>();
}
