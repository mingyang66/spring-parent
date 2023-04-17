package com.emily.infrastructure.common.i18n;

import com.emily.infrastructure.common.object.JavaBeanUtils;
import com.emily.infrastructure.common.utils.json.JSONUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * @Description :  多语言解析
 * @Author :  Emily
 * @CreateDate :  Created in 2023/4/15 5:42 PM
 */
public class I18nUtils {
    public static Object acquire(final Object entity, final LanguageType languageType) {
        if (JavaBeanUtils.isFinal(entity)) {
            return entity;
        }
        if (entity.getClass().isAnnotationPresent(ApiI18n.class)) {
            doSetField(entity, languageType);
        }
        return entity;
    }

    /**
     * 获取实体类对象脱敏后的对象
     *
     * @param entity 需要脱敏的实体类对象
     * @return
     */
    private static void doSetField(final Object entity, final LanguageType languageType) {
        try {
            Field[] fields = entity.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (JavaBeanUtils.isModifierFinal(field)) {
                    continue;
                }
                field.setAccessible(true);
                String name = field.getName();
                Object value = field.get(entity);
                if (Objects.isNull(value)) {
                    continue;
                }
                if (value instanceof Collection) {

                } else if (value instanceof Map) {

                } else if (value.getClass().isArray()) {

                } else if (value instanceof String) {
                    if (field.isAnnotationPresent(ApiI18nProperty.class)) {
                        field.set(entity, LanguageMap.acquire((String) value, languageType));
                    }
                }
            }
        } catch (Exception ex) {

        }
    }

    public static void main(String[] args) {
        LanguageMap.bindEn("田晓霞", "tianxiaoxia");
        I18nStudent student = new I18nStudent();
        student.setName("田晓霞");
        student.setAge(18);
        System.out.println(JSONUtils.toJSONString(acquire(student, LanguageType.EN)));

    }
}
