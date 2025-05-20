package com.emily.infrastructure.desensitize.test.plugin;

import com.emily.infrastructure.desensitize.DesensitizeType;
import com.emily.infrastructure.desensitize.plugin.DesensitizePlugin;

/**
 * @author :  Emily
 * @since :  2024/12/26 上午9:39
 */
public class StringDesensitizePlugin implements DesensitizePlugin<String> {
    @Override
    public boolean support(Object value) {
        return value instanceof String;
    }

    @Override
    public String getPlugin(String value, DesensitizeType desensitizeType) {
        return "**叶";
    }
}
