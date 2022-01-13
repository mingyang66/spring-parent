package com.emily.infrastructure.autoconfigure.bean.factory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * @author Emily
 * @program: spring-parent
 * @description: bean注册成功但未实例化之前调用的后置处理器，用来更改BeanDefinition
 * @create: 2020/09/11
 */
@SuppressWarnings("all")
public class EmilyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        /*if (beanFactory.containsBeanDefinition("spring.emily.request-" + ApiRequestProperties.class.getName())) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition("spring.emily.request-" + ApiRequestProperties.class.getName());
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }*/
    /*    if (beanFactory.containsBeanDefinition(ApiRequestAutoConfiguration.class.getName())) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(ApiRequestAutoConfiguration.class.getName());
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
        if (beanFactory.containsBeanDefinition(ApiRequestAutoConfiguration.API_LOG_EXCEPTION_BEAN_NAME)) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(ApiRequestAutoConfiguration.API_LOG_EXCEPTION_BEAN_NAME);
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
        if (beanFactory.containsBeanDefinition(ApiRequestAutoConfiguration.API_LOG_NORMAL_BEAN_NAME)) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(ApiRequestAutoConfiguration.API_LOG_NORMAL_BEAN_NAME);
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }*/
      /*  if (beanFactory.containsBeanDefinition(RedisAutoConfiguration.class.getName())) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(RedisAutoConfiguration.class.getName());
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }*/
   /*     if (beanFactory.containsBeanDefinition(HttpClientAutoConfiguration.class.getName())) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(HttpClientAutoConfiguration.class.getName());
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }*/
        /*if (beanFactory.containsBeanDefinition(String.join("", "spring.emily.http-client-", HttpClientProperties.class.getName()))) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(String.join("", "spring.emily.http-client-", HttpClientProperties.class.getName()));
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }*/
        /*if (beanFactory.containsBeanDefinition("loggerService")) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition("loggerService");
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }*/
    /*    if (beanFactory.containsBeanDefinition("spring.emily.request.logback-com.emily.infrastructure.autoconfigure.request.RequestLoggerProperties")) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition("spring.emily.request.logback-com.emily.infrastructure.autoconfigure.request.RequestLoggerProperties");
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }*/
 /*       if (beanFactory.containsBeanDefinition("spring.emily.datasource-com.emily.infrastructure.datasource.DataSourceProperties")) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition("spring.emily.datasource-com.emily.infrastructure.datasource.DataSourceProperties");
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }*/
    /*    if (beanFactory.containsBeanDefinition("com.alibaba.druid.spring.boot.autoconfigure.stat.DruidSpringAopConfiguration")) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition("com.alibaba.druid.spring.boot.autoconfigure.stat.DruidSpringAopConfiguration");
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
        if (beanFactory.containsBeanDefinition("spring.datasource.druid-com.alibaba.druid.spring.boot.autoconfigure.properties.DruidStatPropertie")) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition("spring.datasource.druid-com.alibaba.druid.spring.boot.autoconfigure.properties.DruidStatPropertie");
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
        if (beanFactory.containsBeanDefinition("org.springframework.aop.support.RegexpMethodPointcutAdvisor")) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition("org.springframework.aop.support.RegexpMethodPointcutAdvisor");
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }
        if (beanFactory.containsBeanDefinition("spring.datasource.druid-com.alibaba.druid.spring.boot.autoconfigure.properties.DruidStatProperties")) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition("spring.datasource.druid-com.alibaba.druid.spring.boot.autoconfigure.properties.DruidStatProperties");
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }*/
    }
}
