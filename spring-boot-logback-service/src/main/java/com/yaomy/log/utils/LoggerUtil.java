package com.yaomy.log.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaomy.log.po.UserAction;
import org.slf4j.LoggerFactory;

/**
 * @Description: 日志工具类 日志级别总共有TARCE < DEBUG < INFO < WARN < ERROR < FATAL，且级别是逐渐提供，
 * 如果日志级别设置为INFO，则意味TRACE和DEBUG级别的日志都看不到。
 * @Version: 1.0
 */
public class LoggerUtil {

    public static <T> void info(Class<T> clazz, String msg){
        LoggerFactory.getLogger(clazz).info(msg);
    }

    public static <T> void warn(Class<T> clazz, String msg){
        LoggerFactory.getLogger(clazz).warn(msg);
    }

    public static <T> void debug(Class<T> clazz, String msg){
        LoggerFactory.getLogger(clazz).debug(msg);
    }

    public static <T> void error(Class<T> clazz, String msg){
        LoggerFactory.getLogger(clazz).error(msg);
    }

    public static void user(UserAction userAction){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            LoggerFactory.getLogger(LoggerUtil.class).trace(objectMapper.writeValueAsString(userAction));
        } catch (JsonProcessingException e){
            error(LoggerUtil.class, e.toString());
            System.out.println("----------");
        }
    }

}
