package com.emily.infrastructure.sample.web.test;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

/**
 * 布隆过滤器
 *
 * @author Emily
 * @since Created in 2022/8/19 1:11 下午
 */
public class BloomFilterTest {
    public static void main(String[] args) {
        BloomFilter<String> bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), 10000, 0.01);
        bloomFilter.put("10021");
        System.out.println(bloomFilter.mightContain("10021"));
        System.out.println(bloomFilter.mightContain("10022"));
    }
}
