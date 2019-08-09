package com.yaomy.common.initializingbean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Package: com.yaomy.common.initializingbean.MyInitializingBean
 * @Date: 2019/8/8 19:17
 * @Version: 1.0
 */
@Component
public class MyInitializingBean implements BeanPostProcessor,InitializingBean {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("--------postProcessBeforeInitialization-------------");
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("-------postProcessAfterInitialization-------------------");
        return bean;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("---MyInitializingBean---");
    }
}
