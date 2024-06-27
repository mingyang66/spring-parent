package com.emily.infrastructure.redis.utils;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * IOC容器工厂类
 *
 * @author :  Emily
 * @since :  2023/10/20 9:35 PM
 */
public class BeanFactoryUtils {
    /**
     * 容器对象
     */
    private static DefaultListableBeanFactory defaultListableBeanFactory;

    /**
     * 初始化容器对象
     *
     * @param defaultListableBeanFactory 容器对象
     */
    public static void setDefaultListableBeanFactory(DefaultListableBeanFactory defaultListableBeanFactory) {
        BeanFactoryUtils.defaultListableBeanFactory = defaultListableBeanFactory;
    }

    /**
     * 获取指定bean对应的实例对象
     *
     * @param beanName        beanName
     * @param singletonObject 实例类型
     */
    public static void registerSingleton(String beanName, Object singletonObject) {
        defaultListableBeanFactory.registerSingleton(beanName, singletonObject);
    }

    /**
     * 获取指定bean对应的实例对象
     *
     * @param name         beanName
     * @param requiredType 实例对象
     * @param <T>          实例类型
     * @return 实例对象
     */
    public static <T> T getBean(String name, Class<T> requiredType) {
        return defaultListableBeanFactory.getBean(name, requiredType);
    }
}
