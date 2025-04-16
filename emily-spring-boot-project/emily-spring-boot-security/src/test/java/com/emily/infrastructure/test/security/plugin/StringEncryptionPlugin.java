package com.emily.infrastructure.test.security.plugin;

import com.emily.infrastructure.security.plugin.SecurityPlugin;

/**
 * @author :  姚明洋
 * @since :  2025/2/8 下午4:34
 */
public class StringEncryptionPlugin implements SecurityPlugin<String> {
    @Override
    public String getPlugin(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return value + "-加密后";
    }
}
