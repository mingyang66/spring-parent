package com.emily.infrastructure.common.sensitive;

import com.emily.infrastructure.common.constant.AttributeInfo;
import com.emily.infrastructure.common.entity.BaseResponse;
import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.common.object.JavaBeanUtils;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

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
     * @param entity 需要脱敏的实体类对象，如果是数据值类型则直接返回
     * @return
     * @Description 使用示例：
     * <pre>
     * @JsonSensitive(include = false)
     * public class JsonRequest {
     *     @NotEmpty
     *     @JsonSimField(SensitiveType.USERNAME)
     *     private String username;
     *     @JsonSimField
     *     private String password;
     *     @JsonSimField(SensitiveType.EMAIL)
     *     private String email;
     *     @JsonSimField(SensitiveType.ID_CARD)
     *     private String idCard;
     *     @JsonSimField(SensitiveType.BANK_CARD)
     *     private String bankCard;
     *     @JsonSimField(SensitiveType.PHONE)
     *     private String phone;
     *     @JsonSimField(SensitiveType.PHONE)
     *     private String mobile;
     * </pre>
     * 支持如下模式：
     * Map<String, JsonRequest></>
     * List<JsonRequest></>
     * JsonRequest[]
     * Map<String, Map<String, JsonRequest></>></>
     * 除上述外层包装，还支持实体类内部嵌套上述各种包装变体
     */
    public static Object acquire(final Object entity) {
        try {
            if (JavaBeanUtils.isFinal(entity)) {
                return entity;
            }
            if (entity instanceof Collection) {
                Collection coll = new ArrayList();
                for (Iterator it = ((Collection) entity).iterator(); it.hasNext(); ) {
                    coll.add(acquire(it.next()));
                }
                return coll;
            } else if (entity instanceof Map) {
                Map dMap = Maps.newHashMap();
                for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) entity).entrySet()) {
                    dMap.put(entry.getKey(), acquire(entry.getValue()));
                }
                return dMap;
            } else if (entity.getClass().isArray()) {
                if (entity.getClass().getComponentType().isPrimitive()) {
                    return entity;
                } else {
                    Object[] v = (Object[]) entity;
                    Object[] t = new Object[v.length];
                    for (int i = 0; i < v.length; i++) {
                        t[i] = acquire(v[i]);
                    }
                    return t;
                }
            } else if (entity instanceof BaseResponse) {
                BaseResponse response = (BaseResponse) entity;
                return new BaseResponse(response.getStatus(), response.getMessage(), acquire(response.getData()), response.getSpentTime());
            } else if (entity.getClass().isAnnotationPresent(JsonSensitive.class)) {
                return doSetField(entity);
            }
        } catch (IllegalAccessException exception) {
            logger.error(PrintExceptionInfo.printErrorInfo(exception));
        }
        return entity;
    }

    /**
     * 获取实体类对象脱敏后的对象
     *
     * @param entity 需要脱敏的实体类对象
     * @return
     */
    private static Map<String, Object> doSetField(final Object entity) throws IllegalAccessException {
        if (Objects.isNull(entity)) {
            return null;
        }
        Map<String, Object> fieldMap = Maps.newHashMap();
        //通用fieldKey fieldValue忽略
        Map<String, JsonFlexField> flexFieldMap = null;
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (JavaBeanUtils.isModifierFinal(field)) {
                continue;
            }
            field.setAccessible(true);
            String name = field.getName();
            Object value = field.get(entity);
            if (Objects.isNull(value)) {
                fieldMap.put(name, null);
                continue;
            }
            if (field.isAnnotationPresent(JsonSimField.class)) {
                if (value instanceof String) {
                    fieldMap.put(name, doGetProperty(field.getAnnotation(JsonSimField.class).value(), (String) value));
                } else {
                    fieldMap.put(name, doGetEntity(field, value));
                }
            } else if (field.isAnnotationPresent(JsonFlexField.class)) {
                if (value instanceof String) {
                    flexFieldMap = (flexFieldMap == null) ? Maps.newHashMap() : flexFieldMap;
                    flexFieldMap.put(name, field.getAnnotation(JsonFlexField.class));
                }
                fieldMap.put(name, value);
            } else if (value instanceof Collection) {
                Collection coll = new ArrayList();
                for (Iterator it = ((Collection) value).iterator(); it.hasNext(); ) {
                    coll.add(doGetEntity(field, it.next()));
                }
                fieldMap.put(name, coll);
            } else if (value instanceof Map) {
                Map dMap = Maps.newHashMap();
                for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) value).entrySet()) {
                    dMap.put(entry.getKey(), doGetEntity(field, entry.getValue()));
                }
                fieldMap.put(name, dMap);
            } else if (value.getClass().isArray()) {
                if (value.getClass().getComponentType().isPrimitive()) {
                    fieldMap.put(name, value);
                } else {
                    Object[] v = (Object[]) value;
                    Object[] t = new Object[v.length];
                    for (int i = 0; i < v.length; i++) {
                        t[i] = doGetEntity(field, v[i]);
                    }
                    fieldMap.put(name, t);
                }
            } else {
                fieldMap.put(name, doGetEntity(field, value));
            }
        }
        // 灵活复杂数据类型脱敏
        fieldMap.putAll(doGetFlexEntity(fieldMap, flexFieldMap));
        return fieldMap;
    }



    /**
     * 指定的修饰符是否序列化
     *
     * @param field 字段反射类型
     * @return
     */
    private static boolean isModifierFinal(final Field field) {
        int modifiers = field.getModifiers();
        if (checkModifierFinalStaticTransVol(modifiers) || checkModifierNativeSyncStrict(modifiers)) {
            return true;
        }
        return false;
    }

    private static boolean checkModifierNativeSyncStrict(int modifiers) {
        return Modifier.isNative(modifiers)
                || Modifier.isSynchronized(modifiers)
                || Modifier.isStrict(modifiers);
    }

    private static boolean checkModifierFinalStaticTransVol(int modifiers) {
        return Modifier.isFinal(modifiers)
                || Modifier.isStatic(modifiers)
                || Modifier.isTransient(modifiers)
                || Modifier.isVolatile(modifiers);
    }

    /**
     * 获取最终的字段值
     *
     * @param entity 字段值对象
     * @return
     */
    private static Object doGetEntity(final Field field, final Object entity) {
        if (Objects.isNull(entity)) {
            return null;
        }
        if (field.isAnnotationPresent(JsonSimField.class)) {
            if (entity instanceof String) {
                return doGetProperty(field.getAnnotation(JsonSimField.class).value(), (String) entity);
            } else {
                return acquire(entity);
            }
        } else {
            return acquire(entity);
        }
    }

    /**
     * 灵活复杂类型字段脱敏
     *
     * @param fieldMap     实体类字段值集合
     * @param flexFieldMap 复杂类型字段集合
     * @return
     */
    private static Map<String, Object> doGetFlexEntity(final Map<String, Object> fieldMap, final Map<String, JsonFlexField> flexFieldMap) {
        if (CollectionUtils.isEmpty(flexFieldMap)) {
            return Collections.emptyMap();
        }
        Map<String, Object> dataMap = Maps.newHashMap();
        for (Map.Entry<String, JsonFlexField> entry : flexFieldMap.entrySet()) {
            JsonFlexField jsonFlexField = entry.getValue();
            Object value = fieldMap.get(entry.getKey());
            if (Objects.isNull(value) || !(value instanceof String)) {
                continue;
            }
            for (int i = 0; i < jsonFlexField.fieldKeys().length; i++) {
                if (!StringUtils.equals(jsonFlexField.fieldKeys()[i], (String) value)) {
                    continue;
                }
                SensitiveType type;
                //如果A>B（等价于A-1>=B），则展示默认值
                if (i >= jsonFlexField.types().length) {
                    type = SensitiveType.DEFAULT;
                } else {
                    type = jsonFlexField.types()[i];
                }
                //获取值字段值
                Object fv = fieldMap.get(jsonFlexField.fieldValue());
                if (Objects.nonNull(fv) && (fv instanceof String)) {
                    dataMap.put(jsonFlexField.fieldValue(), doGetProperty(type, (String) fv));
                }
            }
        }
        return dataMap;
    }

    /**
     * 脱敏字段
     *
     * @param type       脱敏类型
     * @param fieldValue 字段值
     * @return
     */
    public static String doGetProperty(SensitiveType type, String fieldValue) {
        if (StringUtils.isBlank(fieldValue) || StringUtils.isEmpty(fieldValue)) {
            return fieldValue;
        }
        if (SensitiveType.PHONE.equals(type)) {
            return middle(fieldValue);
        } else if (SensitiveType.ID_CARD.equals(type)) {
            return middle(fieldValue);
        } else if (SensitiveType.BANK_CARD.equals(type)) {
            return middle(fieldValue);
        } else if (SensitiveType.EMAIL.equals(type)) {
            return email(fieldValue);
        } else if (SensitiveType.USERNAME.equals(type)) {
            return chineseName(fieldValue);
        } else {
            return AttributeInfo.PLACE_HOLDER;
        }
    }
}
