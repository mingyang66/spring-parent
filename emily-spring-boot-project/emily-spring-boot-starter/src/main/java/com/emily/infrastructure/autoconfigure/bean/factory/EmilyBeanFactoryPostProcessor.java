package com.emily.infrastructure.autoconfigure.bean.factory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * bean注册成功但未实例化之前调用的后置处理器，用来更改BeanDefinition
 *
 * @author Emily
 * @since 2020/09/11
 */
@SuppressWarnings("all")
public class EmilyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
