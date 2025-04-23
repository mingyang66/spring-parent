package com.emily.infrastructure.security.plugin;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于{@link I18nPluginProperty}注解的插件注册中心
 *
 * @author :  Emily
 * @since :  2024/12/25 下午11:29
 */
@SuppressWarnings("all")
public class SecurityPluginRegistry {

    private static final Map<String, BasePlugin> securityPluginMap = new ConcurrentHashMap<>();

    public static BasePlugin getSecurityPlugin(String pluginId) {
        if (Objects.isNull(pluginId)) {
            throw new IllegalArgumentException("pluginId is null");
        }
        return securityPluginMap.get(pluginId);
    }

    public static void registerSecurityPlugin(String pluginId, BasePlugin plugin) {
        if (Objects.isNull(pluginId) || Objects.isNull(plugin)) {
            return;
        }
        securityPluginMap.put(pluginId, plugin);
    }

    public static void registerSecurityPlugin(Map<String, ComplexSecurityPlugin> encryptionPlugins) {
        if (Objects.isNull(encryptionPlugins)) {
            securityPluginMap.putAll(encryptionPlugins);
        }
    }

    public static boolean containsPlugin(String pluginId) {
        return securityPluginMap.containsKey(pluginId);
    }
}
