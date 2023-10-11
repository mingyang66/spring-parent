package com.emily.infrastructure.logger.configuration.context;

import ch.qos.logback.classic.LoggerContext;
import com.emily.infrastructure.logger.common.PathUtils;
import com.emily.infrastructure.logger.common.StrUtils;
import com.emily.infrastructure.logger.configuration.classic.AbstractLogback;
import com.emily.infrastructure.logger.configuration.classic.LogbackGroup;
import com.emily.infrastructure.logger.configuration.classic.LogbackModule;
import com.emily.infrastructure.logger.configuration.classic.LogbackRoot;
import com.emily.infrastructure.logger.configuration.filter.LogbackFilter;
import com.emily.infrastructure.logger.configuration.property.LogbackProperty;
import com.emily.infrastructure.logger.configuration.property.LogbackPropertyBuilder;
import com.emily.infrastructure.logger.configuration.property.LoggerProperties;
import com.emily.infrastructure.logger.configuration.type.LogbackType;
import com.emily.infrastructure.logger.manager.LoggerCacheManager;
import org.slf4j.Logger;

import java.text.MessageFormat;

/**
 * 日志类 logback+slf4j
 *
 * @author Emily
 * @since : 2020/08/04
 */
public class LogbackContext implements Context {
    private LoggerProperties properties;
    private LoggerContext loggerContext;

    /**
     * ------------------------------------
     * 1. 属性配置
     * 2. 报告状态展示控制；
     * 3. debug内部状态信息控制；
     * 4. packagingData异常堆栈拼接所属jar包控制
     * 5. 全局过滤器TurboFilter控制
     *
     * @param properties logback日志属性
     * @param context    上下文
     */
    @Override
    public void configure(LoggerProperties properties, LoggerContext context) {
        this.properties = properties;
        this.loggerContext = context;
        // 开启OnConsoleStatusListener监听器，即开启debug模式
        ConfigurationAction configuration = new ConfigurationAction(properties, context);
        configuration.start();
        //全局过滤器，接受指定标记的日志记录到文件中
        properties.getMarker().getAcceptMarker().forEach((marker) -> {
            context.addTurboFilter(LogbackFilter.getSingleton().getAcceptMarkerFilter(context, marker));
        });
        //全局过滤器，拒绝标记的日志记录到文件中
        properties.getMarker().getDenyMarker().forEach((marker) -> {
            context.addTurboFilter(LogbackFilter.getSingleton().getDenyMarkerFilter(context, marker));
        });
    }

    /**
     * 获取logger日志对象
     * 使用双重检查锁(Double-Checked Locking)的方式实现，如下示例：首先检查对象是否已经创建，如果没有，则进入synchronized块。在synchronized块内部再次检查
     * 是否已经被创建，以防止多个线程同时创建对象。如果对象任然为null,则创建对象并赋值给instance。
     * <pre>{@code
     * public class ObjectCreator {
     *     private static Object instance;
     *
     *     public static Object getInstance() {
     *         if (instance == null) {
     *             synchronized (ObjectCreator.class) {
     *                 if (instance == null) {
     *                     instance = new Object();
     *                 }
     *             }
     *         }
     *         return instance;
     *     }
     * }
     * }</pre>
     *
     * @param clazz       当前打印类实例
     * @param filePath    文件路径
     * @param fileName    文件名称
     * @param logbackType 日志类型
     * @param <T>         类类型
     * @return logger对象
     */
    @Override
    public <T> Logger getLogger(Class<T> clazz, String filePath, String fileName, LogbackType logbackType) {
        LogbackProperty property = LogbackPropertyBuilder.create()
                // 文件保存路径
                .withFilePath(PathUtils.normalizePath(filePath))
                // 文件名
                .withFileName(fileName)
                // 日志类型
                .withLogbackType(logbackType)
                .build();
        // 获取logger name
        String loggerName = getLoggerName(clazz, property);
        // 设置logger name
        property.setLoggerName(loggerName);
        // 获取Logger对象
        Logger logger = LoggerCacheManager.LOGGER.get(loggerName);
        if (logger == null) {
            synchronized (LogbackContext.class) {
                if (logger == null) {
                    // 获取logger日志对象
                    logger = getLogger(property);
                    // 存入缓存
                    LoggerCacheManager.LOGGER.put(loggerName, logger);
                } else {
                    logger = LoggerCacheManager.LOGGER.get(loggerName);
                }
            }
        }
        return logger;
    }


    /**
     * 构建Logger对象
     * 日志级别以及优先级排序: OFF > ERROR > WARN > INFO > DEBUG > TRACE >ALL
     *
     * @param property 属性配置上下文传递类
     */
    Logger getLogger(LogbackProperty property) {
        AbstractLogback logback;
        if (property.getLogbackType().equals(LogbackType.MODULE)) {
            logback = new LogbackModule(properties, loggerContext);
        } else if (property.getLogbackType().equals(LogbackType.GROUP)) {
            logback = new LogbackGroup(properties, loggerContext);
        } else {
            logback = new LogbackRoot(properties, loggerContext);
        }
        return logback.getLogger(property);
    }

    /**
     * 获取 logger name
     * 拼接规则：分组.路径.文件名（可能不存在）.类名（包括包名）
     *
     * @param clazz    当前类实例
     * @param property property属性名
     * @return logger name
     */
    <T> String getLoggerName(Class<T> clazz, LogbackProperty property) {
        if (property.getFileName() == null) {
            property.setFileName(StrUtils.EMPTY);
        }
        //拼装logger name
        return MessageFormat.format("{0}{1}.{2}.{3}", property.getLogbackType(), property.getFilePath(), property.getFileName(), clazz.getName())
                .replace(PathUtils.SLASH, PathUtils.DOT)
                .replace(StrUtils.join(PathUtils.DOT, PathUtils.DOT), PathUtils.DOT);
    }

    /**
     * 启动上下文，初始化root logger对象
     */
    @Override
    public void start() {
        // 初始化root logger
        LogbackProperty property = new LogbackPropertyBuilder()
                // logger name
                .withLoggerName(Logger.ROOT_LOGGER_NAME)
                // logger file path
                .withFilePath(PathUtils.normalizePath(properties.getRoot().getFilePath()))
                // logger type
                .withLogbackType(LogbackType.ROOT)
                .build();
        // 获取root logger对象
        Logger rootLogger = getLogger(property);
        // 将root添加到缓存
        LoggerCacheManager.LOGGER.put(Logger.ROOT_LOGGER_NAME, rootLogger);
    }

    /**
     * 此方法会清除掉所有的内部属性，内部状态消息除外，关闭所有的appender，移除所有的turboFilters过滤器，
     * 引发OnReset事件，移除所有的状态监听器，移除所有的上下文监听器（reset相关复位除外）
     */
    @Override
    public void stopAndReset() {
        loggerContext.stop();
        loggerContext.reset();
        LoggerCacheManager.LOGGER.clear();
        LoggerCacheManager.APPENDER.clear();
    }
}
