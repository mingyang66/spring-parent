package com.emily.infrastructure.common;

import java.math.BigDecimal;

/**
 * 计算工具类
 *
 * @author :  Emily
 * @since :  2024/4/30 9:10 PM
 */
public class ComputeUtils {
    /**
     * 获取有效值
     * 1. 如果为空字符串，则返回原值
     * 2. 如果为数字，则返回原值
     * 3. 如果为小数，则返回原值; 3.120000 返回 3.12
     *
     * @param value 值
     * @return 有效值
     */
    public static String getEffectiveValue(String value) {
        if (StringUtils.isBlank(value)) {
            return value;
        }
        try {
            return Long.valueOf(value).toString();
        } catch (Exception e) {
            BigDecimal decimal = new BigDecimal(value);
            return decimal.stripTrailingZeros().toPlainString();
        }
    }
}
