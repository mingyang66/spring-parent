package com.emily.infrastructure.sensitive;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * @program: spring-parent
 * @description: bean相互转换工具类
 * @author: Emily
 * @create: 2021/05/28
 */
class JavaBeanUtils {
    /**
     * 判断是否是无需解析的值对象
     *
     * @param value 值对象
     * @return 是-true 否-false
     */
    public static boolean isFinal(final Object value) {
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
     * 指定的修饰符是否序列化
     *
     * @param field 字段反射类型
     * @return
     */
    public static boolean isModifierFinal(final Field field) {
        int modifiers = field.getModifiers();
        if (checkModifierFinalStaticTransVol(modifiers) || checkModifierNativeSyncStrict(modifiers)) {
            return true;
        }
        return false;
    }

    protected static boolean checkModifierNativeSyncStrict(int modifiers) {
        return Modifier.isNative(modifiers)
                || Modifier.isSynchronized(modifiers)
                || Modifier.isStrict(modifiers);
    }

    protected static boolean checkModifierFinalStaticTransVol(int modifiers) {
        return Modifier.isFinal(modifiers)
                || Modifier.isStatic(modifiers)
                || Modifier.isTransient(modifiers)
                || Modifier.isVolatile(modifiers);
    }
}
