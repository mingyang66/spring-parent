package com.emily.infrastructure.test.service;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/05/17
 */
public class Test1 {
    public static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void main(String[] args) throws IllegalAccessException, NoSuchFieldException, InterruptedException {
        threadLocal.set("主线程1。。。");
        System.out.println("线程1：" + threadLocal.get());
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("子线程1：" + Thread.currentThread().getName() + ":" + threadLocal.get());
            }
        }).start();

        Thread.sleep(1000);
        threadLocal.set("主线程2。。。");
        System.out.println("线程2：" + threadLocal.get());
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("子线程2：" + Thread.currentThread().getName() + ":" + threadLocal.get());
            }
        }).start();
        //删除本地内存中的变量
        threadLocal.remove();
    }

}
