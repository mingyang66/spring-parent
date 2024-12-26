package com.emily.sample.desensitize.plugin;

import com.emily.infrastructure.desensitize.DesensitizeType;
import com.emily.infrastructure.desensitize.plugin.DesensitizePlugin;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author :  Emily
 * @since :  2024/12/26 上午9:45
 */
@Component
public class ListDesensitizePlugin implements DesensitizePlugin<List<String>> {
    @Override
    public boolean support(Object value) {
        return value instanceof List<?>;
    }

    @Override
    public List<String> getPlugin(List<String> value, DesensitizeType desensitizeType) {
        System.out.println(desensitizeType.name());
        return List.of("**叶");
    }
}
