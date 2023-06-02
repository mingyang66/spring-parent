package com.emily.infrastructure.file.test;

import com.emily.infrastructure.file.ResourceUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

/**
 * @Description :  读取资源文件单元测试类
 * @Author :  Emily
 * @CreateDate :  Created in 2023/5/28 4:46 PM
 */
public class ResourceUtilsTest {
    @Test
    public void loadConfig() {
        String path = "classpath:test.properties";
        Properties properties = ResourceUtils.loadConfigElseGet(path);
        Assert.assertEquals(properties.getProperty("test.key"), "testkey");

        path = "/Users/yaomingyang/Documents/IDE/workplace-java/spring-parent/oceansky-file/src/test/resources/test.properties";
        properties = ResourceUtils.loadConfigElseGet(path);
        Assert.assertEquals(properties.getProperty("test.key"), "testkey");

        path = "https://www.baidu.com/";
        properties = ResourceUtils.loadConfigElseGet(path);
        Assert.assertEquals(properties.getProperty("<!DOCTYPE"), "html>");

        path = "classpath:test.xml";
        properties = ResourceUtils.loadConfigElseGet(path);
        Assert.assertEquals(properties.getProperty("username"), "孙少平");
        Assert.assertEquals(properties.getProperty("password"), "123456");

        path = "classpath:test.yaml";
        properties = ResourceUtils.loadConfigElseGet(path);
        Assert.assertEquals(properties.getProperty("username"), "tiaoxiao");
        Assert.assertEquals(properties.getProperty("password"), "1234");

    }
}
