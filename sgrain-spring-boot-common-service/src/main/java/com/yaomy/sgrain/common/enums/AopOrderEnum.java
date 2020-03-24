package com.yaomy.sgrain.common.enums;
/**
* @Description: 定义优先级顺序
* @Author: 姚明洋
* @create: 2020/3/23
*/
public enum AopOrderEnum {
    CONTROLLER_ADVICE(40),
    RATE_LIMITER(30);

    private int order;
    AopOrderEnum(int order){
        this.order = order;
    }

    public int getOrder() {
        return order;
    }
}
