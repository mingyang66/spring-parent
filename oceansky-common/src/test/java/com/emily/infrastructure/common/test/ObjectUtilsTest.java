package com.emily.infrastructure.common.test;

import com.emily.infrastructure.common.ObjectUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

/**
 * @Description :  对象工具类单元测试
 * @Author :  Emily
 * @CreateDate :  Created in 2023/6/13 10:33 AM
 */
public class ObjectUtilsTest {
    @Test
    public void isNull() {
        Assert.assertTrue(ObjectUtils.isNull(null));
        Assert.assertFalse(ObjectUtils.isNull(""));
        Assert.assertFalse(ObjectUtils.isNull("a"));
        Assert.assertFalse(ObjectUtils.isNull(new Object()));

        Assert.assertFalse(ObjectUtils.isNotNull(null));
        Assert.assertTrue(ObjectUtils.isNotNull(""));
        Assert.assertTrue(ObjectUtils.isNotNull("a"));
        Assert.assertTrue(ObjectUtils.isNotNull(new Object()));
    }

    @Test
    public void isEmpty() {
        Assert.assertTrue(ObjectUtils.isEmpty(null));
        Assert.assertTrue(ObjectUtils.isEmpty(""));
        Assert.assertTrue(ObjectUtils.isEmpty(new String[]{}));
        Assert.assertTrue(ObjectUtils.isEmpty(Collections.emptyMap()));
        Assert.assertTrue(ObjectUtils.isEmpty(Collections.emptyList()));

        Assert.assertFalse(ObjectUtils.isNotEmpty(null));
        Assert.assertFalse(ObjectUtils.isNotEmpty(""));
        Assert.assertFalse(ObjectUtils.isNotEmpty(new String[]{}));
        Assert.assertFalse(ObjectUtils.isNotEmpty(Collections.emptyMap()));
        Assert.assertFalse(ObjectUtils.isNotEmpty(Collections.emptyList()));
    }
}
