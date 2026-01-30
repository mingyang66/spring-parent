package com.emily.infrastructure.logback.common;

import com.emily.infrastructure.logback.configuration.type.LogbackType;

import java.text.MessageFormat;
import java.util.Objects;

/**
 * 通用name工具方法
 *
 * @author :  Emily
 * @since :  2023/10/17 10:10 PM
 */
public class LogNameUtils {
    /**
     * 获取 logger name
     * 拼接规则：分组.路径.文件名（可能不存在）.类名（包括包名）
     *
     * @param logbackType 日志类型
     * @return logger name
     */
    public static String joinLogName(LogbackType logbackType, String filePath, String fileName, Class<?> requiredClass) {
        //拼装logger name
        return MessageFormat.format("{0}.{1}.{2}.{3}", logbackType, filePath, Objects.requireNonNullElse(fileName, StrUtils.EMPTY), requiredClass.getName())
                .replace(PathUtils.SLASH, PathUtils.DOT)
                .replace(StrUtils.join(PathUtils.DOT, PathUtils.DOT), PathUtils.DOT);
    }
}
