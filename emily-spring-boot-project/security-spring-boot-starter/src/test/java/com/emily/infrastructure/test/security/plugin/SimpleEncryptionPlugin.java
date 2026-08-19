package com.emily.infrastructure.test.security.plugin;


import com.emily.infrastructure.security.plugin.SimpleSecurityPlugin;

/**
 * @author :  Emily
 * @since :  2025/4/20 上午11:13
 */
public class SimpleEncryptionPlugin implements SimpleSecurityPlugin<String> {
    @Override
    public String getPlugin(String value) {
        return value + "-加密后";
    }
}
