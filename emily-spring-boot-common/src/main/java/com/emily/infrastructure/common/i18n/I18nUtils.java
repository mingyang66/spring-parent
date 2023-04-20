package com.emily.infrastructure.common.i18n;

import com.emily.infrastructure.common.entity.BaseResponse;
import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.common.object.JavaBeanUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * @Description :  多语言解析
 * @Author :  Emily
 * @CreateDate :  Created in 2023/4/15 5:42 PM
 */
public class I18nUtils {

    public static final Logger logger = LoggerFactory.getLogger(I18nUtils.class);

    /**
     * 对实体类进行多语言翻译
     *
     * @param entity       实体类|普通对象
     * @param languageType 语言类型
     * @return 翻译后的实体类对象
     */
    public static Object acquire(final Object entity, LanguageType languageType) {
        try {
            if (JavaBeanUtils.isFinal(entity)) {
                return entity;
            }
            if (Objects.isNull(languageType)) {
                languageType = LanguageType.ZH;
            }
            if (entity instanceof Collection) {
                for (Iterator it = ((Collection) entity).iterator(); it.hasNext(); ) {
                    acquire(it.next(), languageType);
                }
            } else if (entity instanceof Map) {
                for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) entity).entrySet()) {
                    acquire(entry.getValue(), languageType);
                }
            } else if (entity.getClass().isArray()) {
                if (!entity.getClass().getComponentType().isPrimitive()) {
                    for (Object v : (Object[]) entity) {
                        acquire(v, languageType);
                    }
                }
            } else if (entity instanceof BaseResponse) {
                BaseResponse response = (BaseResponse) entity;
                acquire(response.getData(), languageType);
            } else if (entity.getClass().isAnnotationPresent(ApiI18n.class)) {
                doSetField(entity, languageType);
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
     */
    protected static void doSetField(final Object entity, final LanguageType languageType) throws IllegalAccessException {
        if (Objects.isNull(entity)) {
            return;
        }
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
            } else if (value instanceof Map) {
                doGetEntityMap(field, entity, value, languageType);
            } else if (value.getClass().isArray()) {
                doGetEntityArray(field, entity, value, languageType);
            }
        }
    }

    /**
     * @param field        实体类属性对象
     * @param entity       实体类对象
     * @param value        属性值对象
     * @param languageType 语言类型
     * @throws IllegalAccessException 抛出非法访问异常
     * @Description 对字符串进行多语言支持
     */
    protected static void doGetEntityStr(final Field field, final Object entity, final Object value, final LanguageType languageType) throws IllegalAccessException {
        if (field.isAnnotationPresent(ApiI18nProperty.class)) {
            field.set(entity, doGetProperty((String) value, languageType));
        }
    }

    /**
     * @param field        实体类属性对象
     * @param entity       实体类对象
     * @param value        属性值对象
     * @param languageType 语言类型
     * @throws IllegalAccessException 抛出非法访问异常
     * @Description 对Collection集合中存储是字符串、实体对象进行多语言支持
     */
    protected static void doGetEntityColl(final Field field, final Object entity, final Object value, final LanguageType languageType) throws IllegalAccessException {
        Collection<String> list = null;
        Collection collection = ((Collection) value);
        for (Iterator it = collection.iterator(); it.hasNext(); ) {
            Object v = it.next();
            if (Objects.isNull(v)) {
                continue;
            }
            if ((v instanceof String) && field.isAnnotationPresent(ApiI18nProperty.class)) {
                list = (list == null) ? Lists.newArrayList() : list;
                list.add(doGetProperty((String) v, languageType));
            } else {
                acquire(v, languageType);
            }
        }
        if (Objects.nonNull(list)) {
            field.set(entity, list);
        }
    }

    /**
     * @param field        实体类属性对象
     * @param entity       实体类对象
     * @param value        属性值对象
     * @param languageType 语言类型
     * @throws IllegalAccessException 抛出非法访问异常
     * @Description 对Map集合中存储是字符串、实体对象进行多语言支持
     */
    protected static void doGetEntityMap(final Field field, final Object entity, final Object value, final LanguageType languageType) throws IllegalAccessException {
        Map<Object, Object> dMap = ((Map<Object, Object>) value);
        for (Map.Entry<Object, Object> entry : dMap.entrySet()) {
            Object key = entry.getKey();
            Object v = entry.getValue();
            if (Objects.isNull(v)) {
                continue;
            }
            if ((v instanceof String) && field.isAnnotationPresent(ApiI18nProperty.class)) {
                dMap.put(key, doGetProperty((String) v, languageType));
            } else {
                acquire(value, languageType);
            }
        }
    }

    /**
     * @param field        实体类属性对象
     * @param entity       实体类对象
     * @param value        属性值对象
     * @param languageType 语言类型
     * @throws IllegalAccessException 抛出非法访问异常
     * @Description 对数组中存储是字符串、实体对象进行多语言支持
     */
    protected static void doGetEntityArray(final Field field, final Object entity, final Object value, final LanguageType languageType) throws IllegalAccessException {
        if (value.getClass().getComponentType().isPrimitive()) {
            return;
        }
        Object[] arrays = ((Object[]) value);
        for (int i = 0; i < arrays.length; i++) {
            Object v = arrays[i];
            if (Objects.isNull(v)) {
                continue;
            }
            if ((v instanceof String) && field.isAnnotationPresent(ApiI18nProperty.class)) {
                arrays[i] = doGetProperty((String) v, languageType);
            } else {
                acquire(value, languageType);
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
        return LanguageMap.acquire(value, languageType);
    }
}
