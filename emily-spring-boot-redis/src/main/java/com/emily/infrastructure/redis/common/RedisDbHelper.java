package com.emily.infrastructure.redis.common;

import com.emily.infrastructure.core.constant.CharacterInfo;
import com.emily.infrastructure.core.exception.HttpStatusType;
import org.springframework.util.Assert;

/**
 * Redis操作帮助类
 *
 * @author Emily
 * @since Created in 2022/8/18 2:05 下午
 */
public class RedisDbHelper {

    /**
     * 获取Redis键key方法 A:B:C
     *
     * @param prefix key的开头
     * @param keys   可以指定多个key
     * @return redis建值
     */
    public static String getKey(String prefix, String... keys) {
        Assert.notNull(prefix, HttpStatusType.ILLEGAL_ARGUMENT.getMessage());
        StringBuffer sb = new StringBuffer(prefix);
        for (int i = 0; i < keys.length; i++) {
            sb.append(CharacterInfo.COLON_EN);
            sb.append(keys[i]);
        }
        return sb.toString();
    }

}
