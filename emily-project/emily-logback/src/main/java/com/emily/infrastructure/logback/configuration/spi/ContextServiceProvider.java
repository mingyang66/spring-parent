package com.emily.infrastructure.logback.configuration.spi;

import ch.qos.logback.classic.LoggerContext;
import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.common.LogNameUtils;
import com.emily.infrastructure.logback.common.LogPathField;
import com.emily.infrastructure.logback.common.PathUtils;
import com.emily.infrastructure.logback.configuration.appender.LogbackAsyncAppender;
import com.emily.infrastructure.logback.configuration.appender.LogbackConsoleAppender;
import com.emily.infrastructure.logback.configuration.appender.LogbackRollingFileAppender;
import com.emily.infrastructure.logback.configuration.classic.AbstractLogback;
import com.emily.infrastructure.logback.configuration.classic.LogbackGroup;
import com.emily.infrastructure.logback.configuration.classic.LogbackModule;
import com.emily.infrastructure.logback.configuration.classic.LogbackRoot;
import com.emily.infrastructure.logback.configuration.context.ConfigurationAction;
import com.emily.infrastructure.logback.configuration.encoder.LogbackConsoleLayoutEncoder;
import com.emily.infrastructure.logback.configuration.encoder.LogbackPatternLayoutEncoder;
import com.emily.infrastructure.logback.configuration.filter.LogAcceptMarkerFilter;
import com.emily.infrastructure.logback.configuration.filter.LogDenyMarkerFilter;
import com.emily.infrastructure.logback.configuration.filter.LogLevelFilter;
import com.emily.infrastructure.logback.configuration.filter.LogThresholdLevelFilter;
import com.emily.infrastructure.logback.configuration.policy.LogbackFixedWindowRollingPolicy;
import com.emily.infrastructure.logback.configuration.policy.LogbackSizeAndTimeBasedRollingPolicy;
import com.emily.infrastructure.logback.configuration.policy.LogbackTimeBasedRollingPolicy;
import com.emily.infrastructure.logback.configuration.type.LogbackType;
import com.emily.infrastructure.logback.factory.LogBeanFactory;
import org.slf4j.Logger;

/**
 * 日志类 logback+slf4j
 *
 * @author Emily
 * @since : 2020/08/04
 */
