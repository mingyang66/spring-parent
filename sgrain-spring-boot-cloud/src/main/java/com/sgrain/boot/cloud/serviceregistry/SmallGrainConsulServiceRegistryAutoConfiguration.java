package com.sgrain.boot.cloud.serviceregistry;

import com.ecwid.consul.v1.ConsulClient;
import com.sgrain.boot.common.utils.log.LoggerUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.consul.discovery.HeartbeatProperties;
import org.springframework.cloud.consul.discovery.TtlScheduler;
import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistryAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: spring-parent
 * @description: 服务注册自动化配置类
 * @create: 2020/11/17
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(ConsulServiceRegistryAutoConfiguration.class)
@AutoConfigureBefore(ConsulServiceRegistryAutoConfiguration.class)
public class SmallGrainConsulServiceRegistryAutoConfiguration implements InitializingBean, DisposableBean {

    @Bean
    public SmallGrainConsullServiceRegistry smallGrainConsullServiceRegistry(ConsulClient client, ConsulDiscoveryProperties properties,
                                                                             @Autowired(required = false) TtlScheduler ttlScheduler,
                                                                             HeartbeatProperties heartbeatProperties) {
        return new SmallGrainConsullServiceRegistry(client, properties, ttlScheduler, heartbeatProperties);
    }

    @Override
    public void destroy() throws Exception {
        LoggerUtils.info(SmallGrainConsulServiceRegistryAutoConfiguration.class, "【销毁--自动化配置】----服务注册自动化配置组件【SmallGrainConsulServiceRegistryAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerUtils.info(SmallGrainConsulServiceRegistryAutoConfiguration.class, "【初始化--自动化配置】----服务注册自动化配置组件【SmallGrainConsulServiceRegistryAutoConfiguration】");
    }
}
