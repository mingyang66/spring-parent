package com.emily.infrastructure.test.security;

import com.emily.infrastructure.security.utils.ObfuscateUtils;
import com.emily.infrastructure.security.utils.ReversibleShufflerUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

/**
 * @author :  Emily
 * @since :  2025/4/5 下午9:56
 */
public class ReversibleShufflerTest {
    @Test
    public void tesetShuffler() {
        String originStr = "SDDS1 834567「8{92}1  566";
        long seed = System.currentTimeMillis();
        String shuffle = ReversibleShufflerUtils.shuffle(originStr, seed);
        String reversibleShuffle = ReversibleShufflerUtils.reverseShuffle(shuffle, seed);
        Assertions.assertEquals(originStr, reversibleShuffle);
    }

    @Test
    public void tesetObfuscate() {
        String originStr = "你好{sdf}哈喽你好，！@#￥%nasdf";
        String obfuscate = ObfuscateUtils.obfuscate(originStr);
        System.out.println(obfuscate);
        String deObfuscate = ObfuscateUtils.deobfuscate(obfuscate);
        Assertions.assertEquals(originStr, deObfuscate);
    }
    @Test
    public void test(){
       System.out.println(formatNumber("0.1"));
       System.out.println(formatNumber("0.01"));
       System.out.println(formatNumber("0.0100"));
       System.out.println(formatNumber("2"));
       System.out.println(formatNumber("2."));
       System.out.println(formatNumber("2.0"));
       System.out.println(formatNumber("2.0000"));
       System.out.println(formatNumber("2.000012"));
       System.out.println(formatNumber("2.0000120"));
    }
    public static String formatNumber(String input) {
        BigDecimal num = new BigDecimal(input);
        // 去除末尾零并转换为普通字符串
        return num.stripTrailingZeros().toPlainString();
    }
}
