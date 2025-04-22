package com.emily.infrastructure.language.test.plugin;

import com.emily.infrastructure.language.i18n.LanguageType;
import com.emily.infrastructure.language.i18n.plugin.I18nPlugin;
import com.emily.infrastructure.language.test.entity.PluginStudent;

/**
 * @author :  Emily
 * @since :  2024/12/25 上午10:53
 */
public class DefaultI18nPlugin implements I18nPlugin<PluginStudent, String> {
    @Override
    public boolean support(Object value) {
        return value instanceof String;
    }

    @Override
    public String getPlugin(PluginStudent student, String value, LanguageType languageType) {
        return "xiulian he";
    }
}
