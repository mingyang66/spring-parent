package com.emily.infrastructure.test.security;

import com.emily.infrastructure.security.utils.SecurityUtils;
import com.emily.infrastructure.test.security.entity.Address;
import com.emily.infrastructure.test.security.entity.UserSimple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author :  姚明洋
 * @since :  2025/2/8 下午4:30
 */
public class SecurityUtilsCollTest {
    @Test
    public void simpleTest() {
        UserSimple user = new UserSimple();
        user.setAge(18);
        user.setUsername("test");
        user.setPassword("123456");

        Address address = new Address();
        address.setCity("上海");
        address.setCountry("中国");
        address.setHeight(156);
        user.setAddress(address);
        UserSimple user2 = SecurityUtils.securityElseGet(user, throwable -> System.out.println("异常" + throwable.getMessage()));
        Assertions.assertEquals(user2.getUsername(), "test-加密后");
        Assertions.assertEquals(user2.getPassword(), "123456-加密后");
        Assertions.assertEquals(user2.getAge(), 18);
        Assertions.assertEquals(user2.getAddress().getCity(), "上海-加密后");
        Assertions.assertEquals(user2.getAddress().getCountry(), "中国-加密后");
        Assertions.assertEquals(user2.getAddress().getHeight(), 156);
    }

    @Test
    public void simpleCollTest() {
        UserSimple user = new UserSimple();
        user.setAge(18);
        user.setUsername("test");
        user.setPassword("123456");

        Address address = new Address();
        address.setCity("上海");
        address.setCountry("中国");
        address.setHeight(156);
        user.setAddress(address);
        List<UserSimple> list = List.of(user);
        List<UserSimple> user2 = SecurityUtils.securityElseGet(list, throwable -> System.out.println("异常" + throwable.getMessage()));
        Assertions.assertEquals(user2.get(0).getUsername(), "test-加密后");
        Assertions.assertEquals(user2.get(0).getPassword(), "123456-加密后");
        Assertions.assertEquals(user2.get(0).getAge(), 18);
        Assertions.assertEquals(user2.get(0).getAddress().getCity(), "上海-加密后");
        Assertions.assertEquals(user2.get(0).getAddress().getCountry(), "中国-加密后");
        Assertions.assertEquals(user2.get(0).getAddress().getHeight(), 156);
    }

    @Test
    public void simpleMapTest() {
        UserSimple user = new UserSimple();
        user.setAge(18);
        user.setUsername("test");
        user.setPassword("123456");

        Address address = new Address();
        address.setCity("上海");
        address.setCountry("中国");
        address.setHeight(156);
        user.setAddress(address);
        Map<String, UserSimple> dataMap = new HashMap<>();
        dataMap.put("test", user);

        Map<String, UserSimple> user2 = SecurityUtils.securityElseGet(dataMap, throwable -> System.out.println("异常" + throwable.getMessage()));
        Assertions.assertEquals(user2.get("test").getUsername(), "test-加密后");
        Assertions.assertEquals(user2.get("test").getPassword(), "123456-加密后");
        Assertions.assertEquals(user2.get("test").getAge(), 18);
        Assertions.assertEquals(user2.get("test").getAddress().getCity(), "上海-加密后");
        Assertions.assertEquals(user2.get("test").getAddress().getCountry(), "中国-加密后");
        Assertions.assertEquals(user2.get("test").getAddress().getHeight(), 156);
    }

    @Test
    public void simpleArrayTest() {
        UserSimple user = new UserSimple();
        user.setAge(18);
        user.setUsername("test");
        user.setPassword("123456");

        Address address = new Address();
        address.setCity("上海");
        address.setCountry("中国");
        address.setHeight(156);
        user.setAddress(address);
        UserSimple[] simples = {user};
        UserSimple[] user2 = SecurityUtils.securityElseGet(simples, throwable -> System.out.println("异常" + throwable.getMessage()));
        Assertions.assertEquals(user2[0].getUsername(), "test-加密后");
        Assertions.assertEquals(user2[0].getPassword(), "123456-加密后");
        Assertions.assertEquals(user2[0].getAge(), 18);
        Assertions.assertEquals(user2[0].getAddress().getCity(), "上海-加密后");
        Assertions.assertEquals(user2[0].getAddress().getCountry(), "中国-加密后");
        Assertions.assertEquals(user2[0].getAddress().getHeight(), 156);
    }
}
