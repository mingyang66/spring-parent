package com.emily.infrastructure.autoconfigure.bean.factory;

import com.emily.infrastructure.logback.factory.LogbackFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Emily
 * @program: spring-parent
 * @description:
 * @create: 2020/09/11
 */
@Configuration(proxyBeanMethods = false)
public class EmilyBeanFactoryPostProcessorAutoConfiguration implements InitializingBean, DisposableBean {

    @Bean
    public static EmilyBeanFactoryPostProcessor grainBeanFactoryPostProcessor(){
        return new EmilyBeanFactoryPostProcessor();
    }

    @Override
    public void destroy() throws Exception {
        LogbackFactory.info(EmilyBeanFactoryPostProcessorAutoConfiguration.class, "<== 【销毁--自动化配置】----BeanFactoryPostProcessor自定义组件【EmilyBeanFactoryPostProcessorAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LogbackFactory.info(EmilyBeanFactoryPostProcessorAutoConfiguration.class, "==> 【初始化--自动化配置】----BeanFactoryPostProcessor自定义组件【EmilyBeanFactoryPostProcessorAutoConfiguration】");
    }
}
