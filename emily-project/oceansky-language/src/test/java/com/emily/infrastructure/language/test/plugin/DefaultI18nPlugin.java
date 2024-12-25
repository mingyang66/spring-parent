package com.emily.infrastructure.language.test.plugin;

import com.emily.infrastructure.language.convert.LanguageType;
import com.emily.infrastructure.language.plugin.I18nPlugin;

/**
 * @author :  姚明洋
 * @since :  2024/12/25 上午10:53
 */
public class DefaultI18nPlugin implements I18nPlugin<String> {
    @Override
    public boolean support(Object value) {
        return value instanceof String;
    }

    @Override
    public String getPlugin(String value, LanguageType languageType) {
        return "xiulian he";
    }
}
