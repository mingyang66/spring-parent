package com.emily.infrastructure.logback.factory;

import ch.qos.logback.classic.LoggerContext;
import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.configuration.classic.LogbackGroup;
import com.emily.infrastructure.logback.configuration.classic.LogbackModule;
import com.emily.infrastructure.logback.configuration.classic.LogbackRoot;
import com.emily.infrastructure.logback.configuration.encoder.LogbackPatternLayoutEncoder;
import com.emily.infrastructure.logback.configuration.filter.LogAcceptMarkerFilter;
import com.emily.infrastructure.logback.configuration.filter.LogDenyMarkerFilter;
import com.emily.infrastructure.logback.configuration.filter.LogLevelFilter;
import com.emily.infrastructure.logback.configuration.filter.LogThresholdLevelFilter;
import com.emily.infrastructure.logback.configuration.policy.LogbackFixedWindowRollingPolicy;
import com.emily.infrastructure.logback.configuration.policy.LogbackSizeAndTimeBasedRollingPolicy;
import com.emily.infrastructure.logback.configuration.policy.LogbackTimeBasedRollingPolicy;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 默认容器工厂类
 *
 * @author :  Emily
 * @since :  2024/1/1 9:47 AM
 */
public class LogBeanFactory {
    private static final Map<String, Object> beanMap = new ConcurrentHashMap<>(64);

    public static void registerBean(LoggerContext lc, LogbackProperties properties) {
        beanMap.putIfAbsent(LogbackGroup.class.getSimpleName(), new LogbackGroup(lc, properties));
        beanMap.putIfAbsent(LogbackModule.class.getSimpleName(), new LogbackModule(lc, properties));
        beanMap.putIfAbsent(LogbackRoot.class.getSimpleName(), new LogbackRoot(lc, properties));

        beanMap.putIfAbsent(LogbackSizeAndTimeBasedRollingPolicy.class.getSimpleName(), new LogbackSizeAndTimeBasedRollingPolicy(lc, properties));
        beanMap.putIfAbsent(LogbackTimeBasedRollingPolicy.class.getSimpleName(), new LogbackTimeBasedRollingPolicy(lc, properties));
        beanMap.putIfAbsent(LogbackFixedWindowRollingPolicy.class.getSimpleName(), new LogbackFixedWindowRollingPolicy(lc, properties));

        beanMap.putIfAbsent(LogbackPatternLayoutEncoder.class.getSimpleName(), new LogbackPatternLayoutEncoder(lc));

        beanMap.putIfAbsent(LogAcceptMarkerFilter.class.getSimpleName(), new LogAcceptMarkerFilter(lc));
        beanMap.putIfAbsent(LogDenyMarkerFilter.class.getSimpleName(), new LogDenyMarkerFilter(lc));
        beanMap.putIfAbsent(LogLevelFilter.class.getSimpleName(), new LogLevelFilter(lc));
        beanMap.putIfAbsent(LogThresholdLevelFilter.class.getSimpleName(), new LogThresholdLevelFilter(lc));
    }

    public static void registerBean(String beanName, Object bean) {
        beanMap.putIfAbsent(beanName, bean);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> clazz) {
        return (T) beanMap.get(clazz.getSimpleName());
    }

    @SuppressWarnings("unchecked")
    public static <T> Set<T> getBeans(Class<T> clazz) {
        return (Set<T>) beanMap.values().stream().filter(l -> clazz.isAssignableFrom(l.getClass())).collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName) {
        return (T) beanMap.get(beanName);
    }

    public static void clear() {
        beanMap.clear();
    }
}
