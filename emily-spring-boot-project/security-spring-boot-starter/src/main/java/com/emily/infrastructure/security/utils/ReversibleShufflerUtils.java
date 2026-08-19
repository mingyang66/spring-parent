package com.emily.infrastructure.security.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 字符串混排工具
 *
 * @author :  Emily
 * @since :  2025/4/5 下午4:54
 */
public class ReversibleShufflerUtils {
    /**
     * 混排字符串
     *
     * @param input 原始字符串
     * @param seed  随机种子（用于生成置换规则）
     * @return 混排后的字符串
     */
    public static String shuffle(String input, long seed) {
        if (input == null || input.length() <= 1) {
            return input;
        }
        Random rand = new Random(seed);
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < input.length(); i++) {
            indices.add(i);
        }
        // 生成随机置换索引
        Collections.shuffle(indices, rand);
        // 构建混排后的字符数组
        char[] shuffled = new char[input.length()];
        for (int i = 0; i < indices.size(); i++) {
            shuffled[i] = input.charAt(indices.get(i));
        }

        return new String(shuffled);
    }

    /**
     * 逆混排字符串
     *
     * @param shuffled 混排后的字符串
     * @param seed     生成置换规则的随机种子
     * @return 原始字符串
     */
    public static String reverseShuffle(String shuffled, long seed) {
        if (shuffled == null || shuffled.length() <= 1) {
            return shuffled;
        }
        Random rand = new Random(seed);
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < shuffled.length(); i++) {
            indices.add(i);
        }
        // 重新生成相同的置换索引
        Collections.shuffle(indices, rand);
        // 构建逆置换映射表
        char[] original = new char[shuffled.length()];
        for (int i = 0; i < indices.size(); i++) {
            original[indices.get(i)] = shuffled.charAt(i);
        }
        return new String(original);
    }
}
