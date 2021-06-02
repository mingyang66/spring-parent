package com.emily.infrastructure.cloud.bean;

import com.emily.infrastructure.logback.utils.LoggerUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Emily
 * @program: spring-parent
 * @description: Bean后置处理器
 * @create: 2020/09/11
 */
@Configuration(proxyBeanMethods = false)
public class EmilyBeanFactoryPostProcessorAutoConfiguration implements InitializingBean, DisposableBean {

    @Bean
    public static EmilyBeanFactoryPostProcessor grainCloudBeanFactoryPostProcessor(){
        return new EmilyBeanFactoryPostProcessor();
    }

    @Override
    public void destroy() {
        LoggerUtils.info(EmilyBeanFactoryPostProcessorAutoConfiguration.class, "【销毁--自动化配置】----BeanFactoryPostProcessor自定义组件【EmilyBeanFactoryPostProcessorAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() {
        LoggerUtils.info(EmilyBeanFactoryPostProcessorAutoConfiguration.class, "【初始化--自动化配置】----BeanFactoryPostProcessor自定义组件【EmilyBeanFactoryPostProcessorAutoConfiguration】");
    }
}
