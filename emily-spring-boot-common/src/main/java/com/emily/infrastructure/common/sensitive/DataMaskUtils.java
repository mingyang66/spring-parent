package com.emily.infrastructure.common.sensitive;

import com.emily.infrastructure.common.constant.AttributeInfo;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @Description :  数据隐藏
 * @Author :  Emily
 * @CreateDate :  Created in 2023/4/21 1:51 PM
 */
public class DataMaskUtils {
    /**
     * [中文姓名] 只显示第一个汉字，其他隐藏为2个星号<例子：李**>
     */
    public static String chineseName(final String fullName) {
        if (StringUtils.isBlank(fullName)) {
            return StringUtils.EMPTY;
        }
        final String name = StringUtils.left(fullName, 1);
        return StringUtils.rightPad(name, StringUtils.length(fullName), "*");
    }

    /**
     * [中文姓名] 只显示第一个汉字，其他隐藏为2个星号<例子：李**>
     */
    public static String chineseName(final String familyName, final String givenName) {
        if (StringUtils.isBlank(familyName) || StringUtils.isBlank(givenName)) {
            return StringUtils.EMPTY;
        }
        return chineseName(familyName + givenName);
    }

    /**
     * [身份证号] 显示最后四位，其他隐藏。共计18位或者15位。<例子：*************5762>
     */
    public static String idCardNum(final String id) {
        if (StringUtils.isBlank(id)) {
            return StringUtils.EMPTY;
        }

        return StringUtils.left(id, 3).concat(StringUtils
                .removeStart(StringUtils.leftPad(StringUtils.right(id, 3), StringUtils.length(id), "*"),
                        "***"));
    }

    /**
     * [固定电话] 后四位，其他隐藏<例子：****1234>
     */
    public static String fixedPhone(final String num) {
        if (StringUtils.isBlank(num)) {
            return StringUtils.EMPTY;
        }
        return StringUtils.leftPad(StringUtils.right(num, 4), StringUtils.length(num), "*");
    }

    /**
     * 字符串中间隐藏，分四份，中间两份隐藏
     *
     * @param middle 字符串
     * @return 影藏后的字符串
     */
    public static String middle(final String middle) {
        int length = new BigDecimal(middle.length()).divide(new BigDecimal(4), 0, RoundingMode.DOWN).intValue();
        String leftPad = StringUtils.substring(middle, 0, length);
        String rightPad = StringUtils.substring(middle, middle.length() - length);
        return StringUtils.rightPad(leftPad, middle.length() - 2 * length + length, "*").concat(rightPad);
    }

    /**
     * [手机号码] 前三位，后四位，其他隐藏<例子:138******1234>
     */
    public static String mobilePhone(final String num) {
        if (StringUtils.isBlank(num)) {
            return StringUtils.EMPTY;
        }
        return StringUtils.left(num, 2).concat(StringUtils
                .removeStart(StringUtils.leftPad(StringUtils.right(num, 2), StringUtils.length(num), "*"),
                        "***"));

    }

    /**
     * [地址] 只显示到地区，不显示详细地址；我们要对个人信息增强保护<例子：北京市海淀区****>
     *
     * @param sensitiveSize 敏感信息长度
     */
    public static String address(final String address, final int sensitiveSize) {
        if (StringUtils.isBlank(address)) {
            return StringUtils.EMPTY;
        }
        final int length = StringUtils.length(address);
        return StringUtils.rightPad(StringUtils.left(address, length - sensitiveSize), length, "*");
    }

    /**
     * [电子邮箱] 邮箱前缀仅显示第一个字母，前缀其他隐藏，用星号代替，@及后面的地址显示<例子:g**@163.com>
     */
    public static String email(final String email) {
        if (StringUtils.isBlank(email)) {
            return StringUtils.EMPTY;
        }
        final int index = StringUtils.indexOf(email, "@");
        if (index <= 1) {
            return email;
        } else {
            return StringUtils.rightPad(StringUtils.left(email, 1), index, "*")
                    .concat(StringUtils.mid(email, index, StringUtils.length(email)));
        }
    }

    /**
     * [银行卡号] 前六位，后四位，其他用星号隐藏每位1个星号<例子:6222600**********1234>
     */
    public static String bankCard(final String cardNum) {
        if (StringUtils.isBlank(cardNum)) {
            return StringUtils.EMPTY;
        }
        return StringUtils.left(cardNum, 6).concat(StringUtils.removeStart(
                StringUtils.leftPad(StringUtils.right(cardNum, 4), StringUtils.length(cardNum), "*"),
                "******"));
    }

    /**
     * [公司开户银行联号] 公司开户银行联行号,显示前两位，其他用星号隐藏，每位1个星号<例子:12********>
     */
    public static String bankCode(final String code) {
        if (StringUtils.isBlank(code)) {
            return StringUtils.EMPTY;
        }
        return StringUtils.rightPad(StringUtils.left(code, 2), StringUtils.length(code), "*");
    }

    /**
     * @param value 字段值
     * @param type  脱敏类型
     * @return 脱敏后的字段值
     */
    public static String doGetProperty(String value, SensitiveType type) {
        if (StringUtils.isBlank(value) || StringUtils.isEmpty(value)) {
            return value;
        }
        if (SensitiveType.PHONE.equals(type)) {
            return DataMaskUtils.middle(value);
        } else if (SensitiveType.ID_CARD.equals(type)) {
            return DataMaskUtils.middle(value);
        } else if (SensitiveType.BANK_CARD.equals(type)) {
            return DataMaskUtils.middle(value);
        } else if (SensitiveType.EMAIL.equals(type)) {
            return DataMaskUtils.email(value);
        } else if (SensitiveType.USERNAME.equals(type)) {
            return DataMaskUtils.chineseName(value);
        } else {
            return AttributeInfo.PLACE_HOLDER;
        }
    }
}
