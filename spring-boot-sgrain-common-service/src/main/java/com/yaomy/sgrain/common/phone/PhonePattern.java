package com.yaomy.sgrain.common.phone;


import org.apache.commons.lang3.StringUtils;

public class PhonePattern {
    public static void main(String[] args) {
        //$1 $2 表示正则表达式里面的第一个和第二个，也就是括号里面的内容
        System.out.println(StringUtils.replacePattern("12321120687","(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
        System.out.println("12321120687".replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
        System.out.println("1425665665525895689".replaceAll("(\\d{4})\\d{4}(\\d{4})\\d{4}(\\d{3})", "$1****$2****$3"));
    }
}
