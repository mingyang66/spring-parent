package com.emily.infrastructure.i18n;

import java.io.*;
import java.util.Map;

/**
 * 基础字典
 *
 * @author Emily
 */
public class I18nDictionary {
    /**
     * 字典映射关系
     */
    private Map<Character, Character> languageMap;
    /**
     * 节点管理器
     */
    private I18nNodeManager<String> i18nNodeManager;
    /**
     * 最大长度
     */
    private int maxLen = 2;

    public I18nDictionary() {
    }

    public I18nDictionary(Map<Character, Character> languageMap, I18nNodeManager<String> i18nNodeManager, int maxLen) {
        this.languageMap = languageMap;
        this.i18nNodeManager = i18nNodeManager;
        this.maxLen = maxLen;
    }

    public Map<Character, Character> getLanguageMap() {
        return languageMap;
    }

    public I18nNodeManager<String> getI18nNodeManager() {
        return i18nNodeManager;
    }

    public int getMaxLen() {
        return maxLen;
    }

    /**
     * 将字符串转换为约定的语言类型
     *
     * @param str 字符串
     * @return 转换后的字符串
     */
    public String convert(String str) {
        Reader in = new StringReader(str);
        Writer out = new StringWriter();
        try {
            convert(in, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toString();
    }

    /**
     * 将指定字符串转换为对应语言类型
     *
     * @param reader 指定字符串
     * @param writer 目标字符串
     * @throws IOException
     */
    public void convert(Reader reader, Writer writer) throws IOException {
        PushbackReader in = new PushbackReader(new BufferedReader(reader), this.getMaxLen());
        char[] buf = new char[this.getMaxLen()];
        int len;
        while ((len = in.read(buf)) != -1) {
            I18nNode<String> node = this.getI18nNodeManager().match(buf, 0, len);
            if (node != null) {
                int offset = node.getLevel();
                writer.write(node.getValue());
                in.unread(buf, offset, len - offset);
            } else {
                in.unread(buf, 0, len);
                char ch = (char) in.read();
                writer.write(convert(ch));
            }
        }
    }

    /**
     * 获取字符映射到的语言类型
     *
     * @param ch 字符
     * @return 关联的语言
     */
    public Character convert(char ch) {
        Character tmp = this.getLanguageMap().get(ch);
        if (tmp == null) {
            return ch;
        }
        return tmp;
    }
}
