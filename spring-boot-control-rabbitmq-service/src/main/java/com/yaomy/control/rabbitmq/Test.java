package com.yaomy.control.rabbitmq;

import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Author: 姚明洋
 * @Date: 2019/10/15 15:46
 * @Version: 1.0
 */
public class Test {
    public static void main(String[] args) throws Exception{

        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(()->{
            System.out.println("------1------------");
            String s = null;
            s.length();
            return 1;
        }).exceptionally((throwable) -> {
            return null;
        });
        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(()->{
            System.out.println("------2------------");
            return 2;
        }).handleAsync((a, thrable)->{
            System.out.println("-----------handle2---------");
            return a;
        });
        System.out.println(future1.get());
        System.out.println(future2.get());


    }
}
