package com.sgrain.boot.autoconfigure.bean.registry;

import com.sgrain.boot.common.utils.LoggerUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/09/16
 */
@Configuration(proxyBeanMethods = false)
@Import(SmallGrainImportBeanDefinitionRegistrar.class)
public class SmallGrainBeanDefinitionRegistryAutoConfiguration implements CommandLineRunner {
    /**
     * spring BeanFacory的后置处理器，会在IOC容器执行扫描注册（@ComponentScan和@ComponentScans）、自动化配置加载注册之前执行，提前将bean注入到IOC容器
     *
     * @return
     */
    @Bean
    public SmallGrainBeanDefinitionRegistryPostProcessor smallGrainBeanDefinitionRegistryPostProcessor() {
        return new SmallGrainBeanDefinitionRegistryPostProcessor();
    }

    @Override
    public void run(String... args) throws Exception {
        LoggerUtils.info(SmallGrainBeanDefinitionRegistryAutoConfiguration.class, "【自动化配置】----BeanDefinitionRegistryPostProcessor自定义组件初始化完成...");
    }
}
