package com.emily.framework.cloud.config;

import com.ecwid.consul.v1.ConsulClient;
import com.emily.framework.common.logger.LoggerUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.consul.config.ConsulConfigBootstrapConfiguration;
import org.springframework.cloud.consul.config.ConsulConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @description: consul配置中心配置文件加载自动化配置
 * @author: Emily
 * @create: 2020/11/09
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(ConsulConfigBootstrapConfiguration.class)
@ConditionalOnProperty(name = "spring.cloud.consul.config.enabled", matchIfMissing = true)
public class EmilyConsulConfigBootstrapConfiguration implements InitializingBean {
    private ConsulClient consul;

    public EmilyConsulConfigBootstrapConfiguration(ConsulClient consul) {
        this.consul = consul;
    }

    @Primary
    @Bean("emilyConsulPropertySourceLocator")
    public EmiyConsulPropertySourceLocator smallGrainConsulPropertySourceLocator(
            ConsulConfigProperties consulConfigProperties) {
        return new EmiyConsulPropertySourceLocator(this.consul, consulConfigProperties);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerUtils.info(EmilyConsulConfigBootstrapConfiguration.class, "【自动化配置】----SpringCloud config乱码解决组件初始化完成...");
    }
}
