package com.emily.infrastructure.logback.context;

import com.emily.infrastructure.common.utils.hash.Md5Utils;
import com.emily.infrastructure.common.utils.path.PathUtils;
import com.emily.infrastructure.logback.LogbackProperties;
import com.emily.infrastructure.logback.classic.Logback;
import com.emily.infrastructure.logback.classic.LogbackGroupImpl;
import com.emily.infrastructure.logback.classic.LogbackModuleImpl;
import com.emily.infrastructure.logback.classic.LogbackRootImpl;
import com.emily.infrastructure.logback.enumeration.LogbackType;
import org.slf4j.Logger;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Emily
 * @program: spring-parent
 * @description: 日志类 logback+slf4j
 * @create: 2020/08/04
 */
public class LogbackContext {

    /**
     * Logger对象容器
     */
    private static final Map<String, Logger> CONTEXT = new ConcurrentHashMap<>();

    private LogbackProperties properties;

    public LogbackContext(LogbackProperties properties) {
        this.properties = properties;
    }

    /**
     * 初始化日志Root对象
     */
    public void init() {
        new LogbackRootImpl(properties).getLogger();
    }

    /**
     * 获取日志输出对象
     *
     * @param fileName    日志文件名|模块名称
     * @param logbackType 日志类别 {@link com.emily.infrastructure.logback.enumeration.LogbackType}
     * @return
     */
    public <T> Logger getLogger(Class<T> clazz, String path, String fileName, LogbackType logbackType) {
        // 日志文件路径
        path = PathUtils.normalizePath(path);
        //logger对象name
        String appenderName = getAppenderName(clazz, path, fileName);
        Logger logger = CONTEXT.get(appenderName);
        if (Objects.nonNull(logger)) {
            return logger;
        }
        synchronized (this) {
            logger = CONTEXT.get(appenderName);
            if (Objects.nonNull(logger)) {
                return logger;
            }
            logger = builder(appenderName, path, fileName, logbackType);
            CONTEXT.put(appenderName, logger);
        }
        return logger;

    }


    /**
     * 构建Logger对象
     * 日志级别以及优先级排序: OFF > ERROR > WARN > INFO > DEBUG > TRACE >ALL
     *
     * @param fileName 日志文件名|模块名称
     * @return
     */
    protected Logger builder(String appenderName, String path, String fileName, LogbackType logbackType) {
        Logback logback;
        if (logbackType.getType().equals(LogbackType.MODULE.getType())) {
            logback = new LogbackModuleImpl(this.properties);
        } else {
            logback = new LogbackGroupImpl(this.properties);
        }
        return logback.getLogger(appenderName, path, fileName);
    }

    /**
     * 获取appenderName
     *
     * @param clazz    当前类实例
     * @param path     路径
     * @param fileName 文件名
     * @param <T>
     * @return appenderName
     */
    public <T> String getAppenderName(Class<T> clazz, String path, String fileName) {
        String prefix = Md5Utils.computeMd5Hash(MessageFormat.format("{0}{1}", path, fileName));
        return MessageFormat.format("{0}.{1}", prefix, clazz.getName());
    }

}
