package com.emily.infrastructure.logger;

import com.emily.infrastructure.logger.configuration.type.LogbackType;
import com.emily.infrastructure.logger.init.LoggerContextInitializer;
import org.slf4j.Logger;

/**
 * 日志工具类 日志级别总共有TARCE &lt; DEBUG &lt; INFO &lt; WARN &lt; ERROR &lt; FATAL，且级别是逐渐提供，
 * 如果日志级别设置为INFO，则意味TRACE和DEBUG级别的日志都看不到。
 *
 * @author Emily
 * @since 20230722
 */
public class LoggerFactory {
    /**
     * 获取日志Logger对象
     * 日志路径格式：基础路径/filePath/日志级别info|error|debug/info.log
     *
     * @param clazz 类实例
     * @param <T>   参数实例类型
     * @return logger实例对象
     */
    public static <T> Logger getLogger(Class<T> clazz) {
        return org.slf4j.LoggerFactory.getLogger(clazz);
    }


    /**
     * 获取分组Logger日志对象
     * 日志路径格式：基础路径/filePath/日志级别info|error|debug/info.log
     *
     * @param clazz    类实例
     * @param filePath 分组日志路径
     * @param <T>      类类型
     * @return logger实例对象
     */
    public static <T> Logger getGroupLogger(Class<T> clazz, String filePath) {
        return LoggerContextInitializer.getContext().getLogger(clazz, filePath, null, LogbackType.GROUP);
    }

    /**
     * 获取模块Logger日志对象
     * 日志路径格式：基础路径/filePath/fileName.log
     *
     * @param clazz    类实例
     * @param filePath 模块日志路径
     * @param fileName 模块文件名文件名
     * @param <T>      类类型
     * @return logger实例对象
     */
    public static <T> Logger getModuleLogger(Class<T> clazz, String filePath, String fileName) {
        return LoggerContextInitializer.getContext().getLogger(clazz, filePath, fileName, LogbackType.MODULE);
    }
}
