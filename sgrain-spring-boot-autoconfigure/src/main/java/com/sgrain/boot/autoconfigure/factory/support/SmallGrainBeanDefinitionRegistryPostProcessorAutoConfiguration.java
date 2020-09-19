package com.sgrain.boot.autoconfigure.factory.support;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: spring-parent
 * @description:
 * @author: 姚明洋
 * @create: 2020/09/16
 */
@Configuration(proxyBeanMethods = false)
public class SmallGrainBeanDefinitionRegistryPostProcessorAutoConfiguration {

    @Bean
    public SmallGrainBeanDefinitionRegistryPostProcessor testBeanDefinitionRegistryPostProcessor(){
        return new SmallGrainBeanDefinitionRegistryPostProcessor();
    }
}
