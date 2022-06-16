package com.emily.infrastructure.rpc.core.exception;

import com.emily.infrastructure.common.enums.AppHttpStatus;
import com.emily.infrastructure.common.exception.BasicException;

/**
 * @program: spring-parent
 * @description: 对象池异常
 * @author: Emily
 * @create: 2021/10/18
 */
public class ObjectPoolException extends BasicException {

    public ObjectPoolException() {
        super(AppHttpStatus.ILLEGAL_DATA.getStatus(), "对象池初始化异常");
    }
}
