package com.emily.sample.desensitize.plugin;

import com.emily.infrastructure.desensitize.DesensitizeType;
import com.emily.infrastructure.desensitize.plugin.DesensitizePlugin;
import org.springframework.stereotype.Component;

/**
 * @author :  Emily
 * @since :  2024/12/26 上午9:39
 */
@Component
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
