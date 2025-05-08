package com.emily.sample.i18n.plugin;

import com.emily.infrastructure.language.i18n.LanguageType;
import com.emily.infrastructure.language.i18n.plugin.I18nPlugin;
import com.emily.sample.i18n.entity.PluginField;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author :  Emily
 * @since :  2024/12/25 下午5:09
 */
@Component
public class ListI18nPlugin implements I18nPlugin<PluginField, List<String>> {
    @Override
    public boolean support(Object value) {
        return value instanceof List<?>;
    }

    @Override
    public List<String> getPlugin(PluginField field, List<String> value, LanguageType language) {
        return List.of("shaoping sun");
    }
}
