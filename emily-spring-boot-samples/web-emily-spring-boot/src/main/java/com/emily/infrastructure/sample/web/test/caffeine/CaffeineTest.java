package com.emily.infrastructure.sample.web.test.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

/**
 * @author Emily
 * @since Created in 2022/5/31 11:21 上午
 */
public class CaffeineTest {
    private static final Cache<String, Boolean> CAFFEINE = Caffeine.newBuilder()
            //设置缓存的最大大小为10。当缓存中的项数达到10时，最近最少使用的项将被自动移除。
            //.maximumSize(10)
            //设置缓存的最大权重为10。这个设置与.maximumSize(10)结合使用，可以用来限制缓存中的总数据量。
            .maximumWeight(10)
            //设置缓存的权重值，如下：设置每存储一个boolean值的权重加1
            .weigher((k, v) -> 1)
            //设置缓存项在写入后60秒过期。这意味着，一旦一个缓存项被写入，它将在60秒后自动从缓存中移除。
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .build();

    public static void main(String[] args) {
        CAFFEINE.put("1", false);
        CAFFEINE.put("2", true);
        System.out.println(CAFFEINE.getIfPresent("1"));
        System.out.println(CAFFEINE.getIfPresent(1000 + ""));
        System.out.println("打印缓存个数：" + CAFFEINE.estimatedSize());
        // 移除指定的key
        CAFFEINE.invalidate("1");
        System.out.println(CAFFEINE.getIfPresent("1"));
    }
}
