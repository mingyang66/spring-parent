package com.emily.infrastructure.common.sensitive.enumeration;

/**
 * @Description :  脱敏策略
 * @Author :  Emily
 * @CreateDate :  Created in 2022/7/20 1:49 下午
 */
public enum Strategy {
    /**
     * 日志记录脱敏
     */
    LOGGER,
    /**
     * 实体类序列化脱敏
     */
    ENTITY;
}
