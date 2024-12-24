package com.emily.infrastructure.sensitize;

import java.util.Arrays;

/**
 * 数据脱敏
 *
 * @author Emily
 * @since : Created in 2023/4/21 1:51 PM
 */
public class DataMaskUtils {

    public static final String PLACE_HOLDER = "--隐藏--";

    /**
     * 中文姓名脱敏，第一个字符展示，其它隐藏
     * 示例：孙玉婷  孙**
     *
     * @param name 姓名
     * @return 脱敏后的姓名
     */
    public static String maskChineseName(final String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        if (name.length() == 1) {
            return name;
        }
        StringBuilder sb = new StringBuilder(name);
        for (int i = 1; i < name.length(); i++) {
            sb.setCharAt(i, '*');
        }
        return sb.toString();
    }

    /**
     * 身份证号，分15位和18位，展示前3后四位，其它位数将字符串分四份，展示前后两份，中间隐藏
     * 其它位数证件号码
     * 123                123
     * 1234               1**4
     * 12345              1**45
     * 18位身份证
     * 412822185703252531 412***********2531
     * 十五位身份证
     * 412822185703252    412********3252
     *
     * @param idCard 身份证
     * @return 脱敏后的身份证信息
     */
    public static String maskIdCard(final String idCard) {
        if (idCard == null || idCard.isEmpty()) {
            return idCard;
        }
        if (idCard.length() == 15) {
            return String.join("********", idCard.substring(0, 3), idCard.substring(11));
        } else if (idCard.length() == 18) {
            return String.join("***********", idCard.substring(0, 3), idCard.substring(14));
        } else {
            return maskMiddleTwoPortions(idCard);
        }
    }

    /**
     * 手机号脱敏
     * 1. 11位手机号，取前三后四中间脱敏；如：18221120687  182*****0687
     * 2. 3位区号+11位手机号，区号+取前三后四中间脱敏；如：08518221120687  085182*****0687
     * 3. 4位区号+11位手机号，区号+取前三后四中间脱敏；如：008618221120687 0086182*****0687
     * 4. 手机号长度大于等于4时，取四等份中间两份隐藏，首尾两份展示
     * 5. 长度小于4，则原样展示
     *
     * @param phone 手机号码
     * @return 脱敏后的手机号
     */
    public static String maskPhoneNumber(final String phone) {
        if (phone == null || phone.isEmpty()) {
            return phone;
        }
        if (phone.length() == 11) {
            return String.join("*****", phone.substring(0, 3), phone.substring(7));
        } else if (phone.length() == 14) {
            return String.join("", phone.substring(0, 3), maskPhoneNumber(phone.substring(3)));
        } else if (phone.length() == 15) {
            return String.join("", phone.substring(0, 4), maskPhoneNumber(phone.substring(4)));
        } else if (phone.length() >= 4) {
            return maskMiddleTwoPortions(phone);
        }
        return phone;
    }

    /**
     * 将字符串四等分，中间两份隐藏，收尾展示明文
     * 实例如下：
     * 123           123
     * 1234          1**4
     * 12345         1**45
     * 123456        1**456
     * 1234567       1**4567
     * 12345678      12****78
     * 123456789     12****789
     * 1234567890    12****7890
     *
     * @param str 待脱敏字符串
     * @return 脱敏后的字符串
     */
    public static String maskMiddleTwoPortions(final String str) {
        // 平均四份，每份的长度
        int quarter = str.length() / 4;
        // 第一段字符串
        String before = str.substring(0, quarter);
        // 最后一段字符串
        String after = str.substring(quarter * 3);
        // 中间字符串的长度
        int middle = str.length() - before.length() - after.length();
        // 中间字符串拼接为*号
        return String.join("*".repeat(Math.max(0, middle)), before, after);
    }


    /**
     * 地址脱敏，只显示地址，不展示详细信息
     * len&lt;=0，则按照字符串长度展示前三分之一，其它隐藏
     * len&gt;0，则按照指定的长度展示字符串，其它隐藏
     *
     * @param address 地址
     * @param len     长度
     * @return 脱敏后的地址信息
     */
    public static String maskAddress(final String address, int len) {
        if (address == null || address.isEmpty()) {
            return address;
        }
        if (address.length() < 3) {
            return address;
        }
        if (len <= 0) {
            len = address.length() / 3;
        }
        return address.substring(0, len) +
                "*".repeat(Math.max(0, address.length() - len));
    }

    /**
     * 邮箱脱敏，实例如下：
     * 123                123
     * <p>
     * * @qq.com @qq.com
     * * 1@qq.com           1***@qq.com
     * * 12@qq.com          1***2@qq.com
     * * 123@qq.com         1***3@qq.com
     * * 1234@qq.com        1***4@qq.com
     * * 12345@qq.com       1***5@qq.com
     * * 123456@qq.com      1***6@qq.com
     * * 1234567@qq.com     1***7@qq.com
     * * 12345678@qq.com    1***8@qq.com
     * * 123456789@qq.com   1***9@qq.com
     * * 1234567890@qq.com  1***0@qq.com
     *
     * @param email 邮箱
     * @return 脱敏后的邮箱
     */
    public static String maskEmail(final String email) {
        if (email == null || email.isEmpty()) {
            return email;
        }
        int index = email.indexOf('@');
        if (index < 1) {
            return email;
        }
        String username = email.substring(0, index);
        String domain = email.substring(index);
        int len = username.length();
        if (len < 2) {
            return String.join("***", username, domain);
        }
        String firstChar = username.substring(0, 1);
        String lastChar = username.substring(len - 1);
        return String.join("", Arrays.asList(firstChar, "***", lastChar, domain));
    }

    /**
     * 银行卡号隐藏，大于等于10位，前6后4位展示，其它隐藏，小于等于10位则按照等分四份，中间两份隐藏，首尾两份展示
     * 示例如下：
     * 3位  123                   123
     * 9位  123456789             12****789
     * 10位 1234567890            12****7890
     * 12位 123456789012          123456**9012
     * 16位 1234567890123456      123456******3456
     * 19位 62270010000000000000  622700**********0000
     *
     * @param cardNo 卡号
     * @return 脱敏后的银行卡号
     */
    public static String maskBankCard(final String cardNo) {
        if (cardNo == null || cardNo.isEmpty()) {
            return cardNo;
        }
        if (cardNo.length() <= 10) {
            return maskMiddleTwoPortions(cardNo);
        }
        return cardNo.substring(0, 6) +
                "*".repeat(cardNo.length() - 10) +
                cardNo.substring(cardNo.length() - 4);
    }

    /**
     * @param value           字段值
     * @param desensitizeType 脱敏类型
     * @return 脱敏后的字段值
     */
    public static String doGetProperty(String value, DesensitizeType desensitizeType) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        if (desensitizeType == null) {
            desensitizeType = DesensitizeType.DEFAULT;
        }
        return switch (desensitizeType) {
            case PHONE -> DataMaskUtils.maskPhoneNumber(value);
            case ID_CARD -> DataMaskUtils.maskIdCard(value);
            case BANK_CARD -> DataMaskUtils.maskBankCard(value);
            case EMAIL -> DataMaskUtils.maskEmail(value);
            case USERNAME -> DataMaskUtils.maskChineseName(value);
            case ADDRESS -> DataMaskUtils.maskAddress(value, 0);
            default -> PLACE_HOLDER;
        };
    }
}
