package com.emily.infrastructure.i18n.test;

import com.emily.infrastructure.i18n.I18nChineseHelper;
import org.junit.Test;

/**
 * @Description :  测试类
 * @Author :  Emily
 * @CreateDate :  Created in 2023/5/14 19:18 PM
 */
public class I18nChineseHelperTest {

    @Test
    public void test() {
        String text = "田晓霞、孙少平、孙少安、孙玉厚、孙玉婷、贺丰英、贺秀莲";
        String s = I18nChineseHelper.convertToTraditionalChinese(text);
        System.out.println(s);
        String s1 = I18nChineseHelper.convertToSimplifiedChinese(s);
        System.out.println(s1);
    }

    @Test
    public void test1() {
        System.out.println(I18nChineseHelper.convertToTraditionalChinese("标志"));
        System.out.println(I18nChineseHelper.convertToTraditionalChinese("〇"));
    }
}
