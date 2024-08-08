package com.emily.infrastructure.common.test;

import com.emily.infrastructure.common.PropertiesUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Properties;

/**
 *  读取资源文件单元测试类
 * @author  Emily
 * @since  Created in 2023/5/28 4:46 PM
 */
public class PropertiesUtilsTest {
    @Test
    public void loadConfig() {
        String path = "classpath:test.properties";
        Properties properties = PropertiesUtils.loadConfigElseGet(path);
        Assertions.assertEquals(properties.getProperty("test.key"), "testkey");

        path = "/Users/yaomingyang/Documents/IDE/workplace-java/spring-parent/emily-project/oceansky-common/src/test/resources/test.properties";
        properties = PropertiesUtils.loadConfigElseGet(path);
        Assertions.assertEquals(properties.getProperty("test.key"), "testkey");

        path = "https://www.baidu.com/";
        properties = PropertiesUtils.loadConfigElseGet(path);
        Assertions.assertEquals(properties.getProperty("<!DOCTYPE"), "html>");

        path = "classpath:test.xml";
        properties = PropertiesUtils.loadConfigElseGet(path);
        Assertions.assertEquals(properties.getProperty("username"), "孙少平");
        Assertions.assertEquals(properties.getProperty("password"), "123456");

        path = "classpath:test.yaml";
        properties = PropertiesUtils.loadConfigElseGet(path);
        Assertions.assertEquals(properties.getProperty("username"), "田晓霞");
        Assertions.assertEquals(properties.getProperty("password"), "1234");
        Assertions.assertEquals(properties.getProperty("desc"), "descss");

    }
}
