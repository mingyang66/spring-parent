package com.emily.infrastructure.logger.common;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.util.OptionHelper;

/**
 * @Description :  日志工具类
 * @Author :  Emily
 * @CreateDate :  Created in 2023/7/9 4:34 PM
 */
public class StrUtils {
    /**
     * 字符串拼接
     *
     * @param strings 字符串数组
     * @return 拼接后的字符串
     */
    public static String join(String... strings) {
        return String.join("", strings);
    }

    /**
     * 将字符串路径中的占位符替换为真实的路径
     *
     * @param str     字符串路径
     * @param context logback 上下文
     * @return 替换后的字符串
     */
    public static String substVars(Context context, String... str) {
        return OptionHelper.substVars(join(str), context);
    }
}
