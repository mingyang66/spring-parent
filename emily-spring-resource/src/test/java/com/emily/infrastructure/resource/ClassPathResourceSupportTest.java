package com.emily.infrastructure.resource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * @author :  Emily
 * @since :  2024/7/3 下午23:32
 */
public class ClassPathResourceSupportTest {
    @Test
    public void getResource() throws IOException {
        final List<String> list = List.of("classpath*:mapper/mysql/*.xml", "classpath:mapper/oracle/*.xml", "classpath:*.properties");
        ClassPathResourceSupport support = new ClassPathResourceSupport();
        Assertions.assertThrows(FileNotFoundException.class, () -> support.getResources(list));

        final List<String> list1 = List.of("classpath:*-test.properties");
        List<Resource> resources = support.getResources(list1);
        Assertions.assertEquals(resources.size(), 2);
    }
}
