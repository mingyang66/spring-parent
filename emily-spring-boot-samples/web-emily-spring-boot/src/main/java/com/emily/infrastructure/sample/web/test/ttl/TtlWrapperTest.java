package com.emily.infrastructure.sample.web.test.ttl;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.emily.infrastructure.sample.web.entity.User;

/**
 * @author :  Emily
 * @since :  2023/8/7 5:25 PM
 */
public class TtlWrapperTest {
    static final TransmittableThreadLocal<User> context = new TransmittableThreadLocal<User>() {
        @Override
        protected User childValue(User parentValue) {
            return initialValue();
            //return super.childValue(parentValue);
        }
    };

    public static void main(String[] args) {
        System.out.println("-------------" + context.get());
        User user = new User();
        user.setUsername("田晓霞");
        context.set(user);
        System.out.println("-------------" + context.get());
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("-------------" + context.get());
            }
        }).start();
    }
}
