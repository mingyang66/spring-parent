package com.emily.infrastructure.common.sensitive;

import com.emily.infrastructure.common.constant.AttributeInfo;
import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @Description :  敏感信息工具类
 * @Author :  Emily
 * @CreateDate :  Created in 2022/7/19 3:13 下午
 */
public class SensitiveUtils {

    public static final Logger logger = LoggerFactory.getLogger(SensitiveUtils.class);

    /**
     * [中文姓名] 只显示第一个汉字，其他隐藏为2个星号<例子：李**>
     */
    public static String chineseName(final String fullName) {
        if (StringUtils.isBlank(fullName)) {
            return "";
        }
        final String name = StringUtils.left(fullName, 1);
        return StringUtils.rightPad(name, StringUtils.length(fullName), "*");
    }

    /**
     * [中文姓名] 只显示第一个汉字，其他隐藏为2个星号<例子：李**>
     */
    public static String chineseName(final String familyName, final String givenName) {
        if (StringUtils.isBlank(familyName) || StringUtils.isBlank(givenName)) {
            return "";
        }
        return chineseName(familyName + givenName);
    }

    /**
     * [身份证号] 显示最后四位，其他隐藏。共计18位或者15位。<例子：*************5762>
     */
    public static String idCardNum(final String id) {
        if (StringUtils.isBlank(id)) {
            return "";
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
            return "";
        }
        return StringUtils.leftPad(StringUtils.right(num, 4), StringUtils.length(num), "*");
    }

    /**
     * 字符串中间隐藏，分四份，中间两份隐藏
     *
     * @param middle
     * @return
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
            return "";
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
            return "";
        }
        final int length = StringUtils.length(address);
        return StringUtils.rightPad(StringUtils.left(address, length - sensitiveSize), length, "*");
    }

    /**
     * [电子邮箱] 邮箱前缀仅显示第一个字母，前缀其他隐藏，用星号代替，@及后面的地址显示<例子:g**@163.com>
     */
    public static String email(final String email) {
        if (StringUtils.isBlank(email)) {
            return "";
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
            return "";
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
            return "";
        }
        return StringUtils.rightPad(StringUtils.left(code, 2), StringUtils.length(code), "*");
    }

    /**
     * 获取实体类对象脱敏后的对象
     *
     * @param entity 需要脱敏的实体类对象
     * @return
     */
    public static Object sensitive(Object entity) {
        if (isFinal(entity)) {
            return entity;
        }
        if (entity instanceof List) {
            List list = new ArrayList();
            ((List) entity).stream().forEach(en -> {
                if (isFinal(en)) {
                    list.add(en);
                } else {
                    list.add(doSetField(en));
                }
            });
            return list;
        } else if (entity instanceof Map) {
            Map dMap = new HashMap();
            ((Map) entity).forEach((k, v) -> {
                if (isFinal(v)) {
                    dMap.put(k, v);
                } else {
                    dMap.put(k, doSetField(v));
                }
            });
            return dMap;
        }

        return doSetField(entity);
    }

    /**
     * 获取实体类对象脱敏后的对象
     *
     * @param entity 需要脱敏的实体类对象
     * @return
     */
    private static Map<String, Object> doSetField(Object entity) {
        Map<String, Object> dataMap = Maps.newHashMap();
        try {
            Field[] fields = entity.getClass().getDeclaredFields();
            for (Field field : fields) {
                int modifiers = field.getModifiers();
                if (Modifier.isFinal(modifiers)
                        || Modifier.isStatic(modifiers)
                        || Modifier.isTransient(modifiers)
                        || Modifier.isVolatile(modifiers)
                        || Modifier.isNative(modifiers)
                        || Modifier.isSynchronized(modifiers)
                        || Modifier.isStrict(modifiers)) {
                    continue;
                }
                field.setAccessible(true);
                String name = field.getName();
                Object value = field.get(entity);
                if (Objects.isNull(value)) {
                    dataMap.put(name, null);
                    continue;
                }
                if (field.isAnnotationPresent(JsonSensitive.class)) {
                    if (isFinal(value)) {
                        JsonSensitive sensitive = field.getAnnotation(JsonSensitive.class);
                        if (value instanceof String) {
                            dataMap.put(name, sensitiveField(sensitive, (String) value));
                        } else {
                            dataMap.put(name, value);
                        }
                    }
                } else {
                    if (isFinal(value)) {
                        dataMap.put(name, value);
                    } else {
                        dataMap.put(name, doSetField(value));
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(PrintExceptionInfo.printErrorInfo(ex));
        }
        return dataMap;
    }

    /**
     * 判定值对象是否是无需再继续进行解析
     *
     * @param value 值对象
     * @return
     */
    private static boolean isFinal(Object value) {
        if (Objects.isNull(value)) {
            return true;
        } else if (value instanceof String) {
            return true;
        } else if (value instanceof Integer) {
            return true;
        } else if (value instanceof Short) {
            return true;
        } else if (value instanceof Long) {
            return true;
        } else if (value instanceof Double) {
            return true;
        } else if (value instanceof Float) {
            return true;
        } else if (value instanceof Byte) {
            return true;
        } else if (value instanceof Boolean) {
            return true;
        } else if (value instanceof Character) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * 脱敏字段
     *
     * @param sensitive
     * @param fieldValue 字段值
     * @return
     */
    public static String sensitiveField(JsonSensitive sensitive, String fieldValue) {
        if (StringUtils.isBlank(fieldValue) || StringUtils.isEmpty(fieldValue)) {
            return fieldValue;
        }

        if (SensitiveType.PHONE.equals(sensitive.value())) {
            return middle(fieldValue);
        } else if (SensitiveType.ID_CARD.equals(sensitive.value())) {
            return middle(fieldValue);
        } else if (SensitiveType.BANK_CARD.equals(sensitive.value())) {
            return middle(fieldValue);
        } else if (SensitiveType.EMAIL.equals(sensitive.value())) {
            return email(fieldValue);
        } else if (SensitiveType.USERNAME.equals(sensitive.value())) {
            return chineseName(fieldValue);
        } else {
            return AttributeInfo.PLACE_HOLDER;
        }
    }
}
