package com.emily.infrastructure.logback.initializer;

import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.configuration.context.LogbackContext;
import com.emily.infrastructure.logback.configuration.enumeration.LevelType;
import com.emily.infrastructure.logback.configuration.enumeration.RollingPolicyType;
import com.emily.infrastructure.logback.util.PropertyUtils;
import com.emily.infrastructure.logger.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

/**
 * @Description: Logback日志组件初始化类
 * @Author: Emily
 * @create: 2022/2/8
 * @since 4.0.7
 */
public class LogbackApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {
    /**
     * 初始化次数
     */
    private static int INITIAL_TIMES = 0;
    /**
     * cloud微服务最大初始化次数
     */
    private static int MAX_INITIAL_TIMES = 2;

    /**
     * 初始化优先级低于org.springframework.cloud.bootstrap.config.PropertySourceBootstrapConfiguration类
     *
     * @return 优先级
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 11;
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();

        LogbackProperties properties = new LogbackProperties();
        //是否开启日志组件属性，默认：true
        properties.setEnabled(environment.getProperty("spring.emily.logback.enabled", Boolean.class, properties.isEnabled()));

        //日志文件存放路径，默认是:./logs
        properties.getAppender().setPath(environment.getProperty("spring.emily.logback.appender.path", properties.getAppender().getPath()));
        //如果是 true，日志被追加到文件结尾，如果是 false，清空现存文件，默认是true
        properties.getAppender().setAppend(environment.getProperty("spring.emily.logback.appender.append", Boolean.class, properties.getAppender().isAppend()));
        //如果是 true，日志会被安全的写入文件，即使其他的FileAppender也在向此文件做写入操作，效率低，默认是 false|Support multiple-JVM writing to the same log file
        properties.getAppender().setPrudent(environment.getProperty("spring.emily.logback.appender.prudent", Boolean.class, properties.getAppender().isPrudent()));
        //设置是否将输出流刷新，确保日志信息不丢失，默认：true
        properties.getAppender().setImmediateFlush(environment.getProperty("spring.emily.logback.appender.immediate-flush", Boolean.class, properties.getAppender().isImmediateFlush()));
        //是否报告内部状态信息，默认；false
        properties.getAppender().setReportState(environment.getProperty("spring.emily.logback.appender.report-state", Boolean.class, properties.getAppender().isReportState()));

        //是否开启基于文件大小和时间的SizeAndTimeBasedRollingPolicy归档策略
        //默认是基于TimeBasedRollingPolicy的时间归档策略，默认false
        properties.getAppender().getRollingPolicy().setType(environment.getProperty("spring.emily.logback.appender.rolling-policy.type", RollingPolicyType.class, properties.getAppender().getRollingPolicy().getType()));
        //设置要保留的最大存档文件数量，以异步方式删除旧文件,默认 7
        properties.getAppender().getRollingPolicy().setMaxHistory(environment.getProperty("spring.emily.logback.appender.rolling-policy.max-history", Integer.class, properties.getAppender().getRollingPolicy().getMaxHistory()));
        //最大日志文件大小 KB、MB、GB，默认:500MB
        properties.getAppender().getRollingPolicy().setMaxFileSize(environment.getProperty("spring.emily.logback.appender.rolling-policy.max-file-size", properties.getAppender().getRollingPolicy().getMaxFileSize()));
        //控制所有归档文件总大小 KB、MB、GB，默认:5GB
        properties.getAppender().getRollingPolicy().setTotalSizeCap(environment.getProperty("spring.emily.logback.appender.rolling-policy.total-size-cap", properties.getAppender().getRollingPolicy().getTotalSizeCap()));
        //设置重启服务后是否清除历史日志文件，默认：false
        properties.getAppender().getRollingPolicy().setCleanHistoryOnStart(environment.getProperty("spring.emily.logback.appender.rolling-policy.clean-history-on-start", Boolean.class, properties.getAppender().getRollingPolicy().isCleanHistoryOnStart()));

        //是否开启异步记录Appender，默认false
        properties.getAppender().getAsync().setEnabled(environment.getProperty("spring.emily.logback.appender.async.enabled", Boolean.class, properties.getAppender().getAsync().isEnabled()));
        //队列的最大容量，默认为 256
        properties.getAppender().getAsync().setQueueSize(environment.getProperty("spring.emily.logback.appender.async.queue-size", Integer.class, properties.getAppender().getAsync().getQueueSize()));
        //默认，当队列还剩余 20% 的容量时，会丢弃级别为 TRACE, DEBUG 与 INFO 的日志，仅仅只保留 WARN 与 ERROR 级别的日志。想要保留所有的事件，可以设置为 0
        properties.getAppender().getAsync().setDiscardingThreshold(environment.getProperty("spring.emily.logback.appender.async.discarding-threshold", Integer.class, properties.getAppender().getAsync().getDiscardingThreshold()));
        /**
         *  根据所引用 appender 队列的深度以及延迟， AsyncAppender 可能会耗费长时间去刷新队列。
         *  当 LoggerContext 被停止时， AsyncAppender stop 方法会等待工作线程指定的时间来完成。
         *  使用 maxFlushTime 来指定最大的刷新时间，单位为毫秒。在指定时间内没有被处理完的事件将会被丢弃。这个属性的值的含义与 Thread.join(long)) 相同
         *  默认是 1000毫秒
         */
        properties.getAppender().getAsync().setMaxFlushTime(environment.getProperty("spring.emily.logback.appender.async.max-flush-time", Integer.class, properties.getAppender().getAsync().getMaxFlushTime()));
        //在队列满的时候 appender 会阻塞而不是丢弃信息。设置为 true，appender 不会阻塞你的应用而会将消息丢弃，默认为 false
        properties.getAppender().getAsync().setNeverBlock(environment.getProperty("spring.emily.logback.appender.async.never-block", Boolean.class, properties.getAppender().getAsync().isNeverBlock()));

