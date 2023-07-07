package com.emily.infrastructure.logger.test;

import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description :
 * @Author :  Emily
 * @CreateDate :  Created in 2023/7/2 3:45 PM
 */
public class Test {
    public static void main(String[] args) {
        Map<String, Integer> cacheLogger = new ConcurrentHashMap<>();
        Integer logger = cacheLogger.putIfAbsent("a", 3);
        Integer logger1 = cacheLogger.putIfAbsent("a", 4);
        System.out.println(logger);
        System.out.println(logger1);
    }
}
