package com.emily.infrastructure.rpc.client;

import com.emily.infrastructure.rpc.core.client.RpcClient;
import com.emily.infrastructure.rpc.core.client.handler.RpcClientChannelHandler;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * @program: spring-parent
 * @description: RPC客户端代理配置类
 * @author: Emily
 * @create: 2021/09/22
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@Configuration
@EnableConfigurationProperties(RpcClientProperties.class)
@ConditionalOnProperty(prefix = RpcClientProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class RpcClientAutoConfiguration {

    @Bean(initMethod = "start")
    public RpcClient rpcClient(RpcClientProperties properties) {
        return new RpcClient(properties.getHost(), properties.getPort());
    }

}
