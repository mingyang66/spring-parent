package com.emily.infrastructure.sensitive.test;

import com.emily.infrastructure.sensitive.DeSensitiveUtils;
import com.emily.infrastructure.sensitive.SensitiveUtils;
import com.emily.infrastructure.sensitive.test.entity.People;
import com.emily.infrastructure.sensitive.test.entity.PeopleMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 *  单元测试
 * @author  Emily
 * @since :  Created in 2023/5/14 4:50 PM
 */
public class SensitiveUtilsTest {

    @Test
    public void jsonNullFieldTest() {
        People people = new People();
        people.setKey("email");
        people.setValue("1563919868@qq.com");
        people.setStr("测试null");
        Map<String, Object> s = (Map<String, Object>) SensitiveUtils.acquireElseGet(people);
        Assertions.assertNull(s.get("str"));
        Assertions.assertEquals(s.get("age"), 0);
        Assertions.assertEquals(s.get("b"), (byte) 0);
        Assertions.assertEquals(s.get("s"), (short) 0);
        Assertions.assertEquals(s.get("l"), 0L);
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
        Assertions.assertEquals(((Map<String, Object>) map.get("test")).get("username"), "田晓霞");
        Assertions.assertEquals(((Map<String, Object>) map.get("test")).get("password"), "123456");
    }

    @Test
    public void testFieldMap(){
        PeopleMap peopleMap = new PeopleMap();
        peopleMap.getParams().put("password","12345");
        peopleMap.getParams().put("username","田晓霞");
        peopleMap.getParams().put("phone","15645562587");
        Object p = SensitiveUtils.acquireElseGet(peopleMap);
        System.out.println(p);
    }
}
