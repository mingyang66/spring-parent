package com.emily.infrastructure.desensitize.test.entity;

import com.emily.infrastructure.desensitize.annotation.DesensitizeModel;
import com.emily.infrastructure.desensitize.annotation.DesensitizePluginProperty;
import com.emily.infrastructure.desensitize.test.plugin.ListDesensitizePlugin;
import com.emily.infrastructure.desensitize.test.plugin.StringDesensitizePlugin;

import java.util.List;

/**
 * @author :  Emily
 * @since :  2024/12/26 上午9:37
 */
@DesensitizeModel
public class PluginField {
    @DesensitizePluginProperty(value = StringDesensitizePlugin.class)
    private String name;
    @DesensitizePluginProperty(value = ListDesensitizePlugin.class)
    private List<String> stringList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }
}
