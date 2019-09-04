package com.yaomy.control.common.control;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaomy.control.common.control.utils.JSONUtils;
import com.yaomy.control.test.HandlerBootStrap;
import com.yaomy.control.test.po.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Version: 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {HandlerBootStrap.class})
public class JsonTest {
    @Test
    public void json(){
        User user = new User();
        user.setDate(new Date());
        Map<String, Object> map = new HashMap<>();
        map.put("a", null);
        map.put("b", 3);
        map.put("c", new Date());
        map.put("d", System.currentTimeMillis());
        map.put("e", "");
        map.put("name", "lili");
        map.put("age", 12);
        map.put("date", new Date());
        String o = JSONUtils.toJSONPrettyString(map);
        System.out.println(o);
        Map user1 = JSONUtils.toJavaBean(o, Map.class);
        System.out.println(JSONUtils.toJSONString(user1));
    }
    @Test
    public void jsonList(){
        Map<String, Object> map = new HashMap<>();
        map.put("a", null);
        map.put("b", 3);
        map.put("c", new Date());
        map.put("d", System.currentTimeMillis());
        map.put("e", "");
        map.put("name", "  ");
        map.put("age", 12);
        map.put("date", new Date());

        List<Map<String, Object>> list = new ArrayList<>();
        list.add(map);
        String str = JSONUtils.toJSONString(list);
        System.out.println(str);
        List<Map<String, Object>> list1 = JSONUtils.toJavaBean(str, List.class);
        System.out.println(list1);
        System.out.println(JSONUtils.toJSONString(list1));
        System.out.println(JSONUtils.toJSONString("sfd"));

        User user = new User();
        user.setList(list);
        user.setDate(new Date());
        user.setName(null);
        user.setWeight(new String[]{"12","21"});

        String s = JSONUtils.toJSONPrettyString(user);
        System.out.println(s);
        Map<String, Object> map1 = JSONUtils.toJavaBean(s, Map.class);
        System.out.println(map1);
        System.out.println(JSONUtils.toJSONPrettyString(map1));
    }
    @Test
    public void testOptional(){
        Map<String, Object> map = new HashMap<>();
        map.put("a", null);
        map.put("b", 3);
        map.put("c", new Date());
        Optional<Map<String, Object>> objectMap = Optional.empty();
        map.put("d", objectMap);
        System.out.println(objectMap.isPresent());
        String s = JSONUtils.toJSONString(map, JsonInclude.Include.NON_ABSENT);
        System.out.println(s);
    }
    @Test
    public void testUser(){
        User user = new User();

        System.out.println(JSONUtils.toJSONString(user, JsonInclude.Include.NON_EMPTY));
    }
    @Test
    public void testFile(){
        Map<String, Object> map = new HashMap<>();
        map.put("a", null);
        map.put("b", 3);
        map.put("c", new Date());
        File file = new File("D:\\work\\workplace\\file.json");
        boolean flag = JSONUtils.writeToFile(file, map);
        System.out.println(flag);
    }
    @Test
    public void testWriteFile() throws IOException {
        JsonFactory jsonFactory = new JsonFactory();
        jsonFactory.setCodec(new ObjectMapper());
        File file = new File("D:\\work\\workplace\\file.json");
        JsonGenerator jsonGenerator = jsonFactory.createGenerator(file, JsonEncoding.UTF8);
        jsonGenerator.useDefaultPrettyPrinter();
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("name", "hhh");
        jsonGenerator.writeStringField("age", "12");
        jsonGenerator.writeEndObject();
        jsonGenerator.close();
    }
}
