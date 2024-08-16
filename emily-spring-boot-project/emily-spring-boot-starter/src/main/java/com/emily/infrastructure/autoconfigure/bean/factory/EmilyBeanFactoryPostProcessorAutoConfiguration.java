package com.emily.infrastructure.autoconfigure.bean.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author Emily
 * @since 2020/09/11
 */
@AutoConfiguration
public class EmilyBeanFactoryPostProcessorAutoConfiguration implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(EmilyBeanFactoryPostProcessorAutoConfiguration.class);

    @Bean
    public static EmilyBeanFactoryPostProcessor grainBeanFactoryPostProcessor() {
        return new EmilyBeanFactoryPostProcessor();
    }

    @Override
    public void destroy() throws Exception {
        logger.info("<== 【销毁--自动化配置】----BeanFactoryPostProcessor自定义组件【EmilyBeanFactoryPostProcessorAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("==> 【初始化--自动化配置】----BeanFactoryPostProcessor自定义组件【EmilyBeanFactoryPostProcessorAutoConfiguration】");
    }
}
