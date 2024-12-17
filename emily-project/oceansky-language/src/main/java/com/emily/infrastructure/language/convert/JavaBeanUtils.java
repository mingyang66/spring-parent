package com.emily.infrastructure.language.convert;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * bean相互转换工具类
 *
 * @author Emily
 * @since 2021/05/28
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
        } else return value.getClass().isEnum();
    }

    /**
     * 指定的修饰符是否序列化
     *
     * @param field 字段反射类型
     * @return true -是 false-否
     */
    public static boolean isModifierFinal(final Field field) {
        int modifiers = field.getModifiers();
        return checkModifierFinalStaticTransVol(modifiers) || checkModifierNativeSyncStrict(modifiers);
    }

    protected static boolean checkModifierNativeSyncStrict(int modifiers) {
        return Modifier.isNative(modifiers)
                || Modifier.isSynchronized(modifiers)
                || Modifier.isStrict(modifiers);
    }

    protected static boolean checkModifierFinalStaticTransVol(int modifiers) {
        return Modifier.isStatic(modifiers)
                || Modifier.isTransient(modifiers)
                || Modifier.isVolatile(modifiers);
    }
}
