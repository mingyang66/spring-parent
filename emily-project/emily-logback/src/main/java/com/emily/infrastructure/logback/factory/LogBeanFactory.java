package com.emily.infrastructure.logback.factory;

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

    public static void registerBean(String beanName, Object bean) {
        beanMap.putIfAbsent(beanName, bean);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(Class<T> requiredType) {
        return (T) beanMap.get(requiredType.getSimpleName());
    }

    @SuppressWarnings("unchecked")
    public static <T> Set<T> getBeans(Class<T> requiredType) {
        return (Set<T>) beanMap.values().stream().filter(l -> requiredType.isAssignableFrom(l.getClass())).collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName) {
        return (T) beanMap.get(beanName);
    }

    public static boolean containsBean(String beanName) {
        return beanMap.containsKey(beanName);
    }

    public static void clear() {
        beanMap.clear();
    }
}
