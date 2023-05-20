package com.emily.infrastructure.sensitive.test;

import com.emily.infrastructure.sensitive.DeSensitiveUtils;
import com.emily.infrastructure.sensitive.SensitiveUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * @Description : 单元测试
 * @Author :  Emily
 * @CreateDate :  Created in 2023/5/14 4:50 PM
 */
public class SensitiveTest {
    @Test
    public void simpleFieldTest() throws IllegalAccessException {
        People people = new People();
        people.setUsername("孙少平");
        people.setPassword("ssp");
        People p = DeSensitiveUtils.acquire(people);
        Assert.assertEquals(p, people);
        Assert.assertEquals(p.getUsername(), "--隐藏--");
    }

    @Test
    public void flexFieldTest() throws IllegalAccessException {
        People people = new People();
        people.setKey("email");
        people.setValue("1563919868@qq.com");
        People p = DeSensitiveUtils.acquire(people);
        Assert.assertEquals(p, people);
        Assert.assertEquals(p.getKey(), "email");
        Assert.assertEquals(p.getValue(), "1*********@qq.com");

        people.setKey("phone");
        people.setValue("1563919868");
        p = DeSensitiveUtils.acquire(people);
        Assert.assertEquals(p.getKey(), "phone");
        Assert.assertEquals(people.getValue(),"15******68");
    }
    @Test
    public void jsonNullFieldTest(){
        People people = new People();
        people.setKey("email");
        people.setValue("1563919868@qq.com");
        people.setStr("测试null");
        Map<String, Object> s = (Map<String,Object>)SensitiveUtils.acquire(people);
        Assert.assertEquals(s.get("str"), null);
        Assert.assertEquals(s.get("age"), 0);
        Assert.assertEquals(s.get("b"), (byte)0);
        Assert.assertEquals(s.get("s"), (short)0);
        Assert.assertEquals(s.get("l"), 0l);
    }

    @Test
    public void jsonNullFieldTest1() throws IllegalAccessException {
        People people = new People();
        people.setKey("email");
        people.setValue("1563919868@qq.com");
        people.setStr("测试null");
        People s = DeSensitiveUtils.acquire(people);
        Assert.assertEquals(s.getStr(), null);
        Assert.assertEquals(s.getAge(), 0);
        Assert.assertEquals(s.getB(), (byte)0);
        Assert.assertEquals(s.getS(), (short)0);
        Assert.assertEquals(s.getL(), 0l);
    }
}
