package com.emily.infrastructure.common.enums;

/**
 * @author Emily
 * @Description: 定义优先级顺序
 * @create: 2020/3/23
 */
public enum AopOrderEnum {
    /**
     * API正常日志
     */
    API_LOG_NORMAL(400),
    /**
     * feign正常日志
     */
    FEIGN_LOG_NORMAL(800),
    /**
     * 数据源
     */
    MYBATIS_AOP(899),
    /**
     * 数据源
     */
    DATASOURCE_AOP(900);

    private int order;

    AopOrderEnum(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }
}
