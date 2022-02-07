package com.emily.infrastructure.logback.listener;

import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.configuration.context.LogbackContext;
import com.emily.infrastructure.logback.configuration.enumeration.LevelType;
import com.emily.infrastructure.logback.configuration.enumeration.RollingPolicyType;
import com.emily.infrastructure.logger.LoggerFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @program: spring-parent
 * @description: logback监听器
 * @author: Emily
 * @create: 2022/01/30
 */
public class LogbackApplicationListener implements GenericApplicationListener, Ordered {
    /**
     * 监听器支持的时间类型
     * ApplicationStartingEvent：在Spring最开始启动的时候触发
     * ApplicationEnvironmentPreparedEvent：在Spring已经准备好上下文但是上下文尚未创建的时候触发
     * ApplicationPreparedEvent：在Bean定义加载之后、刷新上下文之前触发
     * ApplicationStartedEvent：在刷新上下文之后、调用application命令之前触发
     * ApplicationReadyEvent：在调用application命令之后触发
     * ApplicationFailedEvent：在启动Spring发生异常时触发
     */
    private static final Class<?>[] SOURCE_TYPES = {ApplicationEnvironmentPreparedEvent.class};

    private static LogbackProperties properties;

    @Override
    public boolean supportsEventType(ResolvableType eventType) {
        return this.isAssignableFrom(eventType.getRawClass(), SOURCE_TYPES);
    }

