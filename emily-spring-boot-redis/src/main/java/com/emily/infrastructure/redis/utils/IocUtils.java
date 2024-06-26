package com.emily.infrastructure.redis.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * IOC容器实例上下文，可以获取容器内部的实例对象
 *
 * @author Emily
 * @since 2021/5/13
 */
@SuppressWarnings("all")
public class IocUtils {

    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        IocUtils.applicationContext = applicationContext;
    }

    /**
     * 获取指定Class类型的实例对象
     *
     * @param requiredType 实例类型
     * @param <T>          泛型类类型
     * @return bean实例对象
     */
    public static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    /**
     * 获取指定bean实例名称的实例对象
     *
     * @param name bean名称
     * @return bean实例对象
     * @throws BeansException 异常
     */
    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    /**
     * 获取指定beanname的实例对象，并且转换为指定类型
     *
     * @param name         bean实例名称
     * @param requiredType 目标类型
     * @param <T>          类型
     * @return bean实例对象
     */
    public static <T> T getBean(String name, Class<T> requiredType) {
        return applicationContext.getBean(name, requiredType);
    }

    /**
     * 实例对象必须是原型模式
     *
     * @param name bean的名称
     * @param args 实例参数
     * @return bean对象
     */
    public static Object getBean(String name, Object... args) {
        return applicationContext.getBean(name, args);
    }

    /**
     * 获取指定注解标注的实例bean集合
     *
     * @param annotationType 注解类型
     * @return 注解标注的bean对象
     */
    public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
        return applicationContext.getBeansWithAnnotation(annotationType);
    }

    /**
     * 获取指定注解类型标注的bean实例名称
     *
     * @param annotationType 主机类型
     * @return 注解标注的beanname
     */
    public static String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
        return applicationContext.getBeanNamesForAnnotation(annotationType);
    }

    /**
     * 判定IOC容器中是否存在指定名称的实例bean
     *
     * @param name bean实例名称
     * @return true|false
     */
    public static boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }

    /**
     * 判定指定bean 名称的实例是否是原型模式
     *
     * @param name bean实例名称
     * @return true|false
     */
    public static boolean isPrototype(String name) {
        return applicationContext.isPrototype(name);
    }

    /**
     * 判定指定的bean实例是否是单例模式
     *
     * @param name bean实例名称
     * @return true|false
     */
    public static boolean isSingleton(String name) {
        return applicationContext.isSingleton(name);
    }

    /**
     * 判定指定bean名称是否和指定的类型匹配
     *
     * @param name        bean实例名称
     * @param typeToMatch 匹配的类类型
     * @return 是否和指定的类型匹配
     */
    public static boolean isTypeMatch(String name, Class<?> typeToMatch) {
        return applicationContext.isTypeMatch(name, typeToMatch);
    }

    /**
     * 获取指定类的所有类实例对象
     *
     * @param type 类class对象
     * @param <T>  类
     * @return 返回实例对象的集合
     */
    public static <T> Map<String, T> getBeansOfType(@Nullable Class<T> type) {
        return applicationContext.getBeansOfType(type);
    }

    /**
     * 获取指定类的所有类实例对象
     *
     * @param type                 类class对象
     * @param includeNonSingletons 是否包含非单例对象
     * @param allowEagerInit       是否允许初始化lazy延迟加载初始化类和FactoryBean定义初始化类
     * @param <T>                  类
     * @return 返回实例对象的集合
     */
    public static <T> Map<String, T> getBeansOfType(@Nullable Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) {
        return applicationContext.getBeansOfType(type, includeNonSingletons, allowEagerInit);
    }
}
