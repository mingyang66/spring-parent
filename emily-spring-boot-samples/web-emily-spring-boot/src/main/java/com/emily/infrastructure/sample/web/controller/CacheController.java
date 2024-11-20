package com.emily.infrastructure.sample.web.controller;

import com.emily.infrastructure.sample.web.entity.cache.CacheUser;
import com.emily.infrastructure.sample.web.service.CacheService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

/**
 * @author Emily
 * @program: spring-parent
 * 高速缓存控制器
 * @since 2021/08/28
 */
@RestController
@RequestMapping("api/cache")
public class CacheController {

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
    private final CacheService cacheService;

    public CacheController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @GetMapping("getCache")
    public void getCache() throws InterruptedException {
        caffeine.put("test", "测试");
        while (true) {
            System.out.println(caffeine.getIfPresent("test"));
            Thread.sleep(1000);
        }
    }

    @GetMapping("cache")
    public String cache() {
        CacheUser user = new CacheUser();
        user.setId("987654321");
        user.setName("沃伯格");
        user.setPassword("159357");
        return this.cacheService.cache(user);
    }
}
