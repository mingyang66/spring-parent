package com.emily.infrastructure.test.exception;

import com.emily.infrastructure.autoconfigure.exception.handler.DefaultGlobalExceptionHandler;
import com.emily.infrastructure.common.entity.BaseResponse;
import com.emily.infrastructure.common.exception.HttpStatusType;
import org.apache.ibatis.binding.BindingException;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description :  自定义异常
 * @Author :  Emily
 * @CreateDate :  Created in 2022/7/8 1:26 下午
 */
@RestControllerAdvice
public class CustomExceptionHandler extends DefaultGlobalExceptionHandler {
    /**
     * 数据库异常
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BadSqlGrammarException.class)
    public BaseResponse bad1SqlGrammarException(BadSqlGrammarException e, HttpServletRequest request) {
        recordErrorMsg(e, request);
        return BaseResponse.build(HttpStatusType.ILLEGAL_ACCESS);
    }

    /**
     * Mybatis数据库异常
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = MyBatisSystemException.class)
    public BaseResponse myBatisSystemExceptionHandler(MyBatisSystemException e, HttpServletRequest request) {
        recordErrorMsg(e, request);
        return BaseResponse.build(HttpStatusType.ILLEGAL_ACCESS);
    }

    /**
     * Mybatis数据库异常
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = BindingException.class)
    public BaseResponse bindingExceptionHandler(BindingException e, HttpServletRequest request) {
        recordErrorMsg(e, request);
        return BaseResponse.build(HttpStatusType.ILLEGAL_ACCESS);
    }


    /**
     * 事务非法访问
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(TransientDataAccessException.class)
    public BaseResponse transientDataAccessException(TransientDataAccessException e, HttpServletRequest request) {
        recordErrorMsg(e, request);
        return BaseResponse.build(HttpStatusType.ILLEGAL_ACCESS);
    }

    /**
     * 重复键值异常
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(DuplicateKeyException.class)
    public BaseResponse duplicateKeyException(DuplicateKeyException e, HttpServletRequest request) {
        recordErrorMsg(e, request);
        return BaseResponse.build(HttpStatusType.ILLEGAL_ACCESS);
    }

    /**
     * 数据库非法访问
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(DataAccessException.class)
    public BaseResponse dataAccessException(DataAccessException e, HttpServletRequest request) {
        recordErrorMsg(e, request);
        return BaseResponse.build(HttpStatusType.ILLEGAL_ACCESS);
    }
}
