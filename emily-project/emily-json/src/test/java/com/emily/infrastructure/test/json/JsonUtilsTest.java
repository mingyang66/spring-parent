package com.emily.infrastructure.test.json;


import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.test.json.entity.Model;
import com.emily.infrastructure.test.json.entity.TimeEntity;
import com.emily.infrastructure.test.json.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

/**
 * json对象单元测试
 *
 * @author Emily
 * @since Created in 2023/5/28 7:26 PM
 */
public class JsonUtilsTest {
    @Test
    void time() {
        TimeEntity entity = new TimeEntity();
        entity.setLocalDateTime(LocalDateTime.of(2025, 11, 19, 12, 23, 26));
        entity.setDate(new Date());
        entity.setCalendar(Calendar.getInstance());
        System.out.println(JsonUtils.toJSONPrettyString(entity));
    }

    @Test
    void readeTreeTest() {
        Model model = new Model();
        model.username = "沃伯格";
        model.password = "特朗普";
        String str = JsonUtils.toJSONString(model);
        JsonNode jsonNode = JsonUtils.readTree(str);
        Assertions.assertEquals(jsonNode.get("username").asString(), model.username);
        Assertions.assertEquals(jsonNode.get("password").asString(), model.password);

        JsonNode jsonNode1 = JsonUtils.readTree(str.getBytes(StandardCharsets.UTF_8));
        Assertions.assertEquals(jsonNode1.get("username").asString(), model.username);
        Assertions.assertEquals(jsonNode1.get("password").asString(), model.password);
    }

    @Test
    void valueToTree() {
        User user = new User();
        user.setUsername("黄蓉");
        user.setPassword("123456");
        User.Like like = new User.Like();
        like.setName("郭靖");
        like.setHeight(180);
        user.setLike(like);
        user.setList(List.of(like));

        Map<String, User.Like> data = new HashMap<>(1);
        data.put("data", like);
        user.setDataMap(data);
        JsonNode jsonNode = JsonUtils.valueToTree(user);
        Assertions.assertEquals("黄蓉", jsonNode.get("username").asString());
        Assertions.assertEquals("123456", jsonNode.get("password").asString());
        Assertions.assertEquals(180, jsonNode.get("like").get("height").asInt());
        Assertions.assertEquals("郭靖", jsonNode.get("like").get("name").asString());
        Assertions.assertEquals(180, jsonNode.get("dataMap").get("data").get("height").asInt());
        Assertions.assertEquals("郭靖", jsonNode.get("dataMap").get("data").get("name").asString());
    }

    @Test
    void strToTree() {
        String str = "{\"username\":\"黄蓉\",\"password\":\"123456\",\"like\":{\"name\":\"郭靖\",\"height\":180},\"list\":[{\"name\":\"郭靖\",\"height\":180}],\"dataMap\":{\"data\":{\"name\":\"郭靖\",\"height\":180}}}";
        JsonNode jsonNode = JsonUtils.readTree(str);
        Assertions.assertEquals("黄蓉", jsonNode.get("username").asString());
        Assertions.assertEquals("123456", jsonNode.get("password").asString());
        Assertions.assertEquals(180, jsonNode.get("like").get("height").asInt());
        Assertions.assertEquals("郭靖", jsonNode.get("like").get("name").asString());
        Assertions.assertEquals(180, jsonNode.get("dataMap").get("data").get("height").asInt());
        Assertions.assertEquals("郭靖", jsonNode.get("dataMap").get("data").get("name").asString());
    }
}
