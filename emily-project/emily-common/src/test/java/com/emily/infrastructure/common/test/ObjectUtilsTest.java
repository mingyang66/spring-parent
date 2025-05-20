package com.emily.infrastructure.common.test;

import com.emily.infrastructure.common.ObjectUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * 对象工具类单元测试
 *
 * @author Emily
 * @since Created in 2023/6/13 10:33 AM
 */
public class ObjectUtilsTest {
    @Test
    public void isNull() {
        Assertions.assertTrue(ObjectUtils.isNull(null));
        Assertions.assertFalse(ObjectUtils.isNull(""));
        Assertions.assertFalse(ObjectUtils.isNull("a"));
        Assertions.assertFalse(ObjectUtils.isNull(new Object()));

        Assertions.assertFalse(ObjectUtils.isNotNull(null));
        Assertions.assertTrue(ObjectUtils.isNotNull(""));
        Assertions.assertTrue(ObjectUtils.isNotNull("a"));
        Assertions.assertTrue(ObjectUtils.isNotNull(new Object()));
    }

    @Test
    public void isEmpty() {
        Assertions.assertTrue(ObjectUtils.isEmpty(null));
        Assertions.assertTrue(ObjectUtils.isEmpty(""));
        Assertions.assertTrue(ObjectUtils.isEmpty(new String[]{}));
        Assertions.assertTrue(ObjectUtils.isEmpty(Collections.emptyMap()));
        Assertions.assertTrue(ObjectUtils.isEmpty(Collections.emptyList()));

        Assertions.assertFalse(ObjectUtils.isNotEmpty(null));
        Assertions.assertFalse(ObjectUtils.isNotEmpty(""));
        Assertions.assertFalse(ObjectUtils.isNotEmpty(new String[]{}));
        Assertions.assertFalse(ObjectUtils.isNotEmpty(Collections.emptyMap()));
        Assertions.assertFalse(ObjectUtils.isNotEmpty(Collections.emptyList()));
    }

    @Test
    public void defaultIfNull() {
        Assertions.assertEquals(ObjectUtils.defaultIfNull(null, "a"), "a");
        Assertions.assertEquals(ObjectUtils.defaultIfNull("b", "a"), "b");
    }
}
