package com.emily.boot.common.exception;

import com.emily.boot.common.utils.constant.CharacterUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @program: spring-parent
 * @description: 获取打印异常日志信息
 * @create: 2020/08/24
 */
public class PrintExceptionInfo {
    /**
     * @Description 打印错误日志信息
     * @Version 1.0
     */
    public static String printErrorInfo(Throwable e) {
        String message = e.toString();
        StackTraceElement[] elements = e.getStackTrace();
        for (int i = 0; i < elements.length; i++) {
            StackTraceElement element = elements[i];
            if (i == 0) {
                message = StringUtils.join(element.toString(), " ", message);
            } else {
                message = StringUtils.join(message, CharacterUtils.ENTER, element.toString());
            }
        }
        return message;
    }
}
