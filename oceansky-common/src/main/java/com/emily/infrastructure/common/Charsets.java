package com.emily.infrastructure.common;

import java.nio.charset.Charset;

/**
 * @Description :  字符转换类
 * @Author :  Emily
 * @CreateDate :  Created in 2023/6/9 4:02 PM
 */
public class Charsets {
    /**
     * 编码对象如果为null，则获取系统默认编码，否则返回源编码
     *
     * @param charset 编码对象
     * @return 编码对象
     */
    public static Charset toCharset(final Charset charset) {
        return charset == null ? Charset.defaultCharset() : charset;
    }

    /**
     * 将字符串编码转换为编码对象
     *
     * @param charsetName 编码名称
     * @return 编码对象
     */
    static Charset toCharset(final String charsetName) {
        return charsetName == null ? Charset.defaultCharset() : Charset.forName(charsetName);
    }

    /**
     * 字符串编码如果为null，则返回系统默认编码，否则原值返回
     *
     * @param charsetName 编码名称
     * @return 编码对象
     */
    static String toCharsetName(final String charsetName) {
        return charsetName == null ? Charset.defaultCharset().name() : charsetName;
    }
}
