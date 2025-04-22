package com.emily.infrastructure.test.security.plugin;

import com.emily.infrastructure.security.plugin.SecurityPlugin;
import com.emily.infrastructure.test.security.entity.UserSimple;

/**
 * @author :  Emily
 * @since :  2025/2/8 下午4:34
 */
public class UserSimpleEncryptionPlugin implements SecurityPlugin<UserSimple, String> {
    @Override
    public String getPlugin(UserSimple entity, String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return value + "-加密后";
    }
}
