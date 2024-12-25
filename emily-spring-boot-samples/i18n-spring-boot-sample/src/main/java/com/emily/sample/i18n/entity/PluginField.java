package com.emily.sample.i18n.entity;

import com.emily.infrastructure.language.i18n.annotation.I18nModel;
import com.emily.infrastructure.language.i18n.annotation.I18nPluginProperty;
import com.emily.sample.i18n.plugin.ListI18nPlugin;
import com.emily.sample.i18n.plugin.StringI18nPlugin;

import java.util.List;

/**
 * @author :  Emily
 * @since :  2024/12/25 下午10:58
 */
@I18nModel
public class PluginField {
    @I18nPluginProperty(value = StringI18nPlugin.class)
    private String fieldName;
    @I18nPluginProperty(value = ListI18nPlugin.class)
    private List<String> stringList;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }
}
