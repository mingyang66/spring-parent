package com.yaomy.control.common.control.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @Description: 手机号码工具了
 * @Version: 1.0
 */
public class PhoneUtils {
    /**
     * @Description 隐藏手机号码中间四位
     * @Version  1.0
     */
    public static String hidden(String phone){
        if(StringUtils.isEmpty(phone)){
            return null;
        }
        //$1 $2 表示正则表达式里面的第一个和第二个，也就是括号里面的内容
        return StringUtils.replacePattern(phone,"(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    public static void main(String[] args) {
        System.out.println(hidden("183"));
    }
}
