package com.emily.infrastructure.desensitize.test;

import com.emily.infrastructure.desensitize.DataMaskUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 字符串脱敏
 *
 * @author Emily
 * @since :  Created in 2023/5/27 3:18 PM
 */
public class DataMaskUtilsTest {

    @Test
    public void chineseName() {
        Assertions.assertEquals(DataMaskUtils.maskChineseName("孙玉婷"), "孙**");
        Assertions.assertEquals(DataMaskUtils.maskIdCard("123"), "123");
        Assertions.assertEquals(DataMaskUtils.maskIdCard("1234"), "1**4");
        Assertions.assertEquals(DataMaskUtils.maskIdCard("12345"), "1**45");
        Assertions.assertEquals(DataMaskUtils.maskIdCard("412822185703252531"), "412***********2531");
        Assertions.assertEquals(DataMaskUtils.maskIdCard("412822185703252"), "412********3252");
    }

    @Test
    public void phoneNumber() {
        Assertions.assertEquals(DataMaskUtils.maskPhoneNumber("18221120687"), "182*****0687");
        Assertions.assertEquals(DataMaskUtils.maskPhoneNumber("08518221120687"), "085182*****0687");
        Assertions.assertEquals(DataMaskUtils.maskPhoneNumber("008618221120687"), "0086182*****0687");
        Assertions.assertEquals(DataMaskUtils.maskPhoneNumber("123"), "123");
        Assertions.assertEquals(DataMaskUtils.maskPhoneNumber("1234"), "1**4");
        Assertions.assertEquals(DataMaskUtils.maskPhoneNumber("12345"), "1**45");
        Assertions.assertEquals(DataMaskUtils.maskPhoneNumber("123456"), "1**456");
        Assertions.assertEquals(DataMaskUtils.maskPhoneNumber("1234567"), "1**4567");
        Assertions.assertEquals(DataMaskUtils.maskPhoneNumber("12345678"), "12****78");
        Assertions.assertEquals(DataMaskUtils.maskPhoneNumber("123456789"), "12****789");
        Assertions.assertEquals(DataMaskUtils.maskPhoneNumber("1234567890"), "12****7890");

        Assertions.assertEquals(DataMaskUtils.maskMiddleTwoPortions("123"), "123");
        Assertions.assertEquals(DataMaskUtils.maskMiddleTwoPortions("1234"), "1**4");
        Assertions.assertEquals(DataMaskUtils.maskMiddleTwoPortions("12345"), "1**45");
        Assertions.assertEquals(DataMaskUtils.maskMiddleTwoPortions("123456"), "1**456");
        Assertions.assertEquals(DataMaskUtils.maskMiddleTwoPortions("1234567"), "1**4567");
        Assertions.assertEquals(DataMaskUtils.maskMiddleTwoPortions("12345678"), "12****78");
        Assertions.assertEquals(DataMaskUtils.maskMiddleTwoPortions("123456789"), "12****789");
        Assertions.assertEquals(DataMaskUtils.maskMiddleTwoPortions("1234567890"), "12****7890");
    }

    @Test
    public void email() {
        Assertions.assertEquals(DataMaskUtils.maskEmail("123"), "123");
        Assertions.assertEquals(DataMaskUtils.maskEmail("@qq.com"), "@qq.com");
        Assertions.assertEquals(DataMaskUtils.maskEmail("1@qq.com"), "1***@qq.com");
        Assertions.assertEquals(DataMaskUtils.maskEmail("12@qq.com"), "1***2@qq.com");
        Assertions.assertEquals(DataMaskUtils.maskEmail("123@qq.com"), "1***3@qq.com");
        Assertions.assertEquals(DataMaskUtils.maskEmail("1234@qq.com"), "1***4@qq.com");
        Assertions.assertEquals(DataMaskUtils.maskEmail("12345@qq.com"), "1***5@qq.com");
        Assertions.assertEquals(DataMaskUtils.maskEmail("123456@qq.com"), "1***6@qq.com");
        Assertions.assertEquals(DataMaskUtils.maskEmail("1234567@qq.com"), "1***7@qq.com");
        Assertions.assertEquals(DataMaskUtils.maskEmail("12345678@qq.com"), "1***8@qq.com");
        Assertions.assertEquals(DataMaskUtils.maskEmail("123456789@qq.com"), "1***9@qq.com");
        Assertions.assertEquals(DataMaskUtils.maskEmail("1234567890@qq.com"), "1***0@qq.com");
    }

    @Test
    public void bankCard() {
        Assertions.assertEquals(DataMaskUtils.maskBankCard("123"), "123");
        Assertions.assertEquals(DataMaskUtils.maskBankCard("123456789"), "12****789");
        Assertions.assertEquals(DataMaskUtils.maskBankCard("1234567890"), "12****7890");
        Assertions.assertEquals(DataMaskUtils.maskBankCard("123456789012"), "123456**9012");
        Assertions.assertEquals(DataMaskUtils.maskBankCard("1234567890123456"), "123456******3456");
        Assertions.assertEquals(DataMaskUtils.maskBankCard("62270010000000000000"), "622700**********0000");
    }

    @Test
    public void address() {
        Assertions.assertEquals(DataMaskUtils.maskAddress("上海市徐汇区宛平南路186号美罗城4层", 0), "上海市徐汇区*************");
        Assertions.assertEquals(DataMaskUtils.maskAddress("北京市海淀区人民大会堂", -2), "北京市********");
        Assertions.assertEquals(DataMaskUtils.maskAddress("北京市海淀区人民大会堂", 2), "北京*********");
        Assertions.assertEquals(DataMaskUtils.maskAddress("上海市徐汇区宛平南路186号美罗城4层", 10), "上海市徐汇区宛平南路*********");
    }
}
