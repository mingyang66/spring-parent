package com.emily.infrastructure.test.controller;

import com.emily.infrastructure.rpc.client.proxy.RpcProxy;
import com.emily.infrastructure.test.service.HelloService;
import com.emily.infrastructure.test.service.Result;
import org.apache.commons.lang3.RandomUtils;
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
        //RpcProxy.create("127.0.0.1", 9999);
    }

    @GetMapping("rpc")
    public Object rpc() throws InterruptedException {
        //连接netty，并获得一个代理对象
        HelloService bean = RpcProxy.create(HelloService.class);
        int a = RandomUtils.nextInt(0, 2);
        if (a == 0) {
            return bean.str();
        }
        return bean.hello("ffafa");
    }


}
