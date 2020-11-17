package com.sgrain.boot.cloud.serviceregistry;

import com.ecwid.consul.v1.ConsulClient;
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
 * @description:
 * @author: 姚明洋
 * @create: 2020/11/17
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(ConsulServiceRegistryAutoConfiguration.class)
@AutoConfigureBefore(ConsulServiceRegistryAutoConfiguration.class)
public class SmallGrainConsulServiceRegistryAutoConfiguration {

    @Bean
    public SmallGrainConsullServiceRegistry smallGrainConsullServiceRegistry(ConsulClient client, ConsulDiscoveryProperties properties, @Autowired(required = false) TtlScheduler ttlScheduler, HeartbeatProperties heartbeatProperties){
        return new SmallGrainConsullServiceRegistry(client, properties, ttlScheduler, heartbeatProperties);
    }
}
