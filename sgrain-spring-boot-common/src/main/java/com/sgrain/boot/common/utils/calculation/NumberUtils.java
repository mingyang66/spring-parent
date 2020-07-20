package com.sgrain.boot.common.utils.calculation;

import com.sgrain.boot.common.enums.AppHttpStatus;
import com.sgrain.boot.common.exception.BusinessException;
import com.sgrain.boot.common.utils.constant.CharacterUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @program: spring-parent
 * @description: 数据计算工具类
 * @create: 2020/06/16
 */
public class NumberUtils {
    /**
     * 四舍五入，保留指定位数的小数
     *
     * @param number 数据
     * @param scale  小数位
     * @return
     */
    public static String rounding(String number, int scale) {
        try {
            if (scale < 0) {
                scale = 0;
            }
            return new BigDecimal(number).setScale(scale, BigDecimal.ROUND_HALF_UP).toString();
        } catch (Exception e) {
            throw new BusinessException(AppHttpStatus.DATA_CALCULATION_EXCEPTION.getStatus(), "数据计算异常");
        }
    }

    /**
     * 将数据转换为百分比,四舍五入，默认CharacterUtils.ZERO
     * CharacterUtils.ZERO-小数位不存在补零，默认值 如：0.12345(保留两位小数)->12.35%
     * CharacterUtils.HASH_SYMBOL 小数位不存在为空 如：0.12(保留两位小数)->12%
     *
     * @param number 数据
     * @param scale  精度（保留小数位）
     */
    public static String getPercent(String number, int scale, String... symbol) {
        if (StringUtils.isEmpty(number)) {
            return null;
        }
        String symbolStr = CharacterUtils.ZERO;
        if (symbol.length >= 1 && StringUtils.equals(symbol[0], CharacterUtils.HASH_SYMBOL)) {
            symbolStr = CharacterUtils.HASH_SYMBOL;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(symbolStr);
        for (int i = 0; i < scale; i++) {
            if (i == 0) {
                sb.append(CharacterUtils.POINT_SYMBOL);
            }
            sb.append(symbolStr);
        }
        sb.append(CharacterUtils.PERCENT_SIGIN);

        DecimalFormat format = new DecimalFormat(sb.toString());
        return format.format(Double.valueOf(number));
    }

    /**
     * 将数据转换为千分比,四舍五入，默认CharacterUtils.ZERO
     * CharacterUtils.ZERO-小数位不存在补零，默认值 如：0.123456(保留两位小数)->123.46%
     * CharacterUtils.HASH_SYMBOL 小数位不存在为空 如：0.12(保留两位小数)->120%
     *
     * @param number 数据
     * @param scale  精度（保留小数位）
     */
    public static String getThousand(String number, int scale, String... symbol) {
        if (StringUtils.isEmpty(number)) {
            return null;
        }
        String symbolStr = CharacterUtils.ZERO;
        if (symbol.length >= 1 && StringUtils.equals(symbol[0], CharacterUtils.HASH_SYMBOL)) {
            symbolStr = CharacterUtils.HASH_SYMBOL;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(symbolStr);
        for (int i = 0; i < scale; i++) {
            if (i == 0) {
                sb.append(CharacterUtils.POINT_SYMBOL);
            }
            sb.append(symbolStr);
        }
        sb.append("\u2030");

        DecimalFormat format = new DecimalFormat(sb.toString());
        return format.format(Double.valueOf(number));
    }
}
