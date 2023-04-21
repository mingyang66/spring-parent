package com.emily.infrastructure.common.sensitive;

import com.emily.infrastructure.common.constant.AttributeInfo;
import com.emily.infrastructure.common.entity.BaseResponse;
import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.common.object.JavaBeanUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @Description :  敏感信息工具类
 * @Author :  Emily
 * @CreateDate :  Created in 2022/7/19 3:13 下午
 */
public class SensitiveUtils {

    public static final Logger logger = LoggerFactory.getLogger(SensitiveUtils.class);


    /**
     * @param entity 需要脱敏的实体类对象，如果是数据值类型则直接返回
     * @return 脱敏后的实体类对象
     * @Description 对实体类镜像脱敏，支持嵌套内部类及父类的
     */
    public static Object acquire(final Object entity) {
        try {
            if (JavaBeanUtils.isFinal(entity)) {
                return entity;
            }
            if (entity instanceof Collection) {
                Collection<Object> coll = new ArrayList();
                for (Iterator<Object> it = ((Collection<Object>) entity).iterator(); it.hasNext(); ) {
                    coll.add(acquire(it.next()));
                }
                return coll;
            } else if (entity instanceof Map) {
                Map<Object, Object> dMap = Maps.newHashMap();
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
                BaseResponse<Object> response = (BaseResponse<Object>) entity;
                return new BaseResponse<>(response.getStatus(), response.getMessage(), acquire(response.getData()), response.getSpentTime());
            } else if (entity.getClass().isAnnotationPresent(JsonSensitive.class)) {
                return doSetField(entity);
            }
        } catch (Exception exception) {
            logger.error(PrintExceptionInfo.printErrorInfo(exception));
        }
        return entity;
    }

    /**
     * 获取实体类对象脱敏后的对象
     *
     * @param entity 需要脱敏的实体类对象
     * @return 实体类属性脱敏后的集合对象
     */
    private static Map<String, Object> doSetField(final Object entity) throws IllegalAccessException {
        Map<String, Object> fieldMap = Maps.newHashMap();
        Map<String, JsonFlexField> flexFieldMap = null;
        Field[] fields = FieldUtils.getAllFields(entity.getClass());
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
            if (value instanceof String) {
                flexFieldMap = (flexFieldMap == null) ? Maps.newHashMap() : flexFieldMap;
                fieldMap.put(name, doGetEntityStr(field, value, flexFieldMap));
            } else if (value instanceof Collection) {
                fieldMap.put(name, doGetEntityColl(field, value));
            } else if (value instanceof Map) {
                fieldMap.put(name, doGetEntityMap(field, value));
            } else if (value.getClass().isArray()) {
                fieldMap.put(name, doGetEntityArray(field, value));
            } else {
                fieldMap.put(name, acquire(value));
            }
        }
        fieldMap.putAll(doGetEntityFlex(fieldMap, flexFieldMap));
        return fieldMap;
    }

    /**
     * @param field        实体类属性对象
     * @param value        属性值
     * @param flexFieldMap 复杂数据类型集合
     * @return 脱敏后的数据对象
     */
    protected static Object doGetEntityStr(final Field field, final Object value, Map<String, JsonFlexField> flexFieldMap) {
        if (field.isAnnotationPresent(JsonSimField.class)) {
            return doGetProperty((String) value, field.getAnnotation(JsonSimField.class).value());
        } else if (field.isAnnotationPresent(JsonFlexField.class)) {
            flexFieldMap.put(field.getName(), field.getAnnotation(JsonFlexField.class));
            return value;
        } else {
            return acquire(value);
        }
    }

    /**
     * @param field 实体类属性对象
     * @param value 属性值
     * @return 脱敏后的数据对象
     */
    protected static Object doGetEntityColl(final Field field, final Object value) {
        Collection<Object> list = Lists.newArrayList();
        Collection collection = (Collection) value;
        for (Iterator it = collection.iterator(); it.hasNext(); ) {
            Object v = it.next();
            if (Objects.isNull(v)) {
                list.add(null);
            } else if ((v instanceof String) && field.isAnnotationPresent(JsonSimField.class)) {
                list.add(doGetProperty((String) v, field.getAnnotation(JsonSimField.class).value()));
            } else {
                list.add(acquire(v));
            }
        }
        return list;
    }

    /**
     * @param field 实体类属性对象
     * @param value 属性值
     * @return 脱敏后的数据对象
     */
    protected static Object doGetEntityMap(final Field field, final Object value) {
        Map<Object, Object> dMap = Maps.newHashMap();
        for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) value).entrySet()) {
            Object key = entry.getKey();
            Object v = entry.getValue();
            if (Objects.isNull(v)) {
                dMap.put(key, null);
            } else if ((v instanceof String) && field.isAnnotationPresent(JsonSimField.class)) {
                dMap.put(key, doGetProperty((String) v, field.getAnnotation(JsonSimField.class).value()));
            } else {
                dMap.put(key, acquire(v));
            }
        }
        return dMap;
    }

    /**
     * @param field 实体类属性对象
     * @param value 属性值
     * @return 脱敏后的数据对象
     */
    protected static Object doGetEntityArray(final Field field, final Object value) {
        if (value.getClass().getComponentType().isPrimitive()) {
            return value;
        } else {
            Object[] v = (Object[]) value;
            Object[] t = new Object[v.length];
            for (int i = 0; i < v.length; i++) {
                if (Objects.isNull(v[i])) {
                    t[i] = null;
                } else if ((v[i] instanceof String) && field.isAnnotationPresent(JsonSimField.class)) {
                    t[i] = doGetProperty((String) v[i], field.getAnnotation(JsonSimField.class).value());
                } else {
                    t[i] = acquire(v[i]);
                }
            }
            return t;
        }
    }

    /**
     * 灵活复杂类型字段脱敏
     *
     * @param fieldMap     实体类字段值集合
     * @param flexFieldMap 复杂类型字段集合
     * @return 复杂类型字段脱敏后的数据集合
     */
    protected static Map<String, Object> doGetEntityFlex(final Map<String, Object> fieldMap, final Map<String, JsonFlexField> flexFieldMap) {
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
                    dataMap.put(jsonFlexField.fieldValue(), doGetProperty((String) fv, type));
                }
            }
        }
        return dataMap;
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
