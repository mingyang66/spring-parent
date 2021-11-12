package com.emily.infrastructure.test;

import com.alibaba.ttl.TtlCallable;
import com.alibaba.ttl.TtlRunnable;
import com.alibaba.ttl.TtlWrappers;
import com.alibaba.ttl.threadpool.TtlExecutors;
import com.alibaba.ttl.threadpool.TtlForkJoinPoolHelper;
import com.alibaba.ttl.threadpool.agent.internal.transformlet.impl.TtlForkJoinTransformlet;
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
public class Test {
    public static final ThreadLocal<String> threadLocal = new InheritableThreadLocal<>();
    private static ExecutorService executorService = Executors.newFixedThreadPool(1);

    public static void main(String[] args) throws IllegalAccessException, NoSuchFieldException, InterruptedException, ExecutionException {
        //threadLocal.set("主线程1。。。");
      /*  System.out.println("线程1：" + threadLocal.get());
        executorService.submit(TtlRunnable.get(() -> System.out.println("子线程：" + Thread.currentThread().getName() + ":" + threadLocal.get())));
        Thread.sleep(1000);
        threadLocal.set("主线程2。。。");
        System.out.println("线程2：" + threadLocal.get());
        executorService.submit(TtlRunnable.get(() -> System.out.println("子线程：" + Thread.currentThread().getName() + ":" + threadLocal.get())));
*/
        CompletableFuture future1 = CompletableFuture.supplyAsync(()->{
            System.out.println("子线程1：" + Thread.currentThread().getName() + ":" + threadLocal.get());
            return threadLocal.get();
        });
        System.out.println(future1.get()+"");
        threadLocal.set("主线程2。。。");
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(()->{
            System.out.println("子线程2：" + Thread.currentThread().getName() + ":" + threadLocal.get());
            return threadLocal.get();
        });
        System.out.println(future2.get()+"");
    }

    private static Executor executor = TtlExecutors.getTtlExecutor(Executors.newCachedThreadPool());
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
