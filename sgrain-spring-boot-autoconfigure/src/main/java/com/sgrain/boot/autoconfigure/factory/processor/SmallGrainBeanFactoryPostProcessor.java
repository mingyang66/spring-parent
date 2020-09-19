package com.sgrain.boot.autoconfigure.factory.processor;

import com.sgrain.boot.autoconfigure.aop.apilog.ApiLogAutoConfiguration;
import com.sgrain.boot.autoconfigure.aop.apilog.ApiLogProperties;
import com.sgrain.boot.autoconfigure.aop.apilog.service.impl.AsyncLogAopServiceImpl;
import com.sgrain.boot.autoconfigure.threadpool.AsyncThreadPoolAutoConfiguration;
import com.sgrain.boot.autoconfigure.threadpool.AsyncThreadPoolProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * @program: spring-parent
 * @description: bean注册成功但未实例化之前调用的后置处理器，用来更改BeanDefinition
 * @create: 2020/09/11
 */
public class SmallGrainBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (beanFactory.containsBeanDefinition(AsyncThreadPoolAutoConfiguration.class.getName())) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(AsyncThreadPoolAutoConfiguration.class.getName());
            beanDefinition.setRole(2);
        }
        if (beanFactory.containsBeanDefinition("spring.sgrain.async-thread-pool-" + AsyncThreadPoolProperties.class.getName())) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition("spring.sgrain.async-thread-pool-" + AsyncThreadPoolProperties.class.getName());
            beanDefinition.setRole(2);
        }
        if (beanFactory.containsBeanDefinition("spring.sgrain.api-log-" + ApiLogProperties.class.getName())) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition("spring.sgrain.api-log-" + ApiLogProperties.class.getName());
            beanDefinition.setRole(2);
        }
        if (beanFactory.containsBeanDefinition(ApiLogAutoConfiguration.class.getName())) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(ApiLogAutoConfiguration.class.getName());
            beanDefinition.setRole(2);
        }
        if (beanFactory.containsBeanDefinition(AsyncLogAopServiceImpl.class.getName())) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(AsyncLogAopServiceImpl.class.getName());
            beanDefinition.setRole(2);
        }
        if (beanFactory.containsBeanDefinition(ApiLogAutoConfiguration.API_LOG_EXCEPTION_BEAN_NAME)) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(ApiLogAutoConfiguration.API_LOG_EXCEPTION_BEAN_NAME);
            beanDefinition.setRole(2);
        }
        if (beanFactory.containsBeanDefinition(ApiLogAutoConfiguration.API_LOG_NORMAL_BEAN_NAME)) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(ApiLogAutoConfiguration.API_LOG_NORMAL_BEAN_NAME);
            beanDefinition.setRole(2);
        }
    }
}
