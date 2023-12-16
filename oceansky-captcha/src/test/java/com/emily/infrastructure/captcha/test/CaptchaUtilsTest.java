package com.emily.infrastructure.captcha.test;

import org.junit.jupiter.api.Test;

import java.util.Random;

/**
 * 单元测试类
 *
 * @author Emily
 * @since Created in 2023/5/28 2:23 PM
 */
public class CaptchaUtilsTest {
    private static final Random RANDOM = new Random();

    @Test
    public void random() {
        for (int i = 0; i < 100; i++) {
            int b = RANDOM.nextInt(255);
            System.out.println(b);

        }
    }
}
