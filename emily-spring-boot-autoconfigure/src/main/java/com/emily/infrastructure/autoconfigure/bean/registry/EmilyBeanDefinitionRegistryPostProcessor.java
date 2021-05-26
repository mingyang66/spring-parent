package com.emily.infrastructure.autoconfigure.bean.registry;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

/**
 * @author Emily
 * @program: spring-parent
 * @description:
 * 1.提供一个钩子方法postProcessBeanDefinitionRegistry通过代码的方式注册bean到IOC容器
 * 2.提供一个钩子方法postProcessBeanFactory在BeanDefinition注册到IOC容器之后及实例化之前调用的方法，可以用来修改BeanDefinition属性
 * 3.钩子方法postProcessBeanDefinitionRegistry会在postProcessBeanFactory之前执行
 * @create: 2020/09/16
 */
public class EmilyBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
    /**
     * 注册bean到IOC容器之中
     * @param registry
     * @throws BeansException
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

    }

    /**
     * 提供修改bean factory的钩子方法
     * @param beanFactory 容器工厂
     * @throws BeansException
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
