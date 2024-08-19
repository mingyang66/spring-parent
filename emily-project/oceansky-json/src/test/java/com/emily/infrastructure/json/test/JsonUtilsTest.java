package com.emily.infrastructure.json.test;


import com.emily.infrastructure.json.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

/**
 * json对象单元测试
 *
 * @author Emily
 * @since Created in 2023/5/28 7:26 PM
 */
public class JsonUtilsTest {
    @Test
    public void readeTreeTest() {
        Model model = new Model();
        model.username = "沃伯格";
        model.password = "特朗普";
        String str = JsonUtils.toJSONString(model);
        JsonNode jsonNode = JsonUtils.readTree(str);
        Assertions.assertEquals(jsonNode.get("username").asText(), model.username);
        Assertions.assertEquals(jsonNode.get("password").asText(), model.password);

        JsonNode jsonNode1 = JsonUtils.readTree(str.getBytes(StandardCharsets.UTF_8));
        Assertions.assertEquals(jsonNode1.get("username").asText(), model.username);
        Assertions.assertEquals(jsonNode1.get("password").asText(), model.password);
    }
}
