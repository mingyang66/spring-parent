package com.emily.infrastructure.rpc.core.exception;

import com.emily.infrastructure.common.enums.AppHttpStatus;
import com.emily.infrastructure.common.exception.BaseException;

/**
 * @program: emily-infrustructure
 * @description: 对象池异常
 * @author: Emily
 * @create: 2021/10/18
 */
public class ObjectPoolException extends BaseException {

    public ObjectPoolException(){
        super(AppHttpStatus.INIT_EXCEPTION.getStatus(), "对象池初始化异常");
    }
}
