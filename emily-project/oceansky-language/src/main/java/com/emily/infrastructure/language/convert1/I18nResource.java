package com.emily.infrastructure.language.convert1;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简繁体字典资源加载
 *
 * @author Emily
 */
public class I18nResource {
    public static final String SHARP = "#";
    public static final String EQUAL = "=";
    private static final Map<String, I18nDictionary> dictionaryMap = new HashMap<>(8, 1);

    /**
     * 获取词典对象
     *
     * @param i18nType 字典枚举类型
     * @return 字典对象
     */
    public static I18nDictionary getDictionary(I18nType i18nType) {
        I18nDictionary dictionary = dictionaryMap.get(i18nType.getCode());
        if (dictionary != null) {
            return dictionary;
        }
        switch (i18nType) {
            case SIMPLE_TO_TRADITIONAL:
                dictionary = I18nResource.loadDictionary("/dic/s2t.txt", false);
                break;
            case TRADITIONAL_TO_SIMPLE:
                dictionary = I18nResource.loadDictionary("/dic/t2s.txt", false);
                break;
            default:
                throw new IllegalArgumentException("非法数据");
        }
        dictionaryMap.put(i18nType.getCode(), dictionary);
        return dictionary;
    }

    /**
     * 从文件中读取字典资源
     *
     * @param classpath 字典路径
     * @param reverse   是否反转映射关系
     * @return 字典对象
     */
    public static I18nDictionary loadDictionary(String classpath, boolean reverse) {
        //单字语言映射
        Map<Character, Character> languageMap = new ConcurrentHashMap<>(8192);
        I18nNodeManager<String> i18nNodeManager = new I18nNodeManager<>();
        int maxLen = 2;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(newClassPathReader(classpath));
            String line;
            String[] pair;
            while (null != (line = reader.readLine())) {
                // 空行和注释直接扔掉
                if (line.length() == 0 || line.startsWith(SHARP)) {
                    continue;
                }
                pair = split(line, EQUAL);
                if (pair.length < 2) {
                    continue;
                }
                if (reverse) {
                    if (pair[0].length() == 1 && pair[1].length() == 1) {
                        languageMap.put(pair[1].charAt(0), pair[0].charAt(0));
                    } else {
                        maxLen = Math.max(pair[0].length(), maxLen);
                        i18nNodeManager.add(pair[1], pair[0]);
                    }
                } else {
                    if (pair[0].length() == 1 && pair[1].length() == 1) {
                        languageMap.put(pair[0].charAt(0), pair[1].charAt(0));
                    } else {
                        maxLen = Math.max(pair[0].length(), maxLen);
                        i18nNodeManager.add(pair[0], pair[1]);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new I18nDictionary(languageMap, i18nNodeManager, maxLen);
    }

    public static Reader newClassPathReader(String classpath) {
        InputStream is = I18nResource.class.getResourceAsStream(classpath);
        return new InputStreamReader(is, StandardCharsets.UTF_8);
    }

    /**
     * 字符串分割
     *
     * @param str   待分割字符串
     * @param split 分隔符
     * @return 字符串数组
     */
    private static String[] split(String str, String split) {
        int index = str.indexOf(split);
        if (index < 0) {
            return new String[]{str};
        } else {
            return new String[]{str.substring(0, index), str.substring(index + 1)};
        }
    }
}
