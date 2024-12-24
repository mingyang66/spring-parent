package com.emily.infrastructure.language.convert;

import com.emily.infrastructure.language.annotation.I18nFlexibleProperty;
import com.emily.infrastructure.language.annotation.I18nMapProperty;
import com.emily.infrastructure.language.annotation.I18nModel;
import com.emily.infrastructure.language.annotation.I18nProperty;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * 多语言解析
 *
 * @author Emily
 * @since Created in 2023/4/15 5:42 PM
 */
public class I18nUtils {
    /**
     * 对实体类进行多语言翻译
     *
     * @param entity       实体类|普通对象
     * @param languageType 语言类型
     * @param <T>          实体对象
     * @return 翻译后的实体类对象
     */
    public static <T> T translateElseGet(final T entity, LanguageType languageType, final Class<?>... packClass) {
        try {
            return translate(entity, languageType, packClass);
        } catch (IllegalAccessException ex) {
            return entity;
        }
    }

    /**
     * 对实体类进行多语言翻译
     *
     * @param entity       实体类|普通对象
     * @param languageType 语言类型
     * @param <T>          实体对象
     * @return 翻译后的实体类对象
     * @throws IllegalAccessException 非法访问异常
     */
    public static <T> T translate(final T entity, LanguageType languageType, final Class<?>... packClass) throws IllegalAccessException {
        Objects.requireNonNull(languageType, "languageType must not be null");
        if (JavaBeanUtils.isFinal(entity)) {
            return entity;
        }
        if (entity instanceof Collection) {
            for (Object o : (Collection<?>) entity) {
                translate(o, languageType, packClass);
            }
        } else if (entity instanceof Map) {
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) entity).entrySet()) {
                translate(entry.getValue(), languageType, packClass);
            }
        } else if (entity.getClass().isArray()) {
            if (!entity.getClass().getComponentType().isPrimitive()) {
                for (Object v : (Object[]) entity) {
                    translate(v, languageType, packClass);
                }
            }
        } else if (entity.getClass().isAnnotationPresent(I18nModel.class)) {
            doSetField(entity, languageType);
        } else if (packClass.length > 0 && entity.getClass().isAssignableFrom(packClass[0])) {
            doSetField(entity, languageType, ArrayUtils.remove(packClass, 0));
        }
        return entity;
    }

    /**
     * 对实体类entity的属性及父类的属性遍历并对符合条件的属性进行多语言翻译
     *
     * @param entity       实体类对象
     * @param languageType 语言类型
     * @param <T>          实体对象
     * @throws IllegalAccessException 非法访问异常
     */
    protected static <T> void doSetField(final T entity, final LanguageType languageType, final Class<?>... packClass) throws IllegalAccessException {
        Field[] fields = FieldUtils.getAllFields(entity.getClass());
        for (Field field : fields) {
            if (JavaBeanUtils.isModifierFinal(field)) {
                continue;
            }
            field.setAccessible(true);
            Object value = field.get(entity);
            if (Objects.isNull(value)) {
                continue;
            }
            if (value instanceof String) {
                doGetEntityStr(field, entity, value, languageType);
            } else if (value instanceof Collection) {
                doGetEntityColl(field, entity, value, languageType);
            } else if (value instanceof Map<?, ?>) {
                @SuppressWarnings("unchecked")
                Map<Object, Object> v = (Map<Object, Object>) value;
                doGetEntityMap(field, v, languageType);
            } else if (value.getClass().isArray()) {
                doGetEntityArray(field, value, languageType);
            } else {
                translate(value, languageType, packClass);
            }
        }
        doGetEntityFlexible(entity, languageType);
    }

    /**
     * 对字符串进行多语言支持
     *
     * @param field        实体类属性对象
     * @param entity       实体类对象
     * @param value        属性值对象
     * @param languageType 语言类型
     * @param <T>          实体对象
     * @throws IllegalAccessException 抛出非法访问异常
     */
    protected static <T> void doGetEntityStr(final Field field, final T entity, final Object value, final LanguageType languageType) throws IllegalAccessException {
        if (field.isAnnotationPresent(I18nProperty.class)) {
            field.set(entity, doGetProperty((String) value, languageType));
        }
    }

    /**
     * 对Collection集合中存储是字符串、实体对象进行多语言支持
     *
     * @param field        实体类属性对象
     * @param entity       实体类对象
     * @param value        属性值对象
     * @param languageType 语言类型
     * @param <T>          实体对象类型
     * @throws IllegalAccessException 抛出非法访问异常
     */
    protected static <T> void doGetEntityColl(final Field field, final T entity, final Object value, final LanguageType languageType) throws IllegalAccessException {
        Collection<Object> list = null;
        Collection<?> collection = ((Collection<?>) value);
        for (Object v : collection) {
            if (Objects.isNull(v)) {
                continue;
            }
            if ((v instanceof String) && field.isAnnotationPresent(I18nProperty.class)) {
                list = (list == null) ? Lists.newArrayList() : list;
                list.add(doGetProperty((String) v, languageType));
            } else {
                translate(v, languageType);
            }
        }
        if (Objects.nonNull(list)) {
            field.set(entity, list);
        }
    }

    /**
     * 对Map集合中存储是字符串、实体对象进行多语言支持
     *
     * @param field        实体类属性对象
     * @param value        属性值对象
     * @param languageType 语言类型
     * @throws IllegalAccessException 抛出非法访问异常
     */
    protected static void doGetEntityMap(final Field field, final Map<Object, Object> value, final LanguageType languageType) throws IllegalAccessException {
        for (Map.Entry<?, ?> entry : value.entrySet()) {
            Object key = entry.getKey();
            Object v = entry.getValue();
            if (Objects.isNull(v)) {
                continue;
            }
            if (v instanceof String) {
                if (field.isAnnotationPresent(I18nMapProperty.class)) {
                    I18nMapProperty i18nMapProperty = field.getAnnotation(I18nMapProperty.class);
                    int index = (key instanceof String) ? Arrays.asList(i18nMapProperty.value()).indexOf(key) : -1;
                    if (index < 0) {
                        continue;
                    }
                    value.put(key, doGetProperty((String) v, languageType));
                    continue;
                }
                if (field.isAnnotationPresent(I18nProperty.class)) {
                    value.put(key, doGetProperty((String) v, languageType));
                    continue;
                }
            }
            translate(value, languageType);
        }
    }

    /**
     * 对数组中存储是字符串、实体对象进行多语言支持
     *
     * @param field        实体类属性对象
     * @param value        属性值对象
     * @param languageType 语言类型
     * @throws IllegalAccessException 抛出非法访问异常
     */
    protected static void doGetEntityArray(final Field field, final Object value, final LanguageType languageType) throws IllegalAccessException {
        if (value.getClass().getComponentType().isPrimitive()) {
            return;
        }
        Object[] arrays = ((Object[]) value);
        for (int i = 0; i < arrays.length; i++) {
            Object v = arrays[i];
            if (Objects.isNull(v)) {
                continue;
            }
            if ((v instanceof String) && field.isAnnotationPresent(I18nProperty.class)) {
                arrays[i] = doGetProperty((String) v, languageType);
            } else {
                translate(value, languageType);
            }
        }
    }

    /**
     * 获取根据语言类型翻译后的属性结果
     *
     * @param value        属性值
     * @param languageType 语言类型
     * @return 翻译后的结果
     */
    public static String doGetProperty(String value, LanguageType languageType) {
        Objects.requireNonNull(languageType, "languageType must not be null");
        return I18nCache.acquire(value, languageType);
    }

    /**
     * 获取通过两个字段进行灵活传递的参数进行脱敏
     *
     * @param entity 实体类对象
     * @param <T>    实体类类型
     * @throws IllegalAccessException 抛出非法访问异常
     */
    protected static <T> void doGetEntityFlexible(final T entity, LanguageType languageType) throws IllegalAccessException {
        Field[] fields = FieldUtils.getFieldsWithAnnotation(entity.getClass(), I18nFlexibleProperty.class);
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(entity);
            if (Objects.isNull(value)) {
                continue;
            }
            I18nFlexibleProperty i18nFlexibleProperty = field.getAnnotation(I18nFlexibleProperty.class);
            if (ObjectUtils.isEmpty(i18nFlexibleProperty.value()) || StringUtils.isBlank(i18nFlexibleProperty.target())) {
                return;
            }
            Field flexibleField = FieldUtils.getField(entity.getClass(), i18nFlexibleProperty.target(), true);
            if (Objects.isNull(flexibleField)) {
                return;
            }
            Object flexValue = flexibleField.get(entity);
            if (Objects.isNull(flexValue) || !(flexValue instanceof String)) {
                return;
            }
            int index = Arrays.asList(i18nFlexibleProperty.value()).indexOf((String) value);
            if (index < 0) {
                return;
            }
            flexibleField.set(entity, doGetProperty((String) flexValue, languageType));
        }
    }
}
