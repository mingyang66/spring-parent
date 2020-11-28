package com.sgrain.boot.cloud.discovery.registry;

import com.ecwid.consul.v1.ConsulClient;
import com.sgrain.boot.common.utils.RequestUtils;
import com.sgrain.boot.common.utils.constant.CharacterUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.cloud.client.serviceregistry.endpoint.ServiceRegistryEndpoint;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.consul.discovery.HeartbeatProperties;
import org.springframework.cloud.consul.discovery.TtlScheduler;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistration;
import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistry;
import org.springframework.core.env.Environment;

/**
 * @program: spring-parent
 * @description: 服务注册中心
 * @create: 2020/11/17
 */
public class GrainConsullServiceRegistry extends ConsulServiceRegistry {


    public GrainConsullServiceRegistry(ConsulClient client, ConsulDiscoveryProperties properties,
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
