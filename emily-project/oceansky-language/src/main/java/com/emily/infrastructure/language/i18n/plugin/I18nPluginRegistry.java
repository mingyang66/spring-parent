package com.emily.infrastructure.language.i18n.plugin;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author :  Emily
 * @since :  2024/12/25 下午11:29
 */
@SuppressWarnings("all")
public class I18nPluginRegistry {

    private static final Map<String, I18nPlugin> plugins = new ConcurrentHashMap<>();

    public static I18nPlugin<Object> getPlugin(String pluginId) {
        if (ObjectUtils.isEmpty(pluginId)) {
            throw new IllegalArgumentException("pluginId is null");
        }
        return plugins.get(pluginId);
    }

    public static void registerPlugin(String pluginId, I18nPlugin<?> plugin) {
        if (StringUtils.isEmpty(pluginId) || ObjectUtils.isEmpty(plugin)) {
            return;
        }
        plugins.put(pluginId, plugin);
    }

    public static void registerPlugins(Map<String, I18nPlugin> i18nPlugins) {
        if (ObjectUtils.isNotEmpty(i18nPlugins)) {
            plugins.putAll(i18nPlugins);
        }
    }

    public static void unregisterPlugin(String pluginId) {
        plugins.remove(pluginId);
    }

    public static void unregisterAll() {
        plugins.clear();
    }

    public static boolean containsPlugin(String pluginId) {
        return StringUtils.isBlank(pluginId) ? false : plugins.containsKey(pluginId);
    }
}
