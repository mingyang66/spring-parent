package com.emily.infrastructure.core.context.holder;

/**
 * @Description :  Api请求阶段枚举类
 * @Author :  Emily
 * @CreateDate :  Created in 2023/4/22 3:53 PM
 */
public enum StageType {
    //RequestMappingHandlerMapping校验转发阶段
    MAPPING,
    //Request请求AOP拦截阶段
    REQUEST,
    //Feign请求阶段
    FEIGN,
    //RestTemplate请求阶段
    HTTP,
    //Mybatis日志记录
    MYBATIS,
    //其它阶段
    OTHER;
}
