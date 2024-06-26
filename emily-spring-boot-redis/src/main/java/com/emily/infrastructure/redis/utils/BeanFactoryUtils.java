package com.emily.infrastructure.redis.utils;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * IOC容器工厂类
 *
 * @author :  Emily
 * @since :  2023/10/20 9:35 PM
 */
public class BeanFactoryUtils {
    private static DefaultListableBeanFactory defaultListableBeanFactory;

    public static void setDefaultListableBeanFactory(DefaultListableBeanFactory defaultListableBeanFactory) {
        BeanFactoryUtils.defaultListableBeanFactory = defaultListableBeanFactory;
    }

    public static void registerSingleton(String beanName, Object singletonObject) {
        defaultListableBeanFactory.registerSingleton(beanName, singletonObject);
    }

    public static <T> T getBean(String name, Class<T> requiredType) {
        return defaultListableBeanFactory.getBean(name, requiredType);
    }
}
