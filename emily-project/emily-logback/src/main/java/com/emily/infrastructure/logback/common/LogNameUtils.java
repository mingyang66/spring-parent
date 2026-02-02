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
     * <p>
     * 构建标准化的日志记录器名称
     *
     * @param logbackType   日志类型枚举
     * @param filePath      文件路径（可为null）
     * @param fileName      文件名（可为null）
     * @param requiredClass 关联的类
     * @return 标准化后的日志记录器名称
     * @throws IllegalArgumentException 如果logbackType或requiredClass为null
     */
    public static String joinLogName(LogbackType logbackType, String filePath, String fileName, Class<?> requiredClass) {
        // 参数校验
        Objects.requireNonNull(logbackType, "LogbackType cannot be null");
        Objects.requireNonNull(requiredClass, "Required class cannot be null");
        // 处理空值
        String safeFilePath = Objects.requireNonNullElse(filePath, StrUtils.EMPTY);
        String safeFileName = Objects.requireNonNullElse(fileName, StrUtils.EMPTY);
        // 拼装logger name
        return MessageFormat.format("{0}.{1}.{2}.{3}",
                        logbackType,
                        safeFilePath,
                        safeFileName,
                        requiredClass.getName())
                .replace(PathUtils.SLASH, PathUtils.DOT)
                .replace(StrUtils.join(PathUtils.DOT, PathUtils.DOT), PathUtils.DOT);
    }
}
