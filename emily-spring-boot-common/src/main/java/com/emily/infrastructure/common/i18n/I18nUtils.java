package com.emily.infrastructure.common.i18n;

import com.emily.infrastructure.common.object.JavaBeanUtils;
import com.emily.infrastructure.common.utils.json.JSONUtils;

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
                for (Object v : (Object[]) entity) {
                    acquire(v, languageType);
                }
            } else if (entity.getClass().isAnnotationPresent(ApiI18n.class)) {
                doSetField(entity, languageType);
            }
        } catch (IllegalAccessException exception) {

        }
        return entity;
    }

    /**
     * 获取实体类对象脱敏后的对象
     *
     * @param entity 需要脱敏的实体类对象
     * @return
     */
    private static void doSetField(final Object entity, final LanguageType languageType) throws IllegalAccessException {
        if (Objects.isNull(entity)) {
            return;
        }
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (JavaBeanUtils.isModifierFinal(field)) {
                continue;
            }
            field.setAccessible(true);
            Object value = field.get(entity);
            if (Objects.isNull(value)) {
                continue;
            }
            if (field.isAnnotationPresent(ApiI18nProperty.class)) {
                if ((value instanceof String)) {
                    field.set(entity, LanguageMap.acquire((String) value, languageType));
                }
            }
            if (value instanceof Collection) {
                for (Iterator it = ((Collection) value).iterator(); it.hasNext(); ) {
                    doGetEntity(field, it.next(), languageType);
                }
            } else if (value instanceof Map) {
                for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) value).entrySet()) {
                    doGetEntity(field, entry.getValue(), languageType);
                }
            } else if (value.getClass().isArray()) {
                for (Object v : (Object[]) value) {
                    doGetEntity(field, v, languageType);
                }
            }
        }
    }

    public static void doGetEntity(final Field field, final Object entity, final LanguageType languageType) throws IllegalAccessException {
        if (Objects.isNull(entity)) {
            return;
        }
        if (field.isAnnotationPresent(ApiI18nProperty.class)) {
            if (entity instanceof String) {
                field.set(entity, LanguageMap.acquire((String) entity, languageType));
            } else {
                acquire(entity, languageType);
            }
        } else {
            acquire(entity, languageType);
        }
    }
}
