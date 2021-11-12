package com.emily.infrastructure.test.controller;

import com.alibaba.ttl.TtlWrappers;
import com.alibaba.ttl.threadpool.TtlExecutors;
import com.emily.infrastructure.core.holder.ContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.*;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/05/17
 */
@RestController
@RequestMapping("api/threadlocal/")
public class TTLController {

    @GetMapping("test")
    public String get() throws ExecutionException, InterruptedException {
        System.out.println("-----上下文ID:"+ContextHolder.get().getTraceId());
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(TtlWrappers.wrap(()->{
            System.out.println("子线程1：" + Thread.currentThread().getName() + ":" + ContextHolder.get().getTraceId());
            return ContextHolder.get().getTraceId();
        }));
        return future1.get();
    }
}
