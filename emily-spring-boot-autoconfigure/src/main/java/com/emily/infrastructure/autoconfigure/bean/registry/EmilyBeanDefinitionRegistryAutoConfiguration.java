package com.emily.infrastructure.autoconfigure.bean.registry;

import com.emily.infrastructure.autoconfigure.condition.MacOsCondition;
import com.emily.infrastructure.logback.factory.LogbackFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Emily
 * @program: spring-parent
 * @description:
 * @create: 2020/09/16
 */
@Configuration(proxyBeanMethods = false)
@Conditional(MacOsCondition.class)
@Import(EmilyImportBeanDefinitionRegistrar.class)
public class EmilyBeanDefinitionRegistryAutoConfiguration implements InitializingBean, DisposableBean {
    /**
     * spring BeanFacory的后置处理器，会在IOC容器执行扫描注册（@ComponentScan和@ComponentScans）、自动化配置加载注册之前执行，提前将bean注入到IOC容器
     *
     * @return
     */
    @Bean
    public EmilyBeanDefinitionRegistryPostProcessor smallEmilyBeanDefinitionRegistryPostProcessor() {
        return new EmilyBeanDefinitionRegistryPostProcessor();
    }

    @Override
    public void destroy() throws Exception {
        LogbackFactory.info(EmilyBeanDefinitionRegistryAutoConfiguration.class, "<== 【销毁--自动化配置】----自定义组件【EmilyBeanDefinitionRegistryAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LogbackFactory.info(EmilyBeanDefinitionRegistryAutoConfiguration.class, "==> 【初始化--自动化配置】----自定义组件【EmilyBeanDefinitionRegistryAutoConfiguration】");
    }
}
