package com.emily.infrastructure.rpc;

import com.emily.infrastructure.rpc.server.NettyServer;
import com.emily.infrastructure.rpc.server.NettyServerHandler;
import com.emily.infrastructure.rpc.service.HelloServiceImpl;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @program: spring-parent
 * @description: RPC服务调用配置类
 * @author: 姚明洋
 * @create: 2021/09/18
 */
@Configuration
public class NettyAutoConfiguration {

    @PostConstruct
    public void startServer(){
        NettyServerHandler.setClassNameMapping(new HelloServiceImpl());
        NettyServer.start(9999);
    }

    public static void main(String[] args) {
        NettyServerHandler.setClassNameMapping(new HelloServiceImpl());
        NettyServer.start(9999);
    }

}
