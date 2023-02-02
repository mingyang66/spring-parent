package com.emily.infrastructure.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description :  单元测试
 * @Author :  Emily
 * @CreateDate :  Created in 2023/1/30 1:22 下午
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class MysqlServiceImplTest {

    @Before
    public void setUp() throws Exception {
        System.out.println("--before--");
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("--after--");
    }

    @Test
    public void getMysql(){
        System.out.println("--in--");
        Assert.assertEquals("a", 5, 5);
    }
}
