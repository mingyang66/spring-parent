package com.emily.infrastructure.common.exception;

/**
 * @program: spring-parent
 * @description: 自定义异常处理类
 * @author: Emily
 * @create: 2021/09/12
 */
public abstract class CustomException extends Exception {
    /**
     * 获取返回的bean对象
     * @return
     */
    public abstract Object getBean();
}
