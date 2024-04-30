package com.emily.infrastructure.common.test;

import com.emily.infrastructure.common.ComputeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 计算工具类单元测试
 *
 * @author :  Emily
 * @since :  2024/4/30 9:11 PM
 */
public class ComputeUtilsTest {
    @Test
    public void getEffectiveValue() {
        Assertions.assertNull(ComputeUtils.getEffectiveValue(null));
        Assertions.assertEquals(ComputeUtils.getEffectiveValue(""), "");
        Assertions.assertEquals(ComputeUtils.getEffectiveValue(" "), " ");
        Assertions.assertEquals(ComputeUtils.getEffectiveValue("3.14159265777777"), "3.14159265777777");
        Assertions.assertEquals(ComputeUtils.getEffectiveValue("3.141590000000"), "3.14159");
        Assertions.assertEquals(ComputeUtils.getEffectiveValue("3.0000"), "3");
    }
}
