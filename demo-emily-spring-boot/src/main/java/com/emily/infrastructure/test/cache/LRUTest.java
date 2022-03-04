package com.emily.infrastructure.test.cache;

import com.emily.infrastructure.common.utils.json.JSONUtils;

/**
 * @Description :  lru测试
 * @Author :  Emily
 * @CreateDate :  Created in 2022/3/3 2:48 下午
 */
public class LRUTest {
    public static void main(String[] args) {
        LRUCachePool<String, Integer> pool = new LRUCachePool<>(8);
        for (int i=0;i<12;i++){
            pool.put("key"+i, i);
            if(i==5){
                pool.get("key2");
            }
            System.out.println(JSONUtils.toJSONString(pool.getAll()));
            System.out.println(pool.getSize());

        }
    }

}
