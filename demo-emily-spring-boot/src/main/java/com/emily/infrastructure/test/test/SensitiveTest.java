package com.emily.infrastructure.test.test;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 脱敏工具类
 *
 * @author Emily
 * @since Created in 2023/4/17 5:07 PM
 */
public class SensitiveTest {
    public static void main(String[] args) throws IOException {
        List<String> list = List.of("classpath*:mapper/mysql/*.xml", "classpath:mapper/oracle/*.xml","classpath:*.properties");
        List<Resource> resources = getResources(list);
        System.out.println(resources.size());
    }

    public static List<Resource> getResources(List<String> list) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        List<Resource> resources = new ArrayList<>();
        for (String path : list) {
            resources.addAll(List.of(resolver.getResources(path)));
        }
        return resources;
    }
}
