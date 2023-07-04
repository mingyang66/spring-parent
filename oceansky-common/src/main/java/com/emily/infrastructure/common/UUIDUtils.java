package com.emily.infrastructure.common;

import java.util.UUID;

/**
 * @author Emily
 * @program: spring-parent
 * @description: 自动生成token工具类
 * @create: 2020/04/01
 */
public class UUIDUtils {
    public static final String LINE = "-";

    /**
     * 生成简洁版UUID，即：删除横杠的UUID
     */
    public static String randomSimpleUUID() {
        return randomUUID().replace(LINE, "");
    }

    /**
     * 生成唯一标识
     *
     * @return
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

}
