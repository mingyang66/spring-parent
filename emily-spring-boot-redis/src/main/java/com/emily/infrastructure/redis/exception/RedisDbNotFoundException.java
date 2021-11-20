package com.emily.infrastructure.redis.exception;

import com.emily.infrastructure.common.enums.AppHttpStatus;
import com.emily.infrastructure.common.exception.BasicException;

/**
 * @program: spring-parent
 * @description: Redis数据库不存在异常
 * @author: Emily
 * @create: 2021/11/20
 */
public class RedisDbNotFoundException extends BasicException {
    public RedisDbNotFoundException(String message) {
        super(AppHttpStatus.DATABASE_EXCEPTION.getStatus(), message);
    }
}
