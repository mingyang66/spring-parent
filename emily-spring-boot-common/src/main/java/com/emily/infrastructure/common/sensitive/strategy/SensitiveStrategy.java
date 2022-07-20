package com.emily.infrastructure.common.sensitive.strategy;

import java.util.function.Function;

/**
 * @Description :  脱敏策略，枚举类
 * 正则表达式：
 *   \S 代表一个非空白字符
 *   \s 表示一个空白字符（空格、tab、换页符等）
 *   \w 匹配一个字母、数字、下划线，等价于[A-Za-z0-9]
 *   \W 匹配一个非字母、数字、下划线，等价于[^A-Za-z0-9]
 * @Author :  Emily
 * @CreateDate :  Created in 2022/7/19 5:25 下午
 */
public enum SensitiveStrategy {
    /**
     * 默认
     */
    DEFAULT(s -> "--隐藏--"),
    /**
     * 用户名
     */
    USERNAME(s -> s.replaceAll("(\\S)\\S*", "$1**")),
    /**
     * 身份证
     */
    ID_CARD(s -> s.replaceAll("(\\d{4})\\d{10}(\\w{4})", "$1****$2")),
    /**
     * 手机号
     */
    PHONE(s -> s.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));


    private final Function<String, String> desensitizer;

    SensitiveStrategy(Function<String, String> desensitizer) {
        this.desensitizer = desensitizer;
    }

    public Function<String, String> desensitizer() {
        return desensitizer;
    }
}
