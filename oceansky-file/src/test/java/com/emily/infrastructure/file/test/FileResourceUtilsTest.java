package com.emily.infrastructure.file.test;

import com.emily.infrastructure.file.FileResourceUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

/**
 * @Description :  读取资源文件单元测试类
 * @Author :  Emily
 * @CreateDate :  Created in 2023/5/28 4:46 PM
 */
public class FileResourceUtilsTest {
    @Test
    public void loadConfig() {
        String path = "classpath:test.properties";
        Properties properties = FileResourceUtils.loadConfigElseGet(path);
        Assert.assertEquals(properties.getProperty("test.key"), "testkey");

        path = "/Users/yaomingyang/Documents/IDE/workplace-java/spring-parent/oceansky-file/src/test/resources/test.properties";
        properties = FileResourceUtils.loadConfigElseGet(path);
        Assert.assertEquals(properties.getProperty("test.key"), "testkey");

        path = "https://www.baidu.com/";
        properties = FileResourceUtils.loadConfigElseGet(path);
        Assert.assertEquals(properties.getProperty("<!DOCTYPE"), "html>");

        path = "classpath:test.xml";
        properties = FileResourceUtils.loadConfigElseGet(path);
        Assert.assertEquals(properties.getProperty("username"), "孙少平");
        Assert.assertEquals(properties.getProperty("password"), "123456");

        path = "classpath:test.yaml";
        properties = FileResourceUtils.loadConfigElseGet(path);
        Assert.assertEquals(properties.getProperty("username"), "tiaoxiao");
        Assert.assertEquals(properties.getProperty("password"), "1234");

    }
}
