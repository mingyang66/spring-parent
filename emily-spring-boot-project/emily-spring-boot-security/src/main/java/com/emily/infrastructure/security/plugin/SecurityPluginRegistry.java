package com.emily.infrastructure.security.plugin;

import com.emily.infrastructure.security.annotation.SecurityProperty;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于{@link SecurityProperty}注解的插件注册中心
 *
 * @author :  Emily
 * @since :  2024/12/25 下午11:29
 */
@SuppressWarnings("all")
public class SecurityPluginRegistry {

    private static final Map<String, BasePlugin> securityPluginMap = new ConcurrentHashMap<>();

    /**
     * 获取指定的插件
     *
     * @param pluginId 插件唯一标识
     * @return 插件对象
     */
    public static BasePlugin getSecurityPlugin(String pluginId) {
        if (Objects.isNull(pluginId)) {
            throw new IllegalArgumentException("pluginId is null");
        }
        return securityPluginMap.get(pluginId);
    }

    /**
     * 将指定插件注册到容器中
     *
     * @param pluginId 插件唯一标识
     * @param plugin   插件对象
     */
    public static void registerSecurityPlugin(String pluginId, BasePlugin plugin) {
        if (Objects.isNull(pluginId) || Objects.isNull(plugin)) {
            return;
        }
        securityPluginMap.put(pluginId, plugin);
    }

    /**
     * 将指定插件集合注册到容器中
     *
     * @param encryptionPlugins 插件集合
     */
    public static void registerSecurityPlugin(Map<String, BasePlugin> encryptionPlugins) {
        if (Objects.nonNull(encryptionPlugins)) {
            securityPluginMap.putAll(encryptionPlugins);
        }
    }

    /**
     * 判定容器中是否包含指定的插件
     *
     * @param pluginId 插件标识
     * @return true-包含，false-不包含
     */
    public static boolean containsPlugin(String pluginId) {
        return securityPluginMap.containsKey(pluginId);
    }
}
