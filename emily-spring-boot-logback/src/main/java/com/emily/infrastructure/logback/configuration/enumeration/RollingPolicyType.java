package com.emily.infrastructure.logback.configuration.enumeration;

/**
 * @Description: 日志归档策略
 * @Author: Emily
 * @create: 2022/1/4
 */
public enum RollingPolicyType {
    /**
     * 基于文件大小和时间的SizeAndTimeBasedRollingPolicy归档策略
     */
    SIZE_AND_TIME_BASED,
    /**
     * 基于时间的文件归档策略
     */
    TIME_BASE
}
