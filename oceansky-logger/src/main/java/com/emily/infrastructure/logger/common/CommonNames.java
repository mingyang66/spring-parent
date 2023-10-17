package com.emily.infrastructure.logger.common;

import com.emily.infrastructure.logger.configuration.type.LogbackType;
import org.slf4j.Logger;

import java.text.MessageFormat;

/**
 * 通用name工具方法
 *
 * @author :  Emily
 * @since :  2023/10/17 10:10 PM
 */
public class CommonNames {
    /**
     * 获取 logger name
     * 拼接规则：分组.路径.文件名（可能不存在）.类名（包括包名）
     *
     * @param logbackType 日志类型
     * @return logger name
     */
    public static <T> String resolveLoggerName(LogbackType logbackType, String filePath, String fileName, Class<?> clazz) {
        if (logbackType.equals(LogbackType.ROOT)) {
            return Logger.ROOT_LOGGER_NAME;
        }
        if (fileName == null) {
            fileName = StrUtils.EMPTY;
        }
        //拼装logger name
        return MessageFormat.format("{0}{1}.{2}.{3}", logbackType, filePath, fileName, clazz.getName())
                .replace(PathUtils.SLASH, PathUtils.DOT)
                .replace(StrUtils.join(PathUtils.DOT, PathUtils.DOT), PathUtils.DOT);
    }
}
