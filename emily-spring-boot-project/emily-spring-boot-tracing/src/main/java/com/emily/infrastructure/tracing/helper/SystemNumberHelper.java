package com.emily.infrastructure.tracing.helper;

/**
 * 系统编号帮助类
 *
 * @author Emily
 * @since 2021/11/27
 */
public class SystemNumberHelper {
    /**
     * 系统唯一标识
     */
    private static String systemNumber = null;

    /**
     * 获取系统标识
     *
     * @return 系统标识
     */
    public static String getSystemNumber() {
        return systemNumber;
    }

    public static void setSystemNumber(String systemNumber) {
        SystemNumberHelper.systemNumber = systemNumber;
    }
}
