package com.emily.infrastructure.logger.configuration.context;

import ch.qos.logback.classic.LoggerContext;
import com.emily.infrastructure.logger.common.CommonKeys;
import com.emily.infrastructure.logger.common.CommonNames;
import com.emily.infrastructure.logger.common.PathUtils;
import com.emily.infrastructure.logger.configuration.property.LoggerConfig;
import com.emily.infrastructure.logger.configuration.type.LogbackType;
import org.slf4j.Logger;

import static com.emily.infrastructure.logger.common.CommonCache.APPENDER;
import static com.emily.infrastructure.logger.common.CommonCache.LOGGER;

/**
 * 日志类 logback+slf4j
 *
 * @author Emily
 * @since : 2020/08/04
 */
public class LogbackContext implements Context {
    private LoggerConfig config;
    private LoggerContext lc;

    /**
     * ------------------------------------
     * 1. 属性配置
     * 2. 报告状态展示控制；
     * 3. debug内部状态信息控制；
     * 4. packagingData异常堆栈拼接所属jar包控制
     * 5. 全局过滤器TurboFilter控制
     *
     * @param config logback日志属性
     * @param lc     上下文
     */
    @Override
    public void configure(LoggerConfig config, LoggerContext lc) {
        this.config = config;
        this.lc = lc;
        // 注册日志对象
        LogbackBeanFactory.registerBean(config, lc);
        // 开启OnConsoleStatusListener监听器，即开启debug模式
        ConfigurationAction configuration = new ConfigurationAction(config, lc);
        configuration.start();
        //全局过滤器，接受指定标记的日志记录到文件中
        config.getMarker().getAcceptMarker().forEach((marker) -> {
            lc.addTurboFilter(LogbackBeanFactory.getFilter().getAcceptMarkerFilter(marker));
        });
        //全局过滤器，拒绝标记的日志记录到文件中
        config.getMarker().getDenyMarker().forEach((marker) -> {
            lc.addTurboFilter(LogbackBeanFactory.getFilter().getDenyMarkerFilter(marker));
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
        //通用参数
        CommonKeys commonKeys = CommonKeys.newBuilder()
                .withLoggerName(CommonNames.resolveLoggerName(logbackType, filePath, fileName, clazz))
                .withFilePath(PathUtils.normalizePath(filePath))
                .withFileName(fileName)
                .withLogbackType(logbackType)
                .build();
        // 获取Logger对象
        Logger logger = LOGGER.get(commonKeys.getLoggerName());
        if (logger == null) {
            synchronized (LogbackContext.class) {
                if (logger == null) {
                    // 获取logger日志对象
                    logger = LogbackBeanFactory.getLogger(commonKeys);
                    // 存入缓存
                    LOGGER.putIfAbsent(commonKeys.getLoggerName(), logger);
                } else {
                    logger = LOGGER.get(commonKeys.getLoggerName());
                }
            }
        }
        return logger;
    }

    /**
     * 启动上下文，初始化root logger对象
     */
    @Override
    public void start() {
        // 获取root logger对象
        Logger rootLogger = LogbackBeanFactory.getLogger(CommonKeys.newBuilder()
                // logger name
                .withLoggerName(CommonNames.resolveLoggerName(LogbackType.ROOT, null, null, null))
                // logger file path
                .withFilePath(PathUtils.normalizePath(config.getRoot().getFilePath()))
                // logger type
                .withLogbackType(LogbackType.ROOT)
                .build());
        // 将root添加到缓存
        LOGGER.put(Logger.ROOT_LOGGER_NAME, rootLogger);
    }

    /**
     * 此方法会清除掉所有的内部属性，内部状态消息除外，关闭所有的appender，移除所有的turboFilters过滤器，
     * 引发OnReset事件，移除所有的状态监听器，移除所有的上下文监听器（reset相关复位除外）
     */
    @Override
    public void stopAndReset() {
        lc.stop();
        lc.reset();
        LOGGER.clear();
        APPENDER.clear();
    }
}
