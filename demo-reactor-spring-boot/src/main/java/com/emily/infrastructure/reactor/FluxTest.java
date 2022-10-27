package com.emily.infrastructure.reactor;

import reactor.core.publisher.Flux;

import java.time.Duration;

/**
 * @Description :  响应式编程发布者测试
 * @Author :  Emily
 * @CreateDate :  Created in 2022/8/5 11:08 上午
 */
public class FluxTest {

    public static void main(String[] args) {
        //Flux.just(1,2,3,4,5,6).subscribe(a-> System.out.println(a));
        //Mono.just(1).subscribe(t-> System.out.println(t),(throwable)-> System.out.println("error"), ()-> System.out.println("Completed"));
        //Flux.range(1,6).map(i -> i*i).subscribe(i-> System.out.println(i));
        Flux.just("flux", "mono").flatMap(s -> Flux.fromArray(s.split("\\s*"))).delayElements(Duration.ofMillis(100)).doOnNext(s -> System.out.println(s));
    }
}
