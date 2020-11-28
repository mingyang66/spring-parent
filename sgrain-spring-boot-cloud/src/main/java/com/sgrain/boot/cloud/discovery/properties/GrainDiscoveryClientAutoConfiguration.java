package com.sgrain.boot.cloud.discovery.properties;

import com.sgrain.boot.common.utils.UUIDUtils;
import com.sgrain.boot.common.utils.constant.CharacterUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.UtilAutoConfiguration;
import org.springframework.cloud.consul.ConsulAutoConfiguration;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryClientConfiguration;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: spring-parent
 * @description: 服务发现客户端配置组件
 * @author: 姚明洋
 * @create: 2020/11/28
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(ConsulDiscoveryClientConfiguration.class)
@AutoConfigureBefore(ConsulDiscoveryClientConfiguration.class)
@AutoConfigureAfter({ UtilAutoConfiguration.class, ConsulAutoConfiguration.class })
public class GrainDiscoveryClientAutoConfiguration {
    /**
     * 自定义服务发现配置类
     * @param inetUtils
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public ConsulDiscoveryProperties consulDiscoveryProperties(InetUtils inetUtils) {
        ConsulDiscoveryProperties properties = new ConsulDiscoveryProperties(inetUtils);
        //自定义生成实例ID, 实例名称不可以以数字开头
        properties.setInstanceId(StringUtils.join("grain", CharacterUtils.LINE_THROUGH_CENTER, UUIDUtils.randomUUID()));
        return properties;
    }
}
