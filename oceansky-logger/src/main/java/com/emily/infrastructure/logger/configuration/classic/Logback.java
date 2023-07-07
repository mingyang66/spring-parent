package com.emily.infrastructure.logger.configuration.classic;

import ch.qos.logback.classic.Logger;

/**
 * @program: spring-parent
 * @description: logback logger实例基础接口
 * @author: Emily
 * @create: 2021/12/12
 */
public interface Logback {
    /**
     * 获取Root Logger对象
     *
     * @return
     */
    default Logger getLogger() {
        return null;
    }

    /**
     * 获取Logger对象
     *
     * @param loggerName   logger属性名
     * @param appenderName appender属性名
     * @param filePath     文件路径
     * @param fileName     文件名
     * @return
     */
    default Logger getLogger(String loggerName, String appenderName, String filePath, String fileName) {
        return null;
    }
}
