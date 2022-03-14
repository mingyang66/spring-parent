package com.emily.infrastructure.common.utils.calculation;

import com.emily.infrastructure.common.constant.CharacterInfo;
import com.emily.infrastructure.common.enums.AppHttpStatus;
import com.emily.infrastructure.common.exception.BasicException;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @author Emily
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
            throw new BasicException(AppHttpStatus.ILLEGAL_PARAMETER.getStatus(), "数据计算异常");
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
        String symbolStr = CharacterInfo.ZERO;
        if (symbol.length >= 1 && StringUtils.equals(symbol[0], CharacterInfo.HASH_SYMBOL)) {
            symbolStr = CharacterInfo.HASH_SYMBOL;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(symbolStr);
        for (int i = 0; i < scale; i++) {
            if (i == 0) {
                sb.append(CharacterInfo.POINT_SYMBOL);
            }
            sb.append(symbolStr);
        }
        sb.append(CharacterInfo.PERCENT_SIGIN);

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
        String symbolStr = CharacterInfo.ZERO;
        if (symbol.length >= 1 && StringUtils.equals(symbol[0], CharacterInfo.HASH_SYMBOL)) {
            symbolStr = CharacterInfo.HASH_SYMBOL;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(symbolStr);
        for (int i = 0; i < scale; i++) {
            if (i == 0) {
                sb.append(CharacterInfo.POINT_SYMBOL);
            }
            sb.append(symbolStr);
        }
        sb.append("\u2030");

        DecimalFormat format = new DecimalFormat(sb.toString());
        return format.format(Double.valueOf(number));
    }
}
