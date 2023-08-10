package com.emily.infrastructure.test.test;

import com.emily.infrastructure.test.po.User;

/**
 * @program: spring-parent
 *  弱引用测试
 * @author Emily
 * @since 2021/11/13
 */
public class WeakReferenceTest {
    public static void main(String[] args) throws InterruptedException {
        //定义弱引用对象
        UserWeakReference reference = new UserWeakReference(new User());
        //输出弱应用对象
        System.out.println("Username:" + reference.get());
        //GC
        System.gc();
        Thread.sleep(5000);
        //输出GC后弱引用对象输出
        System.out.println(reference.get());
        //判定是否弱应用对象已被回收
        if (reference.get() == null) {
            System.out.println("user is clear...");
        }
    }
}
