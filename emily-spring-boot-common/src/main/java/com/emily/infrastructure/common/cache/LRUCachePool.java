package com.emily.infrastructure.common.cache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description :  LRU是least Recently Used的缩写，即最新最少使用
 * @Author :  Emily
 * @CreateDate :  Created in 2022/3/3 2:36 下午
 */
@SuppressWarnings("all")
public class LRUCachePool<K, V> {
    /**
     * 缓存key
     */
    private K key;
    /**
     * 缓存value
     */
    private V value;
    /**
     * LRU 缓存对象
     */
    private LRUCache<K, V> cache;

    public LRUCachePool(int initCapacity) {
        if (initCapacity < 0) {
            initCapacity = 16;
        }
        this.cache = new LRUCache(initCapacity);
    }

    public V get(K key) {
        return this.cache.get(key);
    }

    public LRUCache<K, V> getAll() {
        return this.cache;
    }

    public void remove(K key) {
        this.cache.remove(key);
    }

    public void put(K key, V value) {
        this.cache.put(key, value);
    }

    public int getSize() {
        return this.cache.size();
    }

    /**
     * LRU缓存
     *
     * @param <K>
     * @param <V>
     */
    public static class LRUCache<K, V> extends LinkedHashMap<K, V> {
        /**
         * 最大容量
         */
        private int maxSize;

        /**
         * @param initCapacity 初始化容量
         *                     loadFactor 加载因子
         *                     accessOrder 迭代模式（访问顺序）
         */
        public LRUCache(int initCapacity) {
            super(initCapacity, 0.75f, true);
            this.maxSize = initCapacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            if (size() > this.maxSize) {
                return true;
            }
            return false;
        }
    }
}
