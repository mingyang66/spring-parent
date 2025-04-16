package com.emily.infrastructure.aop.constant;

/**
 * 定义优先级顺序
 *
 * @author Emily
 * @since 2020/3/23
 */
public class AopOrderInfo {
    /**
     * 加解密
     */
    public static final int Security = 300;
    /**
     * API请求切面
     */
    public static final int REQUEST = 400;
    /**
     * api脱敏请求切面
     */
    public static final int DESENSITIZE = 500;
    /**
     * 多语言翻译拦截器
     */
    public static final int I18N = 600;
    /**
     * 链路日志追踪
     */
    public static final int TRACING = 700;
    /**
     * feign正常日志
     */
    public static final int FEIGN = 800;
    /**
     * Mybatis日志漆面
     */
    public static final int MYBATIS = 850;
    /**
     * 数据源切面
     */
    public static final int DATASOURCE = 900;
    /**
     * RestTemplate请求超时设置拦截器
     */
    public static final int REST = 1000;
    /**
     * 限流切面拦截器
     */
    public static final int RATE_LIMITER = 1200;

}
