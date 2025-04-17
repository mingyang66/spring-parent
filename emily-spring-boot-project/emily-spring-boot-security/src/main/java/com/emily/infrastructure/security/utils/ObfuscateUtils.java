package com.emily.infrastructure.security.utils;

/**
 * 基于ASCII码位移字符串混淆
 *
 * @author :  Emily
 * @since :  2025/4/12 下午2:37
 */
public class ObfuscateUtils {
    /**
     * 混淆：字符ASCII码+1
     */
    public static String obfuscate(String input) {
        char[] chars = input.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) (chars[i] + 1);
        }
        return new String(chars);
    }

    /**
     * 恢复：字符ASCII码-1
     */
    public static String deobfuscate(String obfuscated) {
        char[] chars = obfuscated.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) (chars[i] - 1);
        }
        return new String(chars);
    }

}
