package com.sgrain.boot.autoconfigure.bean.registry;

import com.sgrain.boot.common.utils.log.LoggerUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/09/16
 */
@Configuration(proxyBeanMethods = false)
@Import(GrainImportBeanDefinitionRegistrar.class)
public class GrainBeanDefinitionRegistryAutoConfiguration implements InitializingBean, DisposableBean {
    /**
     * spring BeanFacory的后置处理器，会在IOC容器执行扫描注册（@ComponentScan和@ComponentScans）、自动化配置加载注册之前执行，提前将bean注入到IOC容器
     *
     * @return
     */
    @Bean
    public GrainBeanDefinitionRegistryPostProcessor smallGrainBeanDefinitionRegistryPostProcessor() {
        return new GrainBeanDefinitionRegistryPostProcessor();
    }

    @Override
    public void destroy() throws Exception {
        LoggerUtils.info(GrainBeanDefinitionRegistryAutoConfiguration.class, "【销毁--自动化配置】----BeanDefinitionRegistryPostProcessor自定义组件【SmallGrainBeanDefinitionRegistryAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerUtils.info(GrainBeanDefinitionRegistryAutoConfiguration.class, "【初始化--自动化配置】----BeanDefinitionRegistryPostProcessor自定义组件【SmallGrainBeanDefinitionRegistryAutoConfiguration】");
    }
}
