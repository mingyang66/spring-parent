package com.emily.infrastructure.test.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @program: spring-parent
 * @description: 高速缓存控制器
 * @author: Emily
 * @create: 2021/08/28
 */
@RestController
@RequestMapping("api/caffeine")
public class CaffeineController {

    public static final Cache<Object, String> caffeine = Caffeine.newBuilder()
            .maximumSize(10)
            .expireAfterWrite(Duration.ofSeconds(20))
            .evictionListener(new RemovalListener<Object, Object>() {
                @Override
                public void onRemoval(@Nullable Object o, @Nullable Object o2, RemovalCause removalCause) {
                    System.out.println("缓存数据被删除：" + o + "--" + o2);
                }
            })
            .build();

    @GetMapping("getCache")
    public void getCache() throws InterruptedException {
        caffeine.put("test", "测试");
        while (true){
            System.out.println(caffeine.getIfPresent("test"));
            Thread.sleep(1000);
        }
    }
}
