package com.emily.infrastructure.test.controller;

import com.emily.infrastructure.rpc.client.NettyClient;
import com.emily.infrastructure.test.service.HelloService;
import com.emily.infrastructure.test.service.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: spring-parent
 * @description: RPC服务调用
 * @author: Emily
 * @create: 2021/09/18
 */
@RestController
@RequestMapping("api/netty")
public class NettyController {

    @GetMapping("start")
    public void start() {
        NettyClient.start("127.0.0.1", 9999);
    }

    @GetMapping("rpc")
    public Result rpc() throws InterruptedException {
        //连接netty，并获得一个代理对象
        HelloService bean = NettyClient.getBean(HelloService.class);
        //测试返回结果为java bean
        return bean.hello("ffafa");
    }


}
