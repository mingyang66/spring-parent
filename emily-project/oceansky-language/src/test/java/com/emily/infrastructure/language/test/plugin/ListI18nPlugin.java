package com.emily.infrastructure.language.test.plugin;

import com.emily.infrastructure.language.i18n.LanguageType;
import com.emily.infrastructure.language.i18n.plugin.I18nPlugin;

import java.util.List;

/**
 * @author :  Emily
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
