package com.emily.framework.cloud.autoconfigure.config;

import com.ecwid.consul.v1.ConsulClient;
import com.emily.framework.common.utils.log.LoggerUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.consul.config.ConsulConfigBootstrapConfiguration;
import org.springframework.cloud.consul.config.ConsulConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @description: consul配置中心配置文件加载自动化配置
 * @create: 2020/11/09
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(ConsulConfigBootstrapConfiguration.class)
@ConditionalOnProperty(name = "spring.cloud.consul.config.enabled", matchIfMissing = true)
public class EmilyConsulConfigBootstrapConfiguration implements InitializingBean, DisposableBean {

    private ConsulClient consul;

    public EmilyConsulConfigBootstrapConfiguration(ConsulClient consul) {
        this.consul = consul;
    }

    @Primary
    @Bean("grainConsulPropertySourceLocator")
    public EmilyConsulPropertySourceLocator grainConsulPropertySourceLocator(
            ConsulConfigProperties consulConfigProperties) {
        return new EmilyConsulPropertySourceLocator(this.consul, consulConfigProperties);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerUtils.info(EmilyConsulConfigBootstrapConfiguration.class, "【初始化CLOUD--自动化配置】----SpringCloud配置中心中文乱码组件【EmilyConsulConfigBootstrapConfiguration】");
    }

    @Override
    public void destroy() throws Exception {
        LoggerUtils.info(EmilyConsulConfigBootstrapConfiguration.class, "【销毁CLOUD--自动化配置】----SpringCloud配置中心中文乱码组件【EmilyConsulConfigBootstrapConfiguration】");
    }
}
