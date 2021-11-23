package com.emily.infrastructure.common.constant;

/**
 * @author Emily
 * @Description: 定义优先级顺序
 * @create: 2020/3/23
 */
public class AopOrderInfo {
    /**
     * API正常日志
     */
    public static final int API_LOG_NORMAL = 400;
    /**
     * feign正常日志
     */
    public static final int FEIGN_LOG_NORMAL = 800;
    /**
     * 数据源
     */
    public static final int MYBATIS_AOP = 899;
    /**
     * 数据源
     */
    public static final int DATASOURCE = 900;
    /**
     * 数据库AOP切面拦截器
     */
    public static final int DATASOURCE_INTERCEPTOR = 910;

}
