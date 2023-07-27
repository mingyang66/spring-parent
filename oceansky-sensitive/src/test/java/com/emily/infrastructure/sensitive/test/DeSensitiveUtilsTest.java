package com.emily.infrastructure.sensitive.test;

import com.emily.infrastructure.sensitive.DeSensitiveUtils;
import com.emily.infrastructure.sensitive.test.entity.People;
import org.junit.Assert;
import org.junit.Test;

/**
 *  实体类对象脱敏单元测试
 * @author  Emily
 * @since :  Created in 2023/5/31 3:34 PM
 */
public class DeSensitiveUtilsTest {
    @Test
    public void simpleFieldTest() {
        People people = new People();
        people.setUsername("孙少平");
        people.setPassword("ssp");
        People p = DeSensitiveUtils.acquireElseGet(people);
        Assert.assertEquals(p, people);
        Assert.assertEquals(p.getUsername(), "--隐藏--");
    }

    @Test
    public void flexFieldTest() {
        People people = new People();
        people.setKey("email");
        people.setValue("1563919868@qq.com");
        People p = DeSensitiveUtils.acquireElseGet(people);
        Assert.assertEquals(p, people);
        Assert.assertEquals(p.getKey(), "email");
        Assert.assertEquals(p.getValue(), "1***8@qq.com");

        people.setKey("phone");
        people.setValue("1563919868");
        p = DeSensitiveUtils.acquireElseGet(people);
        Assert.assertEquals(p.getKey(), "phone");
        Assert.assertEquals(people.getValue(), "15****9868");
    }

    @Test
    public void jsonNullFieldTest1() {
        People people = new People();
        people.setKey("email");
        people.setValue("1563919868@qq.com");
        people.setStr("测试null");
        People s = DeSensitiveUtils.acquireElseGet(people);
        Assert.assertEquals(s.getStr(), null);
        Assert.assertEquals(s.getAge(), 0);
        Assert.assertEquals(s.getB(), (byte) 0);
        Assert.assertEquals(s.getS(), (short) 0);
        Assert.assertEquals(s.getL(), 0l);
    }
}
