package com.yaomy.sgrain.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 * @program: spring-parent
 * @description: 自动生成token工具类
 * @author: 姚明洋
 * @create: 2020/04/01
 */
public class TokenUtils {
    /**
     * 自动生成用户令牌
     */
    public static String generation(){
        return StringUtils.replace(UUID.randomUUID().toString(), "-", "");
    }
}
