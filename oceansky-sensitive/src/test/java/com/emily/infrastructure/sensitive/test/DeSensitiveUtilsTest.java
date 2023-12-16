package com.emily.infrastructure.sensitive.test;

import com.emily.infrastructure.sensitive.DeSensitiveUtils;
import com.emily.infrastructure.sensitive.test.entity.People;
import com.emily.infrastructure.sensitive.test.entity.PeopleMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        Assertions.assertEquals(p, people);
        Assertions.assertEquals(p.getUsername(), "--隐藏--");
    }

    @Test
    public void flexFieldTest() {
        People people = new People();
        people.setKey("email");
        people.setValue("1563919868@qq.com");
        People p = DeSensitiveUtils.acquireElseGet(people);
        Assertions.assertEquals(p, people);
        Assertions.assertEquals(p.getKey(), "email");
        Assertions.assertEquals(p.getValue(), "1***8@qq.com");

        people.setKey("phone");
        people.setValue("1563919868");
        p = DeSensitiveUtils.acquireElseGet(people);
        Assertions.assertEquals(p.getKey(), "phone");
        Assertions.assertEquals(people.getValue(), "15****9868");
    }

    @Test
    public void jsonNullFieldTest1() {
        People people = new People();
        people.setKey("email");
        people.setValue("1563919868@qq.com");
        people.setStr("测试null");
        People s = DeSensitiveUtils.acquireElseGet(people);
        Assertions.assertEquals(s.getStr(), null);
        Assertions.assertEquals(s.getAge(), 0);
        Assertions.assertEquals(s.getB(), (byte) 0);
        Assertions.assertEquals(s.getS(), (short) 0);
        Assertions.assertEquals(s.getL(), 0l);
    }
    @Test
    public void testFieldMap(){
        PeopleMap peopleMap = new PeopleMap();
        peopleMap.getParams().put("password","12345");
        peopleMap.getParams().put("username","田晓霞");
        peopleMap.getParams().put("phone","15645562587");
        PeopleMap p = DeSensitiveUtils.acquireElseGet(peopleMap);
        System.out.println(p);
    }
}
