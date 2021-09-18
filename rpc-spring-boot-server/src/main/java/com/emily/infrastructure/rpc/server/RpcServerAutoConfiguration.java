package com.emily.infrastructure.rpc.server;

import com.emily.infrastructure.rpc.common.service.HelloServiceImpl;
import com.emily.infrastructure.rpc.server.handler.RpcServerHandler;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @program: spring-parent
 * @description: RPC服务调用配置类
 * @author: Emily
 * @create: 2021/09/18
 */
@Configuration
public class RpcServerAutoConfiguration {

    @PostConstruct
    public void startServer(){
        RpcServerHandler.setClassNameMapping(new HelloServiceImpl());
        RpcServer.start(9999);
    }

}
