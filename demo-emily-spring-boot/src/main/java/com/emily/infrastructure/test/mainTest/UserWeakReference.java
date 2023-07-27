package com.emily.infrastructure.test.mainTest;

import com.emily.infrastructure.test.po.User;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * @program: spring-parent
 *  弱引用
 * @author Emily
 * @since 2021/11/13
 */
public class UserWeakReference extends WeakReference<User> {

    public UserWeakReference(User user) {
        super(user);
    }

    public UserWeakReference(User user, ReferenceQueue<? super User> q) {
        super(user, q);
    }
}
