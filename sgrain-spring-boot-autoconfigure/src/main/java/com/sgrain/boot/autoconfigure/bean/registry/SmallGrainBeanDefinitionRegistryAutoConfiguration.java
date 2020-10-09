package com.sgrain.boot.autoconfigure.bean.registry;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @program: spring-parent
 * @description:
 * @author: 姚明洋
 * @create: 2020/09/16
 */
@Configuration(proxyBeanMethods = false)
@Import(SmallGrainImportBeanDefinitionRegistrar.class)
public class SmallGrainBeanDefinitionRegistryAutoConfiguration {
    /**
     * spring BeanFacory的后置处理器，会在IOC容器执行扫描注册（@ComponentScan和@ComponentScans）、自动化配置加载注册之前执行，提前将bean注入到IOC容器
     *
     * @return
     */
    @Bean
    public SmallGrainBeanDefinitionRegistryPostProcessor smallGrainBeanDefinitionRegistryPostProcessor() {
        return new SmallGrainBeanDefinitionRegistryPostProcessor();
    }
}
