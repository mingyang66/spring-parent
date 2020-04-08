package com.yaomy.control.test.api;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/03/06
 */
public class Test {
    public static void main(String[] args) {
        RateLimiter rateLimiter = RateLimiter.create(5);
        List<Runnable> tasks = Lists.newArrayList();
        for (int i=0; i<10; i++){
           if(rateLimiter.tryAcquire(1000, TimeUnit.MILLISECONDS)){
               System.out.println("成功抢到小米10Pro，恭喜恭喜！");
           } else{
               System.out.println("sorry,抢光了，下次再来吧");
           }
        }

    }
}
