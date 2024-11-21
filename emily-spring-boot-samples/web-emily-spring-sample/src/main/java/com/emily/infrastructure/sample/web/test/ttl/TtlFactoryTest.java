package com.emily.infrastructure.sample.web.test.ttl;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;
import com.emily.infrastructure.sample.web.entity.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author :  Emily
 * @since :  2023/8/10 1:07 PM
 */
public class TtlFactoryTest {
    static final ExecutorService service = Executors.newFixedThreadPool(2, TtlExecutors.getDefaultDisableInheritableThreadFactory());
    private static final TransmittableThreadLocal<User> context = new TransmittableThreadLocal<>();

    public static void main(String[] args) {
        User user = new User();
        user.setUsername("田晓霞");
        context.set(user);
        System.out.println("1-" + Thread.currentThread().getName() + "-" + context.get());
        service.submit(new Runnable() {
            @Override
            public void run() {
                User user1 = new User();
                user1.setUsername("田二");
                context.set(user1);
                System.out.println("2-" + Thread.currentThread().getName() + "-" + context.get());
            }
        });
        System.out.println("3-" + Thread.currentThread().getName() + "-" + context.get());
    }
}
