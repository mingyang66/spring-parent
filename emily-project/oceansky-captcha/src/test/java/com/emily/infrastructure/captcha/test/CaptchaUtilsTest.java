package com.emily.infrastructure.captcha.test;

import com.emily.infrastructure.captcha.Captcha;
import com.emily.infrastructure.captcha.CaptchaUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * 单元测试类
 *
 * @author Emily
 * @since Created in 2023/5/28 2:23 PM
 */
public class CaptchaUtilsTest {

    @Test
    public void createDigit() throws IOException {
        Captcha s = CaptchaUtils.createDigit(120, 45, 6, 30);
        Assertions.assertEquals(s.getCode().length(), 6);
    }
}
