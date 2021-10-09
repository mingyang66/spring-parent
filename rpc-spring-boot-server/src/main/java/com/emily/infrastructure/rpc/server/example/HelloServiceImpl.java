package com.emily.infrastructure.rpc.server.example;

import com.emily.infrastructure.rpc.server.annotation.RpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/09/17
 */
@Service
@RpcService
public class HelloServiceImpl implements HelloService{
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);
    @Override
    public Result hello(String s) {
        //logger.info("收到消费者的请求-----" + s);
        Result result=new Result();
        result.setId(1);
        result.setContent("你好,我已经收到了你的消费请求");
        return result;
    }

    @Override
    public String str() {
        return "我是一个字符串。。。";
    }
}
