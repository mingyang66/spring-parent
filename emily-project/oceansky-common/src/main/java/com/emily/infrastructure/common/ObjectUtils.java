package com.emily.infrastructure.common;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * 对象类型工具类
 *
 * @author Emily
 * @since Created in 2023/6/13 10:30 AM
 */
public class ObjectUtils {
    /**
     * 判定对象是否为null
     * ---------------------------------------------------
     * 示例：
     * Assert.assertTrue(ObjectUtils.isNull(null));
     * Assert.assertFalse(ObjectUtils.isNull(""));
     * Assert.assertFalse(ObjectUtils.isNull("a"));
     * Assert.assertFalse(ObjectUtils.isNull(new Object()));
     * ---------------------------------------------------
     *
     * @param obj 对象
     * @return true-是，false-否
     */
    public static boolean isNull(Object obj) {
        return obj == null;
    }

    /**
     * 判定对象是否不为null
     * -----------------------------------------------------
     * 示例：
     * Assert.assertFalse(ObjectUtils.isNotNull(null));
     * Assert.assertTrue(ObjectUtils.isNotNull(""));
     * Assert.assertTrue(ObjectUtils.isNotNull("a"));
     * Assert.assertTrue(ObjectUtils.isNotNull(new Object()));
     * -----------------------------------------------------
     *
     * @param obj 对象
     * @return true-是 false-否
     */
    public static boolean isNotNull(Object obj) {
        return !isNull(obj);
    }

    /**
     * 判断对象是否为Null
     * -------------------------------------------------------
     * 示例：
     * Assert.assertTrue(ObjectUtils.isEmpty(null));
     * Assert.assertTrue(ObjectUtils.isEmpty(""));
     * Assert.assertTrue(ObjectUtils.isEmpty(new String[]{}));
     * Assert.assertTrue(ObjectUtils.isEmpty(Collections.emptyMap()));
     * Assert.assertTrue(ObjectUtils.isEmpty(Collections.emptyList()));
     * -------------------------------------------------------
     *
     * @param object 对象
     * @return true-是 false-否
     */
    public static boolean isEmpty(final Object object) {
        if (object == null) {
            return true;
        }
        if (object instanceof CharSequence) {
            return ((CharSequence) object).isEmpty();
        }
        if (object.getClass().isArray()) {
            return Array.getLength(object) == 0;
        }
        if (object instanceof Collection<?>) {
            return ((Collection<?>) object).isEmpty();
        }
        if (object instanceof Map<?, ?>) {
            return ((Map<?, ?>) object).isEmpty();
        }
        return false;
    }

    /**
     * 判断对象是否为空
     * ------------------------------------------------------
     * 示例：
     * Assert.assertFalse(ObjectUtils.isNotEmpty(null));
     * Assert.assertFalse(ObjectUtils.isNotEmpty(""));
     * Assert.assertFalse(ObjectUtils.isNotEmpty(new String[]{}));
     * Assert.assertFalse(ObjectUtils.isNotEmpty(Collections.emptyMap()));
     * Assert.assertFalse(ObjectUtils.isNotEmpty(Collections.emptyList()));
     * ------------------------------------------------------
     *
     * @param object 对象
     * @return true-是 false-否
     */
    public static boolean isNotEmpty(final Object object) {
        return !isEmpty(object);
    }

    /**
     * <pre>{@code
     *         Assert.assertEquals(ObjectUtils.defaultIfNull(null, "a"), "a");
     *         Assert.assertEquals(ObjectUtils.defaultIfNull("b", "a"), "b");
     * }</pre>
     *
     * @param obj        待判定是否为null的对象
     * @param defaultObj 默认对象
     * @param <T>        数据类型
     * @return 符合条件的对象
     */
    public static <T> T defaultIfNull(final T obj, final T defaultObj) {
        return obj == null ? defaultObj : obj;
    }
}
