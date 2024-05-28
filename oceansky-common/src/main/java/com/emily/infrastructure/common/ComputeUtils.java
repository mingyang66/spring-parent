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
        if (StringUtils.isBlank(value)) {
            return StringUtils.isBlank(defaultValue) ? value : defaultValue;
        }
        BigDecimal decimal = new BigDecimal(value.trim());
        return decimal.setScale(newScale, roundingMode).toPlainString();
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
