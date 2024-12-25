package com.emily.infrastructure.language.test.entity;

import com.emily.infrastructure.language.annotation.I18nModel;
import com.emily.infrastructure.language.annotation.I18nPluginProperty;
import com.emily.infrastructure.language.test.plugin.DefaultI18nPlugin;

import java.util.List;

/**
 * @author :  姚明洋
 * @since :  2024/12/25 下午11:48
 */
@I18nModel
public class PluginStudent {
    @I18nPluginProperty(value = DefaultI18nPlugin.class)
    private String username;
    @I18nPluginProperty(value = DefaultI18nPlugin.class)
    private List<String> list;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
