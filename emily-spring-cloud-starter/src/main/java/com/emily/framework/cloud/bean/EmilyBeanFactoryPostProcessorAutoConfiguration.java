package com.emily.framework.cloud.bean;

import com.emily.framework.common.utils.log.LoggerUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/09/11
 */
@Configuration(proxyBeanMethods = false)
public class EmilyBeanFactoryPostProcessorAutoConfiguration implements InitializingBean, DisposableBean {

    @Bean
    public static EmilyBeanFactoryPostProcessor grainCloudBeanFactoryPostProcessor(){
        return new EmilyBeanFactoryPostProcessor();
    }

    @Override
    public void destroy() throws Exception {
        LoggerUtils.info(EmilyBeanFactoryPostProcessorAutoConfiguration.class, "【销毁CLOUD--自动化配置】----BeanFactoryPostProcessor自定义组件【EmilyBeanFactoryPostProcessorAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerUtils.info(EmilyBeanFactoryPostProcessorAutoConfiguration.class, "【初始化CLOUD--自动化配置】----BeanFactoryPostProcessor自定义组件【EmilyBeanFactoryPostProcessorAutoConfiguration】");
    }
}
