package com.emily.infrastructure.desensitize.test;

import com.emily.infrastructure.desensitize.DesensitizeUtils;
import com.emily.infrastructure.desensitize.test.entity.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实体类对象脱敏单元测试
 *
 * @author Emily
 * @since :  Created in 2023/5/31 3:34 PM
 */
public class DeSensitizeUtilsTest {
    @Test
    public void testDesensitizeProperty() throws Throwable {
        People people = new People();
        people.setUsername("孙少平");
        people.setPassword("ssp");
        People p = DesensitizeUtils.acquire(people);
        Assertions.assertEquals(p, people);
        Assertions.assertEquals(p.getUsername(), "--隐藏--");
    }

    @Test
    public void testDesensitizePropertyList() throws Throwable {
        People people = new People();
        people.setStringList(List.of("田润叶"));
        People p = DesensitizeUtils.acquire(people);
        Assertions.assertEquals(p, people);
        Assertions.assertEquals(p.getStringList().get(0), "--隐藏--");
    }

    @Test
    public void testDesensitizePropertyMap() throws Throwable {
        People people = new People();
        people.setStringMap(new HashMap<>(Map.ofEntries(Map.entry("username", "田晓霞"))));
        People p = DesensitizeUtils.acquire(people);
        Assertions.assertEquals(p, people);
        Assertions.assertEquals(p.getStringMap().get("username"), "--隐藏--");
    }

    @Test
    public void testDesensitizePropertyArray() throws Throwable {
        People people = new People();
        people.setStringArrays(new String[]{"孙少安", "贺秀莲"});
        People p = DesensitizeUtils.acquire(people);
        Assertions.assertEquals(p, people);
        Assertions.assertEquals(p.getStringArrays()[0], "--隐藏--");
        Assertions.assertEquals(p.getStringArrays()[1], "--隐藏--");
    }


    @Test
    public void testDesensitizeNullProperty() throws Throwable {
        People people = new People();
        people.setKey("email");
        people.setValue("1563919868@qq.com");
        people.setStr("测试null");
        People s = DesensitizeUtils.acquire(people);
        Assertions.assertNull(s.getStr());
        Assertions.assertEquals(s.getAge(), 0);
        Assertions.assertEquals(s.getB(), (byte) 0);
        Assertions.assertEquals(s.getS(), (short) 0);
        Assertions.assertEquals(s.getL(), 0L);
    }

    @Test
    public void testDesensitizeMapProperty() throws Throwable {
        PeopleMap peopleMap = new PeopleMap();
        peopleMap.getParams().put("password", "12345");
        peopleMap.getParams().put("username", "田晓霞");
        peopleMap.getParams().put("phone", "15645562587");
        PeopleMap p = DesensitizeUtils.acquire(peopleMap);
        Assertions.assertEquals(p.getParams().get("password"), "--隐藏--");
        Assertions.assertEquals(p.getParams().get("phone"), "15645562587");
    }

    @Test
    public void testRemovePackClass() throws Throwable {
        PubResponse response = new PubResponse();
        response.password = "32433";
        response.username = "条消息";
        response.email = "1393619859@qq.com";
        response.idCard = "321455188625645686";
        response.bankCard = "325648956125656666";
        response.phone = "18254452658";
        response.mobile = "1234567";
        PubResponse.Job job = new PubResponse.Job();
        job.email = "1393619859@qq.com";
        job.work = "呵呵哈哈哈";
        response.job = job;
        response.jobs = new PubResponse.Job[]{job};
        response.jobList = List.of(job);
        BaseResponse<PubResponse> r = BaseResponse.<PubResponse>newBuilder().withData(response).build();
        BaseResponse<PubResponse> response1 = DesensitizeUtils.acquire(r);
        Assertions.assertEquals(response1.getData().email, "1393619859@qq.com");
        BaseResponse<PubResponse> response2 = DesensitizeUtils.acquire(r, BaseResponse.class);
        Assertions.assertEquals(response2.getData().email, "1***9@qq.com");
    }

    @Test
    public void testNestClass() throws Throwable {
        Company company = new Company();
        company.setId(123L);
        company.setName("尤五");
        company.setAddress("湖州");
        Company.Worker worker = new Company.Worker();
        worker.setId(456L);
        worker.setName("甥王爷");
        company.setWorker(worker);
        Company company1 = DesensitizeUtils.acquire(company, Company.class);
        Assertions.assertEquals(company1.getWorker().getName(), "--隐藏--");
    }

    @Test
    public void testNestListClass() throws Throwable {
        Company company = new Company();
        company.setId(123L);
        company.setName("尤五");
        company.setAddress("湖州");
        Company.Worker worker = new Company.Worker();
        worker.setId(456L);
        worker.setName("甥王爷");
        company.setList(List.of(worker));
        Company company1 = DesensitizeUtils.acquire(company, Company.class);
        Assertions.assertEquals(company1.getList().get(0).getName(), "--隐藏--");
    }

    @Test
    public void testNestMapClass() throws Throwable {
        Company company = new Company();
        company.setId(123L);
        company.setName("尤五");
        company.setAddress("湖州");
        Company.Worker worker = new Company.Worker();
        worker.setId(456L);
        worker.setName("甥王爷");
        company.setDataMap(new HashMap<>(Map.of("test", worker)));
        Company company1 = DesensitizeUtils.acquire(company, Company.class);
        Assertions.assertEquals(company1.getDataMap().get("test").getName(), "--隐藏--");
    }

    @Test
    public void testDesensitizeFlexibleProperty() throws Throwable {
        FlexibleField flexibleField = new FlexibleField();
        flexibleField.setKey1("username");
        flexibleField.setValue1("贺秀莲");
        flexibleField.setKey2("username");
        flexibleField.setValue2("贺秀莲");
        flexibleField.setKey3("email");
        flexibleField.setValue3("1564548965@qq.com");
        flexibleField.setKey4("email");
        flexibleField.setValue4(null);
        flexibleField.setKey5("email");
        flexibleField.setValue5("1564548965@qq.com");
        flexibleField.setKey6("address");
        flexibleField.setValue6("1564548965@qq.com");
        flexibleField.setKey7("email");
        flexibleField.setValue7("1564548965@qq.com");
        FlexibleField field = DesensitizeUtils.acquire(flexibleField);
        Assertions.assertEquals(field.getValue1(), "贺秀莲");
        Assertions.assertEquals(field.getValue2(), "贺秀莲");
        Assertions.assertEquals(field.getValue3(), "1564548965@qq.com");
        Assertions.assertNull(field.getValue4());
        Assertions.assertEquals(field.getValue5(), "1***5@qq.com");
        Assertions.assertEquals(field.getValue6(), "1564548965@qq.com");
        Assertions.assertEquals(field.getValue7(), "1***5@qq.com");
    }

    @Test
    public void testDesensitizePluginProperty() throws Throwable {
        PluginField field = new PluginField();
        field.setName("田润叶");
        PluginField pluginField = DesensitizeUtils.acquire(field);
        Assertions.assertEquals(pluginField.getName(), "**叶");
    }

    @Test
    public void testDesensitizePluginPropertyException() throws Throwable {
        PluginField field = new PluginField();
        field.setName("田润叶");
        field.setStringList(List.of("田润叶"));
        PluginField pluginField = DesensitizeUtils.acquire(field);
        Assertions.assertEquals(pluginField.getName(), "**叶");
    }
}
