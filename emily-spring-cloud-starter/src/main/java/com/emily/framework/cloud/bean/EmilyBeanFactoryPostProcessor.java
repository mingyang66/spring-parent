package com.emily.framework.cloud.bean;


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * @program: spring-parent
 * @description: bean注册成功但未实例化之前调用的后置处理器，用来更改BeanDefinition
 * @create: 2020/09/11
 */
public class EmilyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        if (beanFactory.containsBeanDefinition("org.springframework.retry.annotation.RetryConfiguration")) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition("org.springframework.retry.annotation.RetryConfiguration");
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
    }
}
