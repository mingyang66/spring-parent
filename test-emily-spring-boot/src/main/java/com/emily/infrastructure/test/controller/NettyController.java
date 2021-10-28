package com.emily.infrastructure.test.controller;

import com.emily.infrastructure.rpc.client.proxy.IRpcInvokeProxy;
import com.emily.infrastructure.rpc.core.example.Student;
import com.emily.infrastructure.test.service.HelloService;
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

    @GetMapping("rpc")
    public Object rpc() throws InterruptedException {
        //连接netty，并获得一个代理对象
        HelloService bean = IRpcInvokeProxy.create(HelloService.class);
        int a = RandomUtils.nextInt(0, 2);
        if (a == 0) {
            return bean.str();
        }
        return bean.hello("ffafa");
    }

    @GetMapping("rpc1")
    public double rpc1() throws InterruptedException {
        //连接netty，并获得一个代理对象
        HelloService bean = IRpcInvokeProxy.create(HelloService.class);
        Integer a = Integer.valueOf(7);
        Long b = Long.valueOf(23);
        return bean.get(7, 23L);
    }
    @GetMapping("rpc2")
    public double rpc2() throws InterruptedException {
        //连接netty，并获得一个代理对象
        HelloService bean = IRpcInvokeProxy.create(HelloService.class);

        return bean.get(7, 23L, "asdf");
    }
    @GetMapping("rpc3")
    public String rpc3() throws InterruptedException {
        //连接netty，并获得一个代理对象
        HelloService bean = IRpcInvokeProxy.create(HelloService.class);
        Student student = new Student();
        student.setAge(10);
        student.setDesc("三号");
        student.setLen(20);
        student.setMoney(20);
        return bean.getStudent(student);
    }
}
