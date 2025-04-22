package com.emily.infrastructure.test.security;

import com.emily.infrastructure.security.utils.SecurityUtils;
import com.emily.infrastructure.test.security.entity.Address;
import com.emily.infrastructure.test.security.entity.ArrayEntity;
import com.emily.infrastructure.test.security.entity.ArraySubEntity;
import com.emily.infrastructure.test.security.entity.UserSimple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author :  Emily
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

        user.setList(List.of("劳伦斯"));

        Address address1 = new Address();
        address1.setCity("上海1");
        address1.setCountry("中国1");
        address1.setHeight(1561);
        user.setAddressList(List.of(address1));

        List<UserSimple> list = List.of(user);
        List<UserSimple> user2 = SecurityUtils.securityElseGet(list, throwable -> System.out.println("异常" + throwable.getMessage()));
        Assertions.assertEquals(user2.get(0).getUsername(), "test-加密后");
        Assertions.assertEquals(user2.get(0).getPassword(), "123456-加密后");
        Assertions.assertEquals(user2.get(0).getAge(), 18);
        Assertions.assertEquals(user2.get(0).getAddress().getCity(), "上海-加密后");
        Assertions.assertEquals(user2.get(0).getAddress().getCountry(), "中国-加密后");
        Assertions.assertEquals(user2.get(0).getAddress().getHeight(), 156);

        Assertions.assertEquals(user2.get(0).getList().get(0), "劳伦斯-加密后");

        Assertions.assertEquals(user2.get(0).getAddressList().get(0).getCity(), "上海1-加密后");
        Assertions.assertEquals(user2.get(0).getAddressList().get(0).getCountry(), "中国1-加密后");
        Assertions.assertEquals(user2.get(0).getAddressList().get(0).getHeight(), 1561);
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

        Map<String, String> strMap = new HashMap<>();
        strMap.put("username", "李渊");
        user.setStrMap(strMap);

        Map<String, Address> addressMap = new HashMap<>();
        Address address1 = new Address();
        address1.setCity("上海1");
        address1.setCountry("中国1");
        address1.setHeight(1561);
        addressMap.put("address", address1);
        user.setAddressMap(addressMap);

        Map<String, UserSimple> dataMap = new HashMap<>();
        dataMap.put("test", user);


        Map<String, UserSimple> user2 = SecurityUtils.securityElseGet(dataMap, throwable -> System.out.println("异常" + throwable.getMessage()));
        Assertions.assertEquals(user2.get("test").getUsername(), "test-加密后");
        Assertions.assertEquals(user2.get("test").getPassword(), "123456-加密后");
        Assertions.assertEquals(user2.get("test").getAge(), 18);
        Assertions.assertEquals(user2.get("test").getAddress().getCity(), "上海-加密后");
        Assertions.assertEquals(user2.get("test").getAddress().getCountry(), "中国-加密后");
        Assertions.assertEquals(user2.get("test").getAddress().getHeight(), 156);
        Assertions.assertEquals(user2.get("test").getStrMap().get("username"), "李渊-加密后");
        Assertions.assertEquals(user2.get("test").getAddressMap().get("address").getCity(), "上海1-加密后");
        Assertions.assertEquals(user2.get("test").getAddressMap().get("address").getCountry(), "中国1-加密后");
        Assertions.assertEquals(user2.get("test").getAddressMap().get("address").getHeight(), 1561);
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

    @Test
    public void arrayTest() {
        ArraySubEntity subEntity = new ArraySubEntity();
        subEntity.username = "窦建德";
        ArrayEntity entity = new ArrayEntity();
        entity.integers = new Integer[]{12, 56};
        entity.usernames = new String[]{"李世民", "王世充"};
        entity.arraySubEntity = new ArraySubEntity[]{subEntity};
        ArrayEntity entity1 = SecurityUtils.securityElseGet(entity, throwable -> System.out.println("异常" + throwable.getMessage()));
        Assertions.assertEquals(entity1.usernames[0], "李世民-加密后");
        Assertions.assertEquals(entity1.usernames[1], "王世充-加密后");
        Assertions.assertEquals(entity1.arraySubEntity[0].username, "窦建德-加密后");
    }
}
