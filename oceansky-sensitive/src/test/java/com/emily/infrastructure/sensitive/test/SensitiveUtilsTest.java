package com.emily.infrastructure.sensitive.test;

import com.emily.infrastructure.sensitive.SensitiveUtils;
import com.emily.infrastructure.sensitive.test.entity.People;
import com.emily.infrastructure.sensitive.test.entity.PeopleMap;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description : 单元测试
 * @Author :  Emily
 * @CreateDate :  Created in 2023/5/14 4:50 PM
 */
public class SensitiveUtilsTest {

    @Test
    public void jsonNullFieldTest() {
        People people = new People();
        people.setKey("email");
        people.setValue("1563919868@qq.com");
        people.setStr("测试null");
        Map<String, Object> s = (Map<String, Object>) SensitiveUtils.acquireElseGet(people);
        Assert.assertNull(s.get("str"));
        Assert.assertEquals(s.get("age"), 0);
        Assert.assertEquals(s.get("b"), (byte) 0);
        Assert.assertEquals(s.get("s"), (short) 0);
        Assert.assertEquals(s.get("l"), 0L);
    }

    @Test
    public void map() {
        PeopleMap peopleMap = new PeopleMap();
        peopleMap.setUsername("田晓霞");
        peopleMap.setPassword("123456");
        peopleMap.setLocalDateTime(LocalDateTime.now());
        PeopleMap.SubMap subMap = new PeopleMap.SubMap();
        subMap.setSub("subMap");
        peopleMap.getSubMapMap().put("subMap", subMap);
        Map<String, PeopleMap> dataMap = new HashMap<>();
        dataMap.put("test", peopleMap);
        Map<String, Object> map = (Map<String, Object>) SensitiveUtils.acquireElseGet(dataMap);
        Assert.assertEquals(((Map<String, Object>) map.get("test")).get("username"), "田晓霞");
        Assert.assertEquals(((Map<String, Object>) map.get("test")).get("password"), "123456");
    }
}
