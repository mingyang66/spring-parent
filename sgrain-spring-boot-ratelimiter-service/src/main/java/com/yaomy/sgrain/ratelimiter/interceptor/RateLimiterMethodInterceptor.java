package com.yaomy.sgrain.ratelimiter.interceptor;

import com.yaomy.sgrain.common.enums.DateFormatEnum;
import com.yaomy.sgrain.ratelimiter.annotation.RateLimiter;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @program: spring-parent
 * @description: 接口访问的频率控制
 * @author: 姚明洋
 * @create: 2020/03/23
 */
public class RateLimiterMethodInterceptor implements MethodInterceptor {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        System.out.println("test interceptor--------");
        Method method = invocation.getMethod();
        if(method.isAnnotationPresent(RateLimiter.class)){
            RateLimiter rateLimite = method.getAnnotation(RateLimiter.class);
            com.google.common.util.concurrent.RateLimiter limiter = com.google.common.util.concurrent.RateLimiter.create(rateLimite.permits());
            if(limiter.tryAcquire()){
                System.out.println("获取到了令牌");
            } else{
                System.out.println("未获取令牌---------------");
            }
        }
        return invocation.proceed();
    }

    public static void main(String[] args) {
        com.google.common.util.concurrent.RateLimiter rateLimiter = com.google.common.util.concurrent.RateLimiter.create(1);
        System.out.println("获取1个令牌开始，时间为" + DateFormatUtils.format(new Date(), DateFormatEnum.YYYY_MM_DD_HH_MM_SS.getFormat()));
        double cost = rateLimiter.acquire(1);
        System.out.println("获取1个令牌结束，时间为" + DateFormatUtils.format(new Date(), DateFormatEnum.YYYY_MM_DD_HH_MM_SS.getFormat()) + ", 耗时" + cost + "ms");
        System.out.println("获取5个令牌开始，时间为" + DateFormatUtils.format(new Date(), DateFormatEnum.YYYY_MM_DD_HH_MM_SS.getFormat()));
        cost = rateLimiter.acquire(5);
        System.out.println("获取5个令牌结束，时间为" + DateFormatUtils.format(new Date(), DateFormatEnum.YYYY_MM_DD_HH_MM_SS.getFormat()) + ", 耗时" + cost + "ms");
        System.out.println("获取3个令牌开始，时间为" + DateFormatUtils.format(new Date(), DateFormatEnum.YYYY_MM_DD_HH_MM_SS.getFormat()));
        cost = rateLimiter.acquire(3);
        System.out.println("获取3个令牌结束，时间为" + DateFormatUtils.format(new Date(), DateFormatEnum.YYYY_MM_DD_HH_MM_SS.getFormat()) + ", 耗时" + cost + "ms");
    }
}
