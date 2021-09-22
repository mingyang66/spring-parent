package com.emily.infrastructure.rpc.client;

import com.emily.infrastructure.rpc.core.client.RpcClient;
import com.emily.infrastructure.rpc.core.client.handler.RpcProxyHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: spring-parent
 * @description: RPC客户端代理配置类
 * @author: Emily
 * @create: 2021/09/22
 */
@Configuration
public class RpcProxyAutoConfiguration {

    @Bean(initMethod = "start")
    public RpcClient rpcClient(RpcProxyHandler rpcProxyHandler){
        return new RpcClient(rpcProxyHandler, "127.0.0.1", 9999);
    }

    @Bean
    public RpcProxyHandler rpcProxyHandler(){
        return new RpcProxyHandler();
    }
}
