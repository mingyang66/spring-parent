package com.emily.infrastructure.file.test;

import com.emily.infrastructure.file.FileResourceUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

/**
 * @Description :  读取资源文件单元测试类
 * @Author :  Emily
 * @CreateDate :  Created in 2023/5/28 4:46 PM
 */
public class FileResourceUtilsTest {
    @Test
    public void loadConfig() throws IOException, IllegalAccessException {
        String path = "classpath:test.properties";
        Properties properties = FileResourceUtils.loadConfig(path);
        Assert.assertEquals(properties.getProperty("test.key"), "testkey");

        path = "/Users/yaomingyang/Documents/IDE/workplace-java/spring-parent/oceansky-file/src/test/resources/test.properties";
        properties = FileResourceUtils.loadConfig(path);
        Assert.assertEquals(properties.getProperty("test.key"), "testkey");

        path = "https://www.baidu.com/";
        properties = FileResourceUtils.loadConfig(path);
        Assert.assertEquals(properties.getProperty("<!DOCTYPE"), "html>");

        path = "classpath:test.xml";
        properties = FileResourceUtils.loadConfig(path);
        Assert.assertEquals(properties.getProperty("username"), "孙少平");
        Assert.assertEquals(properties.getProperty("password"), "123456");

        path = "classpath:test.yaml";
        properties = FileResourceUtils.loadConfig(path);
        System.out.println(properties);

    }
}