public class ContextServiceProvider implements ContextProvider {
    /**
     * ------------------------------------
     * 1. 属性配置
     * 2. 报告状态展示控制；
     * 3. debug内部状态信息控制；
     * 4. packagingData异常堆栈拼接所属jar包控制
     * 5. 全局过滤器TurboFilter控制
     *
     * @param context    上下文
     * @param properties logback日志属性
     */
    @Override
    public void initialize(LoggerContext context, LogbackProperties properties) {
        // 注册日志对象
        LogBeanFactory.registerBean(LogbackGroup.class.getSimpleName(), new LogbackGroup(context, properties));
        LogBeanFactory.registerBean(LogbackModule.class.getSimpleName(), new LogbackModule(context, properties));
        LogBeanFactory.registerBean(LogbackRoot.class.getSimpleName(), new LogbackRoot(context, properties));

        LogBeanFactory.registerBean(LogbackAsyncAppender.class.getSimpleName(), new LogbackAsyncAppender(context, properties));
        LogBeanFactory.registerBean(LogbackConsoleAppender.class.getSimpleName(), new LogbackConsoleAppender(context, properties));
        LogBeanFactory.registerBean(LogbackRollingFileAppender.class.getSimpleName(), new LogbackRollingFileAppender(context, properties));

        LogBeanFactory.registerBean(LogbackSizeAndTimeBasedRollingPolicy.class.getSimpleName(), new LogbackSizeAndTimeBasedRollingPolicy(context, properties));
        LogBeanFactory.registerBean(LogbackTimeBasedRollingPolicy.class.getSimpleName(), new LogbackTimeBasedRollingPolicy(context, properties));
        LogBeanFactory.registerBean(LogbackFixedWindowRollingPolicy.class.getSimpleName(), new LogbackFixedWindowRollingPolicy(context, properties));

        LogBeanFactory.registerBean(LogbackPatternLayoutEncoder.class.getSimpleName(), new LogbackPatternLayoutEncoder(context));
        LogBeanFactory.registerBean(LogbackConsoleLayoutEncoder.class.getSimpleName(), new LogbackConsoleLayoutEncoder(context));

        LogBeanFactory.registerBean(LogAcceptMarkerFilter.class.getSimpleName(), new LogAcceptMarkerFilter(context));
        LogBeanFactory.registerBean(LogDenyMarkerFilter.class.getSimpleName(), new LogDenyMarkerFilter(context));
        LogBeanFactory.registerBean(LogLevelFilter.class.getSimpleName(), new LogLevelFilter(context));
        LogBeanFactory.registerBean(LogThresholdLevelFilter.class.getSimpleName(), new LogThresholdLevelFilter(context));
        // 开启OnConsoleStatusListener监听器，即开启debug模式
        ConfigurationAction configuration = new ConfigurationAction(context, properties);
        configuration.start();
        //全局过滤器，接受指定标记的日志记录到文件中
        properties.getMarker().getAcceptMarker().forEach((marker) -> context.addTurboFilter(LogBeanFactory.getBean(LogAcceptMarkerFilter.class).getFilter(marker)));
        //全局过滤器，拒绝标记的日志记录到文件中
        properties.getMarker().getDenyMarker().forEach((marker) -> context.addTurboFilter(LogBeanFactory.getBean(LogDenyMarkerFilter.class).getFilter(marker)));
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
     * @param requiredType 当前打印类实例
     * @param filePath     文件路径
     * @param fileName     文件名称
     * @param logbackType  日志类型
     * @param <T>          类类型
     * @return logger对象
     */
    @Override
    public <T> Logger getLogger(Class<T> requiredType, String filePath, String fileName, LogbackType logbackType) {
        //通用参数
        LogPathField field = LogPathField.newBuilder()
                .withLoggerName(LogNameUtils.joinLogName(logbackType, filePath, fileName, requiredType))
                .withFilePath(PathUtils.normalizePath(filePath))
                .withFileName(fileName)
                .withLogbackType(logbackType)
                .build();
        // 获取Logger对象
        Logger logger = LogBeanFactory.getBean(field.getLoggerName());
        if (logger == null) {
            synchronized (ContextServiceProvider.class) {
                // 获取logger日志对象
                logger = LogBeanFactory.getBeans(AbstractLogback.class).stream().filter(l -> l.supports(logbackType)).findFirst().orElseThrow().getLogger(field);
                // 存入缓存
                LogBeanFactory.registerBean(field.getLoggerName(), logger);
            }
        }
        return logger;
    }

    /**
     * 启动上下文，初始化root logger对象
     */
    @Override
    public void start(LogbackProperties properties) {
        // 获取root logger对象
        Logger rootLogger = LogBeanFactory.getBeans(AbstractLogback.class).stream().filter(l -> l.supports(LogbackType.ROOT)).findFirst().orElseThrow().getLogger(LogPathField.newBuilder()
                // logger name
                .withLoggerName(LogNameUtils.joinLogName(LogbackType.ROOT, null, null, null))
                // logger file path
                .withFilePath(PathUtils.normalizePath(properties.getRoot().getFilePath()))
                // logger type
                .withLogbackType(LogbackType.ROOT)
                .build());
        // 将root添加到缓存
        LogBeanFactory.registerBean(Logger.ROOT_LOGGER_NAME, rootLogger);
    }

    /**
     * 此方法会清除掉所有的内部属性，内部状态消息除外，关闭所有的appender，移除所有的turboFilters过滤器，
     * 引发OnReset事件，移除所有的状态监听器，移除所有的上下文监听器（reset相关复位除外）
     */
    @Override
    public void stopAndReset(LoggerContext context) {
        context.stop();
        context.reset();
        LogBeanFactory.clear();
    }
}
