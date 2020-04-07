package com.yaomy.sgrain.common.enums;
/**
* @Description: 定义优先级顺序
* @Author: 姚明洋
* @create: 2020/3/23
*/
public enum SgrainAopOrderEnum {
    /**
     * 日志
     */
    LOG_AOP(400),
    /**
     * 接口流控
     */
    RATE_LIMITER(500),
    /**
     * 接口重复提交拦截器
     */
    REPEAT_SUBMIT(600);

    private int order;

    SgrainAopOrderEnum(int order){
        this.order = order;
    }

    public int getOrder() {
        return order;
    }
}
