package com.emily.infrastructure.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 计算工具类
 *
 * @author :  Emily
 * @since :  2024/4/30 9:10 PM
 */
public class ComputeUtils {
    /**
     * 获取有效值
     * 1. 如果为空字符串，则抛出异常
     * 2. 如果为数字，则返回原值
     * 3. 如果为小数，则返回原值; 3.120000 返回 3.12
     * 4. 如果为非数字，则抛出异常
     *
     * @param value 值
     * @return 有效值
     */
    public static String getEffectiveValue(String value) {
        return getEffectiveValue(value, null);
    }

    /**
     * 获取有效值
     * 1. 如果为空字符串，并且默认值为空，则抛出异常
     * 2. 如果为空字符串，并且默认值不为空，则返回默认值
     * 3. 如果为数字，则返回原值
     * 4. 如果为小数，则返回原值; 3.120000 返回 3.12
     * 5. 如果为非数字，并且默认值不为空，则返回默认值
     * 6. 如果为非数字，并且默认值为空，则抛出异常
     *
     * @param value        值
     * @param defaultValue 默认值
     * @return 有效值
     */
    public static String getEffectiveValue(String value, String defaultValue) {
        try {
            return Long.valueOf(value).toString();
        } catch (Exception e) {
            try {
                BigDecimal decimal = new BigDecimal(value);
                return decimal.stripTrailingZeros().toPlainString();
            } catch (Exception e1) {
                if (StringUtils.isBlank(defaultValue)) {
                    throw new IllegalArgumentException("非法参数");
                }
                return defaultValue;
            }
        }
    }

    /**
     * 保留小数
     *
     * @param value        原始值
     * @param newScale     保留小数位数
     * @param roundingMode 保留小数模式
     * @param defaultValue 默认值
     * @return 保留小数后的值
     */
    public static String round(String value, int newScale, RoundingMode roundingMode, String defaultValue) {
        try {
            BigDecimal decimal = new BigDecimal(value.trim());
            return decimal.setScale(newScale, roundingMode).toPlainString();
        } catch (Exception e) {
            if (StringUtils.isBlank(defaultValue)) {
                throw new IllegalArgumentException("非法参数");
            }
            return defaultValue;
        }
    }

    /**
     * 保留小数
     *
     * @param value        原始值
     * @param newScale     保留小数位数
     * @param defaultValue 默认值
     * @return 保留小数后的值
     */
    public static String round(String value, int newScale, String defaultValue) {
        return round(value, newScale, RoundingMode.HALF_UP, defaultValue);
    }

    /**
     * 保留小数
     *
     * @param value    原始值
     * @param newScale 保留小数位数
     * @return 保留小数后的值
     */
    public static String round(String value, int newScale) {
        return round(value, newScale, RoundingMode.HALF_UP, null);
    }

    /**
     * 百分比计算
     *
     * @param value        百分比计算原值
     * @param scale        保留小数位数
     * @param defaultValue 默认值
     * @return 百分比
     */
    public static String toPercentage(String value, int scale, String defaultValue) {
        try {
            return toPercentage(Double.parseDouble(value), scale);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 百分比计算
     *
     * @param value 百分比计算原值
     * @param scale 保留小数位数
     * @return 百分比
     */
    public static String toPercentage(double value, int scale) {
        return String.format(String.format("%s%s%s", "%.", scale, "f%%"), value * 100);
    }
}
