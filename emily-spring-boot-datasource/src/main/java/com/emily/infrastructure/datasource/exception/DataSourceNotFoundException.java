package com.emily.infrastructure.datasource.exception;

import com.emily.infrastructure.common.enums.AppHttpStatus;
import com.emily.infrastructure.common.exception.BasicException;

/**
 * @program: spring-parent
 * @description: 数据源不存在
 * @author: Emily
 * @create: 2021/11/20
 */
public class DataSourceNotFoundException extends BasicException {
    public DataSourceNotFoundException(String desc){
        super(AppHttpStatus.DATABASE_EXCEPTION.getStatus(), desc);
    }
}
