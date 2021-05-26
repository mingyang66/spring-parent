package com.emily.infrastructure.cloud.bean;


import com.emily.infrastructure.cloud.feign.FeignLoggerAutoConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.retry.annotation.RetryConfiguration;

/**
 * @author Emily
 * @program: spring-parent
 * @description: bean注册成功但未实例化之前调用的后置处理器，用来更改BeanDefinition
 * @create: 2020/09/11
 */
public class EmilyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        if (beanFactory.containsBeanDefinition(RetryConfiguration.class.getName())) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(RetryConfiguration.class.getName());
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
        if (beanFactory.containsBeanDefinition(FeignLoggerAutoConfiguration.class.getName())) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(FeignLoggerAutoConfiguration.class.getName());
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
        if (beanFactory.containsBeanDefinition(FeignLoggerAutoConfiguration.FEIGN_LOGGER_NORMAL_BEAN_NAME)) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(FeignLoggerAutoConfiguration.FEIGN_LOGGER_NORMAL_BEAN_NAME);
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
        if (beanFactory.containsBeanDefinition(FeignLoggerAutoConfiguration.FEIGN_LOGGER_EXCEPTION_BEAN_NAME)) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(FeignLoggerAutoConfiguration.FEIGN_LOGGER_EXCEPTION_BEAN_NAME);
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
    }
}
