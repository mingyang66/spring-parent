package com.emily.infrastructure.test.test.ttl;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;
import com.emily.infrastructure.test.entity.User;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author :  Emily
 * @since :  2023/8/10 9:30 AM
 */
public class TtlErrorTest {
    private static final TransmittableThreadLocal<User> context = new TransmittableThreadLocal<>();

    private static final Executor service = TtlExecutors.getTtlExecutor(Executors.newSingleThreadExecutor());

    public static void main(String[] args) {
        User user1 = new User();
        user1.setUsername("孙少平");
        user1.setPassword("123456");
        context.set(user1);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                User user = new User();
                user.setUsername("田晓霞");
                context.set(user);
                System.out.println("1-" + Thread.currentThread().getName() + ":" + context.get());
                //移除上下文变量
                context.remove();
            }
        };
        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                System.out.println("2-" + Thread.currentThread().getName() + ":" + context.get());
            }
        };
        service.execute(runnable);
        service.execute(runnable1);
        //移除上下文
        context.remove();
    }
}
