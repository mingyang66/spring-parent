package com.emily.infrastructure.sample.web.test.ttl;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;
import com.emily.infrastructure.test.entity.User;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author :  Emily
 * @since :  2023/8/8 9:16 PM
 */
public class TtlTest {
    static final TransmittableThreadLocal<User> context = new TransmittableThreadLocal<User>() {
        @Override
        protected User childValue(User parentValue) {
            return initialValue();
        }
    };

    public static void main(String[] args) throws InterruptedException {
        ExecutorService service = Executors.newSingleThreadScheduledExecutor();
        Executor executor = TtlExecutors.getTtlExecutor(service);
        User user = new User();
        //user.setUsername("田晓霞");
        context.set(user);
        System.out.println("1---" + Thread.currentThread().getName() + ":" + context.get());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("2---" + Thread.currentThread().getName() + ":" + context.get());
                user.setUsername("孙少平");
                System.out.println("3---" + Thread.currentThread().getName() + ":" + context.get());
            }
        });
        System.out.println("4---" + Thread.currentThread().getName() + ":" + context.get());
        context.remove();
        System.out.println("5---" + Thread.currentThread().getName() + ":" + context.get());
    }
}
