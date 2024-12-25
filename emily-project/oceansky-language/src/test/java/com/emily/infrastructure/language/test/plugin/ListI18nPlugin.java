package com.emily.infrastructure.language.test.plugin;

import com.emily.infrastructure.language.convert.LanguageType;
import com.emily.infrastructure.language.plugin.I18nPlugin;

import java.util.List;

/**
 * @author :  姚明洋
 * @since :  2024/12/25 上午10:53
 */
public class ListI18nPlugin implements I18nPlugin<List<String>> {
    @Override
    public boolean support(Object value) {
        return value instanceof List;
    }

    @Override
    public List<String> getPlugin(List<String> value, LanguageType language) {
        return List.of("shaoan sun");
    }
}
