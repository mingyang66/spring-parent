package com.yaomy.control.test.service.impl;

import com.yaomy.control.test.service.AsyncService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @program: spring-parent
 * @description:
 * @author: 姚明洋
 * @create: 2020/08/21
 */
@Service
public class AsyncServiceImpl implements AsyncService {
    @Override
    @Async
    public String async1(String name) {
        System.out.println("线程名称是："+Thread.currentThread().getName());
        name.toString();
        return "async1";
    }
}
