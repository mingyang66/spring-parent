package com.emily.infrastructure.logback.configuration.classic;

import ch.qos.logback.classic.Logger;

/**
 * @program: spring-parent
 * @description:
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
     * @param appenderName
     * @param path
     * @param fileName
     * @return
     */
    default Logger getLogger(String appenderName, String path, String fileName) {
        return null;
    }
}
