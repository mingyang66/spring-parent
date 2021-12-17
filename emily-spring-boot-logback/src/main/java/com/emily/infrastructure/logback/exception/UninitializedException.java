package com.emily.infrastructure.logback.exception;

import com.emily.infrastructure.common.enums.AppHttpStatus;
import com.emily.infrastructure.common.exception.BasicException;

/**
 * @program: spring-parent
 * @description: 未初始化异常
 * @author: Emily
 * @create: 2021/12/17
 */
public class UninitializedException extends BasicException {
    public UninitializedException() {
        super(AppHttpStatus.INIT_EXCEPTION.getStatus(), "日志上下文未初始化");
    }
}
