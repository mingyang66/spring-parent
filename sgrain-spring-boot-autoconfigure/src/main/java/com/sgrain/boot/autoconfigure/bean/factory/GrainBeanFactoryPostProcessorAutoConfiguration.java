package com.sgrain.boot.autoconfigure.bean.factory;

import com.sgrain.boot.common.utils.log.LoggerUtils;
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
public class GrainBeanFactoryPostProcessorAutoConfiguration implements InitializingBean, DisposableBean {

    @Bean
    public static GrainBeanFactoryPostProcessor testBeanPostProcessor(){
        return new GrainBeanFactoryPostProcessor();
    }

    @Override
    public void destroy() throws Exception {
        LoggerUtils.info(GrainBeanFactoryPostProcessorAutoConfiguration.class, "【销毁--自动化配置】----BeanFactoryPostProcessor自定义组件【GrainBeanFactoryPostProcessorAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerUtils.info(GrainBeanFactoryPostProcessorAutoConfiguration.class, "【初始化--自动化配置】----BeanFactoryPostProcessor自定义组件【GrainBeanFactoryPostProcessorAutoConfiguration】");
    }
}
