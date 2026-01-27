package com.emily.infrastructure.logback.common;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存管理器
 *
 * @author Emily
 * @since :  Created in 2023/7/2 5:31 PM
 */
public class CommonCache {
    /**
     * Appender实例对象缓存
     */
    public static final Map<String, Appender<ILoggingEvent>> APPENDER = new ConcurrentHashMap<>();
}