        //日志级别,即该等级之上才会输出，ERROR > WARN > INFO > DEBUG > TRACE >ALL, 默认：INFO
        properties.getRoot().setLevel(environment.getProperty("spring.emily.logback.root.level", LevelType.class, properties.getRoot().getLevel()));
        //通用日志输出格式，默认：[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%thread] [%-5level] [%-36.36logger{36}:%-4.4line] : %msg%n
        properties.getRoot().setPattern(environment.getProperty("spring.emily.logback.root.pattern", properties.getRoot().getPattern()));
        //基础日志文件路径,默认：""
        properties.getRoot().setFilePath(environment.getProperty("spring.emily.logback.root.file-path", properties.getRoot().getFilePath()));
        //是否将日志信息输出到控制台，默认：true
        properties.getRoot().setConsole(environment.getProperty("spring.emily.logback.root.console", Boolean.class, properties.getRoot().isConsole()));

        //日志级别,即该等级之上才会输出，ERROR > WARN > INFO > DEBUG > TRACE >ALL, 默认：INFO
        properties.getGroup().setLevel(environment.getProperty("spring.emily.logback.group.level", LevelType.class, properties.getGroup().getLevel()));
        //模块日志输出格式，默认：%msg%n
        properties.getGroup().setPattern(environment.getProperty("spring.emily.logback.group.pattern", properties.getGroup().getPattern()));
        //是否将模块日志输出到控制台，默认：false
        properties.getGroup().setConsole(environment.getProperty("spring.emily.logback.group.console", Boolean.class, properties.getGroup().isConsole()));

        //日志级别,即该等级之上才会输出，ERROR > WARN > INFO > DEBUG > TRACE >ALL, 默认：INFO
        properties.getModule().setLevel(environment.getProperty("spring.emily.logback.module.level", LevelType.class, properties.getModule().getLevel()));
        //模块日志输出格式，默认：%msg%n
        properties.getModule().setPattern(environment.getProperty("spring.emily.logback.module.pattern", properties.getModule().getPattern()));
        //是否将模块日志输出到控制台，默认：false
        properties.getModule().setConsole(environment.getProperty("spring.emily.logback.module.console", Boolean.class, properties.getModule().isConsole()));

        //初始化日志组件
        initContext(environment, properties);
    }

    /**
     * 1.日志组件开启，微服务未开启 2.日志组件开启，微服务开启并且第二次初始化
     */
    private void initContext(Environment environment, LogbackProperties properties) {
        // 1.日志组件开启，微服务未开启 2.日志组件开启，微服务开启并且第二次初始化
        if (!properties.isEnabled()) {
            return;
        }
        //非微服务初始化
        if (!(PropertyUtils.bootstrapEnabled(environment) || PropertyUtils.useLegacyProcessing(environment))) {
            initLogbackFactory(properties);
            return;
        }
        //微服务初始化
        if (++INITIAL_TIMES == MAX_INITIAL_TIMES) {
            initLogbackFactory(properties);
        }
    }

    /**
     * 初始化日志组件；
     *
     * @param properties
     */
    private void initLogbackFactory(LogbackProperties properties) {
        LoggerFactory.CONTEXT = new LogbackContext(properties);
        LoggerFactory.CONTEXT.init();
    }
}
