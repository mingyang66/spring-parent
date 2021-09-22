package com.emily.infrastructure.rpc.client;

import com.emily.infrastructure.rpc.core.client.RpcClient;
import com.emily.infrastructure.rpc.core.client.handler.RpcProxyHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: spring-parent
 * @description: RPC客户端代理配置类
 * @author: Emily
 * @create: 2021/09/22
 */
@Configuration
@EnableConfigurationProperties(RpcClientProperties.class)
@ConditionalOnProperty(prefix = RpcClientProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class RpcClientAutoConfiguration {

    @Bean(initMethod = "start")
    public RpcClient rpcClient(RpcProxyHandler rpcProxyHandler, RpcClientProperties properties) {
        return new RpcClient(rpcProxyHandler, properties.getHost(), properties.getPort());
    }

    @Bean
    public RpcProxyHandler rpcProxyHandler() {
        return new RpcProxyHandler();
    }
}
