package com.emily.infrastructure.common.constant;

/**
 * @author Emily
 * @Description: 定义优先级顺序
 * @create: 2020/3/23
 */
public class AopOrderInfo {
    /**
     * API请求切面
     */
    public static final int REQUEST = 400;
    /**
     * API请求拦截器
     */
    public static final int REQUEST_INTERCEPTOR = 410;
    /**
     * feign正常日志
     */
    public static final int FEIGN_LOG_NORMAL = 800;
    /**
     * Mybatis日志漆面
     */
    public static final int MYBATIS = 899;
    /**
     * 数据源切面
     */
    public static final int DATASOURCE = 900;
    /**
     * 数据库AOP切面拦截器
     */
    public static final int DATASOURCE_INTERCEPTOR = 910;

}
