package com.sgrain.boot.autoconfigure.bean.factory;

import com.sgrain.boot.common.utils.log.LoggerUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/09/11
 */
@Configuration(proxyBeanMethods = false)
public class SmallGrainBeanFactoryPostProcessorAutoConfiguration implements InitializingBean, DisposableBean {

    @Bean
    public static SmallGrainBeanFactoryPostProcessor testBeanPostProcessor(){
        return new SmallGrainBeanFactoryPostProcessor();
    }

    @Override
    public void destroy() throws Exception {
        LoggerUtils.info(SmallGrainBeanFactoryPostProcessorAutoConfiguration.class, "【销毁--自动化配置】----BeanFactoryPostProcessor自定义组件【SmallGrainBeanFactoryPostProcessorAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerUtils.info(SmallGrainBeanFactoryPostProcessorAutoConfiguration.class, "【初始化--自动化配置】----BeanFactoryPostProcessor自定义组件【SmallGrainBeanFactoryPostProcessorAutoConfiguration】");
    }
}
