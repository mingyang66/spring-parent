package com.emily.infrastructure.logger;

import com.emily.infrastructure.logger.configuration.type.LogbackType;
import com.emily.infrastructure.logger.manager.LoggerContextManager;
import org.slf4j.Logger;

/**
 * @author Emily
 * @Description: 日志工具类 日志级别总共有TARCE < DEBUG < INFO < WARN < ERROR < FATAL，且级别是逐渐提供，
 * 如果日志级别设置为INFO，则意味TRACE和DEBUG级别的日志都看不到。
 * @Version: 1.0
 */
public class LoggerFactory {
    /**
     * 获取日志Logger对象
     *
     * @param clazz 类实例
     */
    public static <T> Logger getLogger(Class<T> clazz) {
        return org.slf4j.LoggerFactory.getLogger(clazz);
    }

    /**
     * 获取分组Logger日志对象
     *
     * @param clazz    类实例对象
     * @param filePath 日志文件路径
     * @param <T>
     * @return
     */
    public static <T> Logger getGroupLogger(Class<T> clazz, String filePath) {
        return getGroupLogger(clazz, filePath, null);
    }

    /**
     * 获取分组Logger日志对象
     *
     * @param clazz    类实例
     * @param filePath 日志文件对象
     * @param fileName 文件名
     * @param <T>
     * @return
     */
    public static <T> Logger getGroupLogger(Class<T> clazz, String filePath, String fileName) {
        return LoggerContextManager.getContext().getLogger(clazz, filePath, fileName, LogbackType.GROUP);
    }

    /**
     * 获取模块Logger日志对象
     *
     * @param clazz    类实例
     * @param filePath 文件路径
     * @param fileName 文件名
     * @param <T>
     * @return
     */
    public static <T> Logger getModuleLogger(Class<T> clazz, String filePath, String fileName) {
        return LoggerContextManager.getContext().getLogger(clazz, filePath, fileName, LogbackType.MODULE);
    }
}
