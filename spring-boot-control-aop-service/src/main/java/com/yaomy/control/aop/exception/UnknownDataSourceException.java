package com.yaomy.control.aop.exception;

/**
 * @Description: 未知数据源异常
 * @Version: 1.0
 */
public class UnknownDataSourceException extends RuntimeException{
    public UnknownDataSourceException(String message){
        super(message);
    }
}
