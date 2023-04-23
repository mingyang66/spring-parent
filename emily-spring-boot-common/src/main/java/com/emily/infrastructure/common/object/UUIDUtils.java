package com.emily.infrastructure.common.object;

import com.emily.infrastructure.common.constant.CharacterInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 * @author Emily
 * @program: spring-parent
 * @description: 自动生成token工具类
 * @create: 2020/04/01
 */
public class UUIDUtils {
    /**
     * 生成简洁版UUID，即：删除横杠的UUID
     */
    public static String randomSimpleUUID() {
        return StringUtils.replace(randomUUID(), CharacterInfo.LINE_THROUGH_CENTER, "");
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
