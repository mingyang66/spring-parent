package com.sgrain.boot.cloud.serviceregistry;

import com.ecwid.consul.v1.ConsulClient;
import com.sgrain.boot.common.utils.RequestUtils;
import com.sgrain.boot.common.utils.UUIDUtils;
import com.sgrain.boot.common.utils.constant.CharacterUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.consul.discovery.HeartbeatProperties;
import org.springframework.cloud.consul.discovery.TtlScheduler;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistration;
import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistry;

/**
 * @program: spring-parent
 * @description:
 * @author: 姚明洋
 * @create: 2020/11/17
 */
public class SmallGrainConsullServiceRegistry extends ConsulServiceRegistry {

    public SmallGrainConsullServiceRegistry(ConsulClient client, ConsulDiscoveryProperties properties, TtlScheduler ttlScheduler, HeartbeatProperties heartbeatProperties) {
        super(client, properties, ttlScheduler, heartbeatProperties);
    }

    @Override
    public void register(ConsulRegistration reg) {
        //服务实例ID
        reg.getService().setId(StringUtils.join(RequestUtils.getServerIp(), "(", reg.getService().getId(), ")"));
        //获取当前服务器的IP地址
        reg.getService().setAddress(RequestUtils.getServerIp());

        super.register(reg);
    }
}
