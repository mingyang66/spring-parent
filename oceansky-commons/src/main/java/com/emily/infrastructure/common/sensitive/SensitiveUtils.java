package com.emily.infrastructure.common.sensitive;

import com.emily.infrastructure.common.object.JavaBeanUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @Description :  对实体类镜像脱敏，返回结构相同的非同一个对象
 * @Author :  Emily
 * @CreateDate :  Created in 2022/7/19 3:13 下午
 */
public class SensitiveUtils {

    /**
     * @param entity 需要脱敏的实体类对象，如果是数据值类型则直接返回
     * @return 脱敏后的实体类对象
     * @Description 对实体类镜像脱敏，返回结构相同的非同一个对象
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
            } else if (entity.getClass().isAnnotationPresent(JsonSensitive.class)) {
                return doSetField(entity);
            }
        } catch (Exception exception) {
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
                fieldMap.put(name, doGetEntityStr(field, value));
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
        fieldMap.putAll(doGetEntityFlex(entity));
        return fieldMap;
    }

    /**
     * @param field 实体类属性对象
     * @param value 属性值
     * @return 脱敏后的数据对象
     */
    protected static Object doGetEntityStr(final Field field, final Object value) {
        if (field.isAnnotationPresent(JsonSimField.class)) {
            return DataMaskUtils.doGetProperty((String) value, field.getAnnotation(JsonSimField.class).value());
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
                list.add(DataMaskUtils.doGetProperty((String) v, field.getAnnotation(JsonSimField.class).value()));
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
                dMap.put(key, DataMaskUtils.doGetProperty((String) v, field.getAnnotation(JsonSimField.class).value()));
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
                    t[i] = DataMaskUtils.doGetProperty((String) v[i], field.getAnnotation(JsonSimField.class).value());
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
     * @param entity 实体类
     * @return 复杂类型字段脱敏后的数据集合
     */
    protected static Map<String, Object> doGetEntityFlex(final Object entity) throws IllegalAccessException {
        Map<String, Object> flexFieldMap = null;
        Field[] fields = FieldUtils.getFieldsWithAnnotation(entity.getClass(), JsonFlexField.class);
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(entity);
            if (Objects.isNull(value)) {
                continue;
            }
            JsonFlexField jsonFlexField = field.getAnnotation(JsonFlexField.class);
            if (Objects.isNull(jsonFlexField.fieldValue())) {
                continue;
            }
            Field flexField = FieldUtils.getField(entity.getClass(), jsonFlexField.fieldValue(), true);
            if (Objects.isNull(flexField)) {
                continue;
            }
            Object flexValue = flexField.get(entity);
            if (Objects.isNull(flexValue) || !(flexValue instanceof String)) {
                continue;
            }
            int index = Arrays.asList(jsonFlexField.fieldKeys()).indexOf((String) value);
            if (index < 0) {
                continue;
            }
            SensitiveType type;
            if (index >= jsonFlexField.types().length) {
                type = SensitiveType.DEFAULT;
            } else {
                type = jsonFlexField.types()[index];
            }
            flexFieldMap = Objects.isNull(flexFieldMap) ? Maps.newHashMap() : flexFieldMap;
            flexFieldMap.put(jsonFlexField.fieldValue(), DataMaskUtils.doGetProperty((String) flexValue, type));
        }
        return Objects.isNull(flexFieldMap) ? Collections.emptyMap() : flexFieldMap;
    }
}
