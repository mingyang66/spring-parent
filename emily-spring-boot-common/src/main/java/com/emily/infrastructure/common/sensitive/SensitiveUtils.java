package com.emily.infrastructure.common.sensitive;

import com.emily.infrastructure.common.constant.AttributeInfo;
import com.emily.infrastructure.common.entity.BaseResponse;
import com.emily.infrastructure.common.exception.PrintExceptionInfo;
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
    public static Object sensitive(final Object entity) {
        return sensitive(entity, null);
    }

    /**
     * 获取实体类对象脱敏后的对象
     *
     * @param entity  需要脱敏的实体类对象
     * @param include 是否脱敏嵌套类，默认：null
     * @return
     */
    private static Object sensitive(final Object entity, final Boolean include) {
        if (isFinal(entity)) {
            return entity;
        }
        if (entity instanceof Collection) {
            Collection coll = new ArrayList();
            ((Collection) entity).stream().forEach(en -> {
                coll.add(doGetEntity(en, include));
            });
            return coll;
        } else if (entity instanceof Map) {
            Map dMap = Maps.newHashMap();
            ((Map) entity).forEach((k, v) -> {
                dMap.put(k, doGetEntity(v, include));
            });
            return dMap;
        } else if (entity.getClass().isArray()) {
            if (entity.getClass().getComponentType().isPrimitive()) {
                return entity;
            } else {
                Object[] v = (Object[]) entity;
                Object[] t = new Object[v.length];
                for (int i = 0; i < v.length; i++) {
                    t[i] = doGetEntity(v[i], include);
                }
                return t;
            }
        } else if (entity instanceof BaseResponse) {
            return doGetBaseResponse(entity, include);
        }
        return doGetEntityResponse(entity, include);
    }

    /**
     * 获取实体类的最终对象值
     *
     * @param entity  实体类
     * @param include 是否解析内部嵌套类
     * @return
     */
    private static Object doGetEntity(final Object entity, final Boolean include) {
        if (isFinal(entity)) {
            return entity;
        } else if (entity instanceof Collection) {
            return sensitive(entity, include);
        } else if (entity instanceof Map) {
            return sensitive(entity, include);
        } else if (entity.getClass().isArray()) {
            return sensitive(entity, include);
        } else if (entity instanceof BaseResponse) {
            return doGetBaseResponse(entity, include);
        } else {
            return doGetEntityResponse(entity, include);
        }
    }

    /**
     * 对最外层是BaseResponse做处理
     *
     * @param entity  实体类
     * @param include 是否解析内部实体类
     * @return
     */
    private static Object doGetBaseResponse(final Object entity, final Boolean include) {
        BaseResponse baseResponse = ((BaseResponse) entity);
        BaseResponse response = new BaseResponse();
        response.setStatus(baseResponse.getStatus());
        response.setMessage(baseResponse.getMessage());
        response.setData(sensitive(baseResponse.getData(), include));
        response.setSpentTime(baseResponse.getSpentTime());
        return response;
    }

    /**
     * 获取实体类脱敏后的数据
     *
     * @param entity  实体类
     * @param include 是否解析嵌套类并脱敏
     * @return
     */
    private static Object doGetEntityResponse(final Object entity, final Boolean include) {
        if (entity.getClass().isAnnotationPresent(JsonSensitive.class)) {
            return doSetField(entity, entity.getClass().getAnnotation(JsonSensitive.class).include());
        } else if (isInclude(include)) {
            return doSetField(entity, Boolean.TRUE);
        } else {
            return entity;
        }
    }

    /**
     * 获取实体类对象脱敏后的对象
     *
     * @param entity 需要脱敏的实体类对象
     * @return
     */
    private static Map<String, Object> doSetField(final Object entity, final Boolean include) {
        Map<String, Object> fieldMap = Maps.newHashMap();
        try {
            //通用fieldKey fieldValue忽略
            Map<String, JsonFlexField> flexFieldMap = null;
            Field[] fields = entity.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (isModifierFinal(field)) {
                    continue;
                }
                field.setAccessible(true);
                String name = field.getName();
                Object value = field.get(entity);
                if (Objects.isNull(value)) {
                    fieldMap.put(name, null);
                    continue;
                }
                //普通字段脱敏
                if (field.isAnnotationPresent(JsonSimField.class)) {
                    if (isFinal(value)) {
                        JsonSimField sensitive = field.getAnnotation(JsonSimField.class);
                        if (value instanceof String) {
                            fieldMap.put(name, doGetSensitiveField(sensitive.value(), (String) value));
                        } else {
                            fieldMap.put(name, value);
                        }
                    }
                    // 复杂类型字段脱敏
                } else if (field.isAnnotationPresent(JsonFlexField.class)) {
                    JsonFlexField jsonFlexField = field.getAnnotation(JsonFlexField.class);
                    if (flexFieldMap == null) {
                        flexFieldMap = Maps.newHashMap();
                    }
                    if ((value instanceof String)) {
                        flexFieldMap.put(name, jsonFlexField);
                    }
                    fieldMap.put(name, value);
                } else if (value instanceof Collection) {
                    Collection coll = new ArrayList();
                    ((Collection) value).stream().forEach(en -> {
                        coll.add(doGetField(en, include));
                    });
                    fieldMap.put(name, coll);
                } else if (value instanceof Map) {
                    Map dMap = Maps.newHashMap();
                    ((Map) value).forEach((k, v) -> {
                        dMap.put(k, doGetField(v, include));
                    });
                    fieldMap.put(name, dMap);
                } else if (value.getClass().isArray()) {
                    if (value.getClass().getComponentType().isPrimitive()) {
                        fieldMap.put(name, value);
                    } else {
                        Object[] v = (Object[]) value;
                        Object[] t = new Object[v.length];
                        for (int i = 0; i < v.length; i++) {
                            t[i] = doGetField(v[i], include);
                        }
                        fieldMap.put(name, t);
                    }
                } else {
                    fieldMap.put(name, doGetField(value, include));
                }
            }
            // 灵活复杂数据类型脱敏
            fieldMap.putAll(doGetFlexField(fieldMap, flexFieldMap));
        } catch (Exception ex) {
            logger.error(PrintExceptionInfo.printErrorInfo(ex));
        }
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
     * @param f       字段值对象
     * @param include 是否解析嵌套实体类
     * @return
     */
    private static Object doGetField(final Object f, final Boolean include) {
        if (isFinal(f)) {
            return f;
        } else if (isInclude(include)) {
            return sensitive(f, Boolean.TRUE);
        } else {
            return f;
        }
    }

    /**
     * 灵活复杂类型字段脱敏
     *
     * @param fieldMap     实体类字段值集合
     * @param flexFieldMap 复杂类型字段集合
     * @return
     */
    private static Map<String, Object> doGetFlexField(final Map<String, Object> fieldMap, final Map<String, JsonFlexField> flexFieldMap) {
        if (CollectionUtils.isEmpty(flexFieldMap)) {
            return Collections.emptyMap();
        }
        Map<String, Object> dataMap = Maps.newHashMap();
        flexFieldMap.forEach((n, j) -> {
            Object v = fieldMap.get(n);
            if ((v instanceof String)) {
                for (int i = 0; i < j.fieldNames().length; i++) {
                    if (!StringUtils.equals(j.fieldNames()[i], (String) v)) {
                        continue;
                    }
                    SensitiveType type;
                    //如果A>B（等价于A-1>=B），则展示默认值
                    if (i >= j.types().length) {
                        type = SensitiveType.DEFAULT;
                    } else {
                        type = j.types()[i];
                    }
                    //获取值字段值
                    Object fv = fieldMap.get(j.fieldValue());
                    if (Objects.nonNull(fv)) {
                        dataMap.put(j.fieldValue(), doGetSensitiveField(type, (String) fv));
                    }
                }
            }
        });
        return dataMap;
    }

    /**
     * 判定是否包含
     *
     * @param include
     * @return
     */
    private static boolean isInclude(final Boolean include) {
        if (Objects.nonNull(include) && include) {
            return true;
        }
        return false;
    }

    /**
     * 判定值对象是否是无需再继续进行解析
     *
     * @param value 值对象
     * @return
     */
    private static boolean isFinal(final Object value) {
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
        } else if (value instanceof Number) {
            return true;
        } else if (value.getClass().isEnum()) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * 脱敏字段
     *
     * @param type       脱敏类型
     * @param fieldValue 字段值
     * @return
     */
    public static String doGetSensitiveField(SensitiveType type, String fieldValue) {
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
