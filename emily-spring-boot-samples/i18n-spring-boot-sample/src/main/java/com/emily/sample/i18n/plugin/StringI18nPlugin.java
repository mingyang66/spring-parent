package com.emily.sample.i18n.plugin;

import com.emily.infrastructure.language.i18n.LanguageType;
import com.emily.infrastructure.language.i18n.plugin.I18nPlugin;
import com.emily.sample.i18n.entity.PluginField;
import org.springframework.stereotype.Component;

/**
 * @author :  Emily
 * @since :  2024/12/25 下午5:05
 */
@Component
public class StringI18nPlugin implements I18nPlugin<PluginField, String> {
    @Override
    public boolean support(Object value) {
        return value instanceof String;
    }

    @Override
    public String getPlugin(PluginField entity, String value, LanguageType language) {
        return "xiaoxia tian";
    }
}
