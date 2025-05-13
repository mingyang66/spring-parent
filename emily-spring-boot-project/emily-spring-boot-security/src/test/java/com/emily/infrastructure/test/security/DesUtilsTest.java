package com.emily.infrastructure.test.security;

import com.emily.infrastructure.security.utils.DesUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author :  Emily
 * @since :  2025/5/11 下午2:01
 */
public class DesUtilsTest {
    @Test
    void testEncrypt() {
        Assertions.assertEquals("EgIOHMYk2mc=", DesUtils.encrypt("123", "12345678", "65432112"));
        Assertions.assertEquals("123", DesUtils.decrypt("123", "12345678", "65432112"));
    }
}
