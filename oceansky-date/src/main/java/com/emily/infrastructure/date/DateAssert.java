package com.emily.infrastructure.date;

/**
 * @Description :  日期断言判断类
 * @Author :  Emily
 * @CreateDate :  Created in 2023/5/28 9:29 AM
 */
public class DateAssert {
    /**
     * 判定字符串是否为null或者空串
     *
     * @param str     字符
     * @param message 异常描述信息
     */
    public static void illegalArgument(String str, String message) {
        if (str == null || str.length() == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 判定对象是否为null
     *
     * @param obj     对象
     * @param message 异常描述
     */
    public static void illegalArgument(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 判定字符串是否为null或者空串，如果是，则返回defaultValue,否则原值返回
     *
     * @param str          字符串对象
     * @param defaultValue 默认值
     * @return 结果值
     */
    public static String requireElseGet(String str, String defaultValue) {
        if (str == null || str.length() == 0) {
            return defaultValue;
        }
        return str;
    }

    /**
     * 判定对象是否为null，如果是则返回defaultValue，否则原值返回
     *
     * @param t            值对象
     * @param defaultValue 默认值
     * @param <T>          对象类型
     * @return 结果
     */
    public static <T> T requireElseGet(T t, T defaultValue) {
        if (t == null) {
            return defaultValue;
        }
        return t;
    }
}
