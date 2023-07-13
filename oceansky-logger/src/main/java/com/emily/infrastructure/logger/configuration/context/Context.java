package com.emily.infrastructure.logger.configuration.context;

import ch.qos.logback.classic.LoggerContext;
import com.emily.infrastructure.logger.configuration.property.LoggerProperties;
import com.emily.infrastructure.logger.configuration.type.LogbackType;
import org.slf4j.Logger;

/**
 * @Description :  自定义logback上下文
 * @Author :  Emily
 * @CreateDate :  Created in 2023/7/10 11:11 AM
 */
public interface Context {
    /**
     * 属性配置
     *
     * @param properties logback日志属性
     * @param context    上下文
     */
    void configure(LoggerProperties properties, LoggerContext context);

    /**
     * 获取logger日志对象
     *
     * @param clazz       当前打印类实例
     * @param filePath    文件路径
     * @param fileName    文件名称
     * @param logbackType 日志类型
     * @param <T>         类类型
     * @return logger对象
     */
    <T> Logger getLogger(Class<T> clazz, String filePath, String fileName, LogbackType logbackType);

    /**
     * 启动上下文，初始化root logger对象
     */
    void start();

    /**
     * 此方法会清除掉所有的内部属性，内部状态消息除外，关闭所有的appender，移除所有的turboFilters过滤器，
     * 引发OnReset事件，移除所有的状态监听器，移除所有的上下文监听器（reset相关复位除外）
     */
    void stopAndReset();
}
