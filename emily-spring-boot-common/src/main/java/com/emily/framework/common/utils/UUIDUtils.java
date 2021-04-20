package com.emily.framework.common.utils;

import com.emily.framework.common.utils.constant.CharacterUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 * @program: spring-parent
 * @description: 自动生成token工具类
 * @create: 2020/04/01
 */
public class UUIDUtils {
    /**
     * 自动生成用户令牌
     */
    public static String generation(){
        return StringUtils.replace(randomUuid(), CharacterUtils.LINE_THROUGH_CENTER, "");
    }

    /**
     * 生成唯一标识
     * @return
     */
    public static String randomUuid(){
        return UUID.randomUUID().toString();
    }
}
