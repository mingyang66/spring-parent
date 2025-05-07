package com.emily.infrastructure.security.plugin;

/**
 * 解密插件
 *
 * @author :  Emily
 * @since :  2025/2/7 下午7:45
 */
public interface ComplexSecurityPlugin<Q, R> extends BasePlugin {
    R getPlugin(Q entity, R value) throws Throwable;

    default int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
