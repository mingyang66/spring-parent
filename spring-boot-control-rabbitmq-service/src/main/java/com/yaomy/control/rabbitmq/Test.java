package com.yaomy.control.rabbitmq;

import org.apache.commons.lang3.time.DateUtils;

import java.util.concurrent.CompletableFuture;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Author: 姚明洋
 * @Date: 2019/10/15 15:46
 * @Version: 1.0
 */
public class Test {
    public static void main(String[] args) throws Exception{


        CompletableFuture<Integer> futurePriceInUSD = CompletableFuture.supplyAsync(()->{
            System.out.println("0----------");
           return 1;
        });
        System.out.println(futurePriceInUSD.get());
    }
}
