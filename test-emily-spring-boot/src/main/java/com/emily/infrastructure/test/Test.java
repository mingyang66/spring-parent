package com.emily.infrastructure.test;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/05/17
 */
public class Test {
    public static final ThreadLocal<String> threadLocal = new InheritableThreadLocal<>();
    private static ExecutorService executorService = Executors.newFixedThreadPool(1);

    public static void main(String[] args) throws IllegalAccessException, NoSuchFieldException, InterruptedException {
        threadLocal.set("主线程1。。。");
        System.out.println("线程1：" + threadLocal.get());
        executorService.submit(() -> System.out.println("子线程：" + Thread.currentThread().getName() + ":" + threadLocal.get()));
        Thread.sleep(1000);
        threadLocal.set("主线程2。。。");
        System.out.println("线程2：" + threadLocal.get());
        executorService.submit(() -> System.out.println("子线程：" + Thread.currentThread().getName() + ":" + threadLocal.get()));
    }

}
