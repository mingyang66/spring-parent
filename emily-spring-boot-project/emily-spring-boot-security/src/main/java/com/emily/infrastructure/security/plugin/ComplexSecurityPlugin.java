package com.emily.infrastructure.security.plugin;

/**
 * 解密插件
 *
 * @author :  Emily
 * @since :  2025/2/7 下午7:45
 */
public interface ComplexSecurityPlugin<Q, R> extends BasePlugin {
    /**
     * 获取字段加解密后的值
     *
     * @param entity 实体类对象
     * @param value  字段值
     * @return 加解密后的字段值
     * @throws Throwable 抛出异常
     */
    R getPlugin(Q entity, R value) throws Throwable;

    default int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
