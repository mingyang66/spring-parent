package com.sgrain.boot.autoconfigure.bean.factory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/09/11
 */
@Configuration
public class SmallGrainBeanFactoryPostProcessorAutoConfiguration {

    @Bean
    public static SmallGrainBeanFactoryPostProcessor testBeanPostProcessor(){
        return new SmallGrainBeanFactoryPostProcessor();
    }
}
