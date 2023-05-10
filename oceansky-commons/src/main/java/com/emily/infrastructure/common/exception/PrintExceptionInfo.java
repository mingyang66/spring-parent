package com.emily.infrastructure.common.exception;

import com.emily.infrastructure.common.constant.CharacterInfo;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Objects;

/**
 * @author Emily
 * @program: spring-parent
 * @description: 获取打印异常日志信息
 * @create: 2020/08/24
 */
public class PrintExceptionInfo {
    /**
     * @Description 打印错误日志信息
     * @Version 1.0
     */
    public static String printErrorInfo(Throwable ex) {
        if (Objects.isNull(ex)) {
            return "";
        }
        //获取异常说明
        String message = ex.getMessage();
        //异常说明为空则获取异常类名
        message = StringUtils.isEmpty(message) ? ex.getClass().getName() : message;
        //获取异常堆栈信息
        StackTraceElement[] elements = ex.getStackTrace();
        for (int i = 0; i < elements.length; i++) {
            StackTraceElement element = elements[i];
            if (i == 0) {
                message = MessageFormat.format("{0}{1}{2}", element.toString(), " ", message);
            } else {
                message = MessageFormat.format("{0}{1}{2}", message, CharacterInfo.ENTER, element.toString());
            }
        }
        return message;
    }

    /**
     * 输出所有异常
     */
    public static String printErrorInfo(Throwable[] ex) {
        if (Objects.isNull(ex)) {
            return "";
        }
        String message = "";
        for (int i = 0; i < ex.length; i++) {
            message = MessageFormat.format("{0}{1}{2}", message, CharacterInfo.ENTER, printErrorInfo(ex[i]));
        }
        return message;
    }

}
