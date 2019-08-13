package com.yaomy.log.utils;

import org.slf4j.LoggerFactory;

/**
 * @Description: 日志工具类 日志级别总共有TARCE < DEBUG < INFO < WARN < ERROR < FATAL，且级别是逐渐提供，
 * 如果日志级别设置为INFO，则意味TRACE和DEBUG级别的日志都看不到。
 * @ProjectName: EM.FrontEnd.PrivateEquity.electronic-contract
 * @Package: com.uufund.ecapi.utils.LogUtil
 * @Date: 2019/5/15 16:49
 * @Version: 1.0
 */
public class LoggerUtil {

    public static <T> void info(String msg){
        LoggerFactory.getLogger(LoggerUtil.class).info(msg);
    }

    public static <T> void info(Class<T> clazz, String msg){
        LoggerFactory.getLogger(clazz).info(msg);
    }

    public static <T> void warn(String msg){
        LoggerFactory.getLogger(LoggerUtil.class).warn(msg);
    }

    public static <T> void warn(Class<T> clazz, String msg){
        LoggerFactory.getLogger(clazz).warn(msg);
    }

    public static <T> void debug(String msg){
        LoggerFactory.getLogger(LoggerUtil.class).debug(msg);
    }

    public static <T> void debug(Class<T> clazz, String msg){
        LoggerFactory.getLogger(clazz).debug(msg);
    }

    public static <T> void error(Class<T> clazz, String msg){
        LoggerFactory.getLogger(clazz).error(msg);
    }
    public static <T> void error(String msg){
        LoggerFactory.getLogger(LoggerUtil.class).error(msg);
    }

}
