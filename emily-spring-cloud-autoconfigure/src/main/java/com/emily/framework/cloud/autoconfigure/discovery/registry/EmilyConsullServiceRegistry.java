package com.emily.framework.cloud.autoconfigure.discovery.registry;

import com.ecwid.consul.v1.ConsulClient;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.consul.discovery.HeartbeatProperties;
import org.springframework.cloud.consul.discovery.TtlScheduler;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistration;
import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistry;

/**
 * @program: spring-parent
 * @description: 服务注册中心
 * @create: 2020/11/17
 */
public class EmilyConsullServiceRegistry extends ConsulServiceRegistry {


    public EmilyConsullServiceRegistry(ConsulClient client, ConsulDiscoveryProperties properties,
                                       TtlScheduler ttlScheduler,
                                       HeartbeatProperties heartbeatProperties) {
        super(client, properties, ttlScheduler, heartbeatProperties);
    }

    @Override
    public void register(ConsulRegistration reg) {

        super.register(reg);
    }

    @Override
    public void deregister(ConsulRegistration reg) {
        super.deregister(reg);
    }
}
