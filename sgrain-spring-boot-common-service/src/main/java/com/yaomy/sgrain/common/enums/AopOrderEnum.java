package com.yaomy.sgrain.common.enums;
/**
* @Description: 定义优先级顺序
* @Author: 姚明洋
* @create: 2020/3/23
*/
public enum AopOrderEnum {
    /**
     * 日志
     */
    CONTROLLER_ADVICE(40),
    /**
     * 接口流控
     */
    RATE_LIMITER(50);

    private int order;
    AopOrderEnum(int order){
        this.order = order;
    }

    public int getOrder() {
        return order;
    }
}