    /**
     * 判定是否支持指定的时间类型
     *
     * @param type           事件类型
     * @param supportedTypes 支持的事件类型
     * @return
     */
    private boolean isAssignableFrom(Class<?> type, Class<?>... supportedTypes) {
        if (type != null) {
            Class[] var3 = supportedTypes;
            int var4 = supportedTypes.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                Class<?> supportedType = var3[var5];
                if (supportedType.isAssignableFrom(type)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationStartingEvent) {
            this.onApplicationStartingEvent((ApplicationStartingEvent) event);
        } else if (event instanceof ApplicationEnvironmentPreparedEvent) {
            this.onApplicationEnvironmentPreparedEvent((ApplicationEnvironmentPreparedEvent) event);
        }
    }

    private void onApplicationStartingEvent(ApplicationStartingEvent event) {

    }

    private void onApplicationEnvironmentPreparedEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();

        LogbackProperties properties = new LogbackProperties();
        //获取是否开启日志组件属性
        boolean enabled = environment.getProperty("spring.emily.logback.enabled", Boolean.class, true);
        //启动日志访问组件，默认：true
        properties.setEnabled(enabled);

        //日志文件存放路径，默认是:./logs
        properties.getAppender().setPath(environment.getProperty("spring.emily.logback.appender.path", "./logs"));
        //如果是 true，日志被追加到文件结尾，如果是 false，清空现存文件，默认是true
        properties.getAppender().setAppend(environment.getProperty("spring.emily.logback.appender.append", Boolean.class, true));
        //如果是 true，日志会被安全的写入文件，即使其他的FileAppender也在向此文件做写入操作，效率低，默认是 false|Support multiple-JVM writing to the same log file
        properties.getAppender().setPrudent(environment.getProperty("spring.emily.logback.appender.prudent", Boolean.class, false));
        //设置是否将输出流刷新，确保日志信息不丢失，默认：true
        properties.getAppender().setImmediateFlush(environment.getProperty("spring.emily.logback.appender.immediate-flush", Boolean.class, true));
        //是否报告内部状态信息，默认；false
        properties.getAppender().setReportState(environment.getProperty("spring.emily.logback.appender.report-state", Boolean.class, false));

        //是否开启基于文件大小和时间的SizeAndTimeBasedRollingPolicy归档策略
        //默认是基于TimeBasedRollingPolicy的时间归档策略，默认false
        properties.getAppender().getRollingPolicy().setType(environment.getProperty("spring.emily.logback.appender.rolling-policy.type", RollingPolicyType.class, RollingPolicyType.TIME_BASE));
        //设置要保留的最大存档文件数量，以异步方式删除旧文件,默认 7
        properties.getAppender().getRollingPolicy().setMaxHistory(environment.getProperty("spring.emily.logback.appender.rolling-policy.max-history", Integer.class, 7));
        //最大日志文件大小 KB、MB、GB，默认:500MB
        properties.getAppender().getRollingPolicy().setMaxFileSize(environment.getProperty("spring.emily.logback.appender.rolling-policy.max-file-size", "500MB"));
        //控制所有归档文件总大小 KB、MB、GB，默认:5GB
        properties.getAppender().getRollingPolicy().setTotalSizeCap(environment.getProperty("spring.emily.logback.appender.rolling-policy.total-size-cap", "5GB"));
        //设置重启服务后是否清除历史日志文件，默认：false
        properties.getAppender().getRollingPolicy().setCleanHistoryOnStart(environment.getProperty("spring.emily.logback.appender.rolling-policy.clean-history-on-start", Boolean.class, false));

        //是否开启异步记录Appender，默认false
        properties.getAppender().getAsync().setEnabled(environment.getProperty("spring.emily.logback.appender.async.enabled", Boolean.class, false));
        //队列的最大容量，默认为 256
        properties.getAppender().getAsync().setQueueSize(environment.getProperty("spring.emily.logback.appender.async.queue-size", Integer.class, 256));
        //默认，当队列还剩余 20% 的容量时，会丢弃级别为 TRACE, DEBUG 与 INFO 的日志，仅仅只保留 WARN 与 ERROR 级别的日志。想要保留所有的事件，可以设置为 0
        properties.getAppender().getAsync().setDiscardingThreshold(environment.getProperty("spring.emily.logback.appender.async.discarding-threshold", Integer.class, 0));
        /**
         *  根据所引用 appender 队列的深度以及延迟， AsyncAppender 可能会耗费长时间去刷新队列。
         *  当 LoggerContext 被停止时， AsyncAppender stop 方法会等待工作线程指定的时间来完成。
         *  使用 maxFlushTime 来指定最大的刷新时间，单位为毫秒。在指定时间内没有被处理完的事件将会被丢弃。这个属性的值的含义与 Thread.join(long)) 相同
         *  默认是 1000毫秒
         */
        properties.getAppender().getAsync().setMaxFlushTime(environment.getProperty("spring.emily.logback.appender.async.max-flush-time", Integer.class, 1000));
        //在队列满的时候 appender 会阻塞而不是丢弃信息。设置为 true，appender 不会阻塞你的应用而会将消息丢弃，默认为 false
        properties.getAppender().getAsync().setNeverBlock(environment.getProperty("spring.emily.logback.appender.async.never-block", Boolean.class, false));

        //日志级别,即该等级之上才会输出，ERROR > WARN > INFO > DEBUG > TRACE >ALL, 默认：INFO
        properties.getRoot().setLevel(environment.getProperty("spring.emily.logback.root.level", LevelType.class, LevelType.INFO));
        //通用日志输出格式，默认：[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%thread] [%-5level] [%-36.36logger{36}:%-4.4line] : %msg%n
        properties.getRoot().setPattern(environment.getProperty("spring.emily.logback.root.pattern", "[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%thread] [%-5level] [%-36.36logger{36}:%-4.4line] : %msg%n"));
        //基础日志文件路径,默认：""
        properties.getRoot().setFilePath(environment.getProperty("spring.emily.logback.root.file-path", ""));
        //是否将日志信息输出到控制台，默认：true
        properties.getRoot().setConsole(environment.getProperty("spring.emily.logback.root.console", Boolean.class, true));

        //日志级别,即该等级之上才会输出，ERROR > WARN > INFO > DEBUG > TRACE >ALL, 默认：INFO
        properties.getGroup().setLevel(environment.getProperty("spring.emily.logback.group.level", LevelType.class, LevelType.INFO));
        //模块日志输出格式，默认：%msg%n
        properties.getGroup().setPattern(environment.getProperty("spring.emily.logback.group.pattern", "%msg%n"));
        //是否将模块日志输出到控制台，默认：false
        properties.getGroup().setConsole(environment.getProperty("spring.emily.logback.group.console", Boolean.class, false));

        //日志级别,即该等级之上才会输出，ERROR > WARN > INFO > DEBUG > TRACE >ALL, 默认：INFO
        properties.getModule().setLevel(environment.getProperty("spring.emily.logback.module.level", LevelType.class, LevelType.INFO));
        //模块日志输出格式，默认：%msg%n
        properties.getModule().setPattern(environment.getProperty("spring.emily.logback.module.pattern", "%msg%n"));
        //是否将模块日志输出到控制台，默认：false
        properties.getModule().setConsole(environment.getProperty("spring.emily.logback.module.console", Boolean.class, false));

        if (enabled) {
            LoggerFactory.CONTEXT = new LogbackContext(properties);
            //LoggerFactory.CONTEXT.init();
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
