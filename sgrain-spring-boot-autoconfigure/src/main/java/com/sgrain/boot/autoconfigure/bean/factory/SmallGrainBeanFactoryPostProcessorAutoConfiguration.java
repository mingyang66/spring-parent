package com.sgrain.boot.autoconfigure.bean.factory;

import com.sgrain.boot.common.utils.log.LoggerUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/09/11
 */
@Configuration(proxyBeanMethods = false)
public class SmallGrainBeanFactoryPostProcessorAutoConfiguration implements CommandLineRunner {

    @Bean
    public static SmallGrainBeanFactoryPostProcessor testBeanPostProcessor(){
        return new SmallGrainBeanFactoryPostProcessor();
    }

    @Override
    public void run(String... args) throws Exception {
        LoggerUtils.info(SmallGrainBeanFactoryPostProcessorAutoConfiguration.class, "【自动化配置】----BeanFactoryPostProcessor自定义组件初始化完成...");
    }
}
