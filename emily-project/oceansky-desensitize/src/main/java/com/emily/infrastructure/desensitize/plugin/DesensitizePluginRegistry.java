package com.emily.infrastructure.desensitize.plugin;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于{@link DesensitizePluginProperty}注解的插件注册中心
 *
 * @author :  Emily
 * @since :  2024/12/25 下午11:29
 */
@SuppressWarnings("all")
public class DesensitizePluginRegistry {

    private static final Map<String, DesensitizePlugin> plugins = new ConcurrentHashMap<>();

    public static DesensitizePlugin<Object> getPlugin(String pluginId) {
        if (ObjectUtils.isEmpty(pluginId)) {
            throw new IllegalArgumentException("pluginId is null");
        }
        return plugins.get(pluginId);
    }

    public static void registerPlugin(String pluginId, DesensitizePlugin<?> plugin) {
        if (StringUtils.isEmpty(pluginId) || ObjectUtils.isEmpty(plugin)) {
            return;
        }
        plugins.put(pluginId, plugin);
    }

    public static void registerPlugins(Map<String, DesensitizePlugin> i18nPlugins) {
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
