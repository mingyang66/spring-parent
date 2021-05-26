package com.emily.infrastructure.context.ioc;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @Description: IOC容器实例上下文，可以获取容器内部的实例对象
 * @Author: Emily
 * @create: 2021/5/13
 */
@SuppressWarnings("all")
public class IOCContext {

    private static ApplicationContext CONTEXT;


    public static void setApplicationContext(ApplicationContext applicationContext) {
        CONTEXT = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return CONTEXT;
    }

    /**
     * 获取指定Class类型的实例对象
     *
     * @param requiredType 实例类型
     * @param <T>          泛型类类型
     * @return
     */
    public static <T> T getBean(Class<T> requiredType) {
        return CONTEXT.getBean(requiredType);
    }

    /**
     * 获取指定bean实例名称的实例对象
     *
     * @param name
     * @return
     * @throws BeansException
     */
    public static Object getBean(String name) {
        return CONTEXT.getBean(name);
    }

    /**
     * 获取指定beanname的实例对象，并且转换为指定类型
     *
     * @param name         bean实例名称
     * @param requiredType 目标类型
     * @param <T>          类型
     * @return
     */
    public static <T> T getBean(String name, Class<T> requiredType) {
        return CONTEXT.getBean(name, requiredType);
    }

    /**
     * @Desc 实例对象必须是原型模式
     * @param name bean的名称
     * @param args 实例参数
     * @return
     * @Bean
     * @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
     * public Job job(Long id){
     * Job job = new Job();
     * job.setId(id);
     * return job;
     * }
     */
    public static Object getBean(String name, Object... args) {
        return CONTEXT.getBean(name, args);
    }

    /**
     * 获取指定注解标注的实例bean集合
     *
     * @param annotationType 注解类型
     * @return
     */
    public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
        return CONTEXT.getBeansWithAnnotation(annotationType);
    }

    /**
     * 获取指定注解类型标注的bean实例名称
     *
     * @param annotationType 主机类型
     * @return
     */
    public static String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
        return CONTEXT.getBeanNamesForAnnotation(annotationType);
    }

    /**
     * 判定IOC容器中是否存在指定名称的实例bean
     *
     * @param name bean实例名称
     * @return true|false
     */
    public static boolean containsBean(String name) {
        return CONTEXT.containsBean(name);
    }

    /**
     * 判定指定bean 名称的实例是否是原型模式
     *
     * @param name bean实例名称
     * @return true|false
     */
    public static boolean isPrototype(String name) {
        return CONTEXT.isPrototype(name);
    }

    /**
     * 判定指定的bean实例是否是单例模式
     *
     * @param name bean实例名称
     * @return true|false
     */
    public static boolean isSingleton(String name) {
        return CONTEXT.isSingleton(name);
    }

    /**
     * 判定指定bean名称是否和指定的类型匹配
     *
     * @param name        bean实例名称
     * @param typeToMatch 匹配的类类型
     * @return
     */
    public static boolean isTypeMatch(String name, Class<?> typeToMatch) {
        return CONTEXT.isTypeMatch(name, typeToMatch);
    }
}
