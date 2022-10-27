package com.emily.infrastructure.test.mainTest;

import com.emily.infrastructure.common.utils.json.JSONUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2022/01/17
 */
public class A {
    public static void main(String[] args) {

    }

    private static Object toObj(Object o, String... field) {
        if (Objects.isNull(o)) {
            return null;
        }
        if (o instanceof String) {
            String str = (String) o;
            if (StringUtils.isEmpty(str) || StringUtils.isBlank(str)) {
                return o;
            }
        }
        try {
            Map dataMap = JSONUtils.toJavaBean(JSONUtils.toJSONString(o), Map.class);
            Arrays.asList(field).stream().forEach(f -> {
                if (dataMap.containsKey(f)) {
                    dataMap.put(f, "----");
                }
            });
            return dataMap;
        } catch (Exception exception) {
            return o;
        }
    }
}
