package com.emily.infrastructure.redis.exception;

import com.emily.infrastructure.common.enums.AppHttpStatus;
import com.emily.infrastructure.common.exception.BasicException;

import java.text.MessageFormat;

/**
 * @program: spring-parent
 * @description: Redis数据库不存在异常
 * @author: Emily
 * @create: 2021/11/20
 */
public class RedisDbNotFoundException extends BasicException {
    public RedisDbNotFoundException(String redisMark) {
        super(AppHttpStatus.DATABASE_EXCEPTION.getStatus(), MessageFormat.format("Redis数据库标识【{0}】对应的数据库不存在", redisMark));
    }
}
