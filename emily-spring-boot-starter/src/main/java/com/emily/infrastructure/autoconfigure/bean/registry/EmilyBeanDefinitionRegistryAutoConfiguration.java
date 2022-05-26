package com.emily.infrastructure.autoconfigure.bean.registry;

import com.emily.infrastructure.core.condition.MacOsCondition;
import com.emily.infrastructure.logger.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Import;

/**
 * @author Emily
 * @program: spring-parent
 * @description:
 * @create: 2020/09/16
 */
@AutoConfiguration
@Conditional(MacOsCondition.class)
@Import(EmilyImportBeanDefinitionRegistrar.class)
public class EmilyBeanDefinitionRegistryAutoConfiguration implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(EmilyBeanDefinitionRegistryAutoConfiguration.class);

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
        logger.info("<== 【销毁--自动化配置】----自定义组件【EmilyBeanDefinitionRegistryAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("==> 【初始化--自动化配置】----自定义组件【EmilyBeanDefinitionRegistryAutoConfiguration】");
    }
}
