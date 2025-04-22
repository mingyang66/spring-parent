package com.emily.infrastructure.test.security.plugin;

import com.emily.infrastructure.security.plugin.SecurityPlugin;
import com.emily.infrastructure.test.security.entity.UserSimple;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author :  Emily
 * @since :  2025/2/8 下午4:34
 */
public class UserSimpleEncryptionPlugin implements SecurityPlugin<UserSimple, Object> {
    @Override
    public Object getPlugin(UserSimple entity, Object value) {
        if (value == null) {
            return value;
        }
        if (value instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) value;
            map.forEach((k, v) -> {
                map.put(k, v + "-加密后");
            });
            return map;
        }
        if(value instanceof List<?> list) {
            return list.stream().map(v->  v+"-加密后").collect(Collectors.toList());
        }
        return value + "-加密后";
    }
}
