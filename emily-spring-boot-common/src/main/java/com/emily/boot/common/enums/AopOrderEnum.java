package com.emily.boot.common.enums;
/**
* @Description: 定义优先级顺序
* @create: 2020/3/23
*/
public enum AopOrderEnum {
    /**
     * API异常日志
     */
    API_LOG_EXCEPTION(300),
    /**
     * API正常日志
     */
    API_LOG_NORMAL(400),
    /**
     * 接口流控
     */
    RATE_LIMITER(500),
    /**
     * 接口重复提交拦截器
     */
    IDEMPOTENT(600);

    private int order;

    AopOrderEnum(int order){
        this.order = order;
    }

    public int getOrder() {
        return order;
    }
}
