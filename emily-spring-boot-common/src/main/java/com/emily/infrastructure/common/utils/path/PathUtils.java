package com.emily.infrastructure.common.utils.path;

import com.emily.infrastructure.common.constant.CharacterInfo;
import org.springframework.util.StringUtils;

/**
 * @author Emily
 * @program: spring-parent
 * @description: 路径工具类
 * @create: 2020/11/26
 */
public class PathUtils {
    /**
     * @param path 路径
     * @return
     * @Description 规范化路径
     */
    public static String normalizePath(String path) {
        if (!StringUtils.hasText(path)) {
            return path;
        }
        String normalizedPath = path;
        if (!normalizedPath.startsWith(CharacterInfo.PATH_SEPARATOR)) {
            normalizedPath = CharacterInfo.PATH_SEPARATOR + normalizedPath;
        }
        if (normalizedPath.endsWith(CharacterInfo.PATH_SEPARATOR)) {
            normalizedPath = normalizedPath.substring(0, normalizedPath.length() - 1);
        }
        return normalizedPath;
    }
}
