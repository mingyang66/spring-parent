package com.emily.framework.autoconfigure.exception;


import com.emily.framework.common.base.ResponseData;
import com.emily.framework.common.enums.AppHttpStatus;
import com.emily.framework.common.exception.BusinessException;
import com.emily.framework.common.exception.PrintExceptionInfo;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * @Description: 控制并统一处理异常类
 * @ExceptionHandler标注的方法优先级问题，它会找到异常的最近继承关系，也就是继承关系最浅的注解方法
 * @Version: 1.0
 */
@RestControllerAdvice
public class ExceptionAdviceHandler {
    /**
     * 未知异常
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseData unKnowExceptionHandler(Exception e) {
        PrintExceptionInfo.printErrorInfo(e, true);
        return ResponseData.buildResponse(AppHttpStatus.EXCEPTION);
    }

    /**
     * 运行时异常
     */
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseData runtimeExceptionHandler(RuntimeException e) {
        PrintExceptionInfo.printErrorInfo(e, true);
        return ResponseData.buildResponse(AppHttpStatus.RUNTIME_EXCEPTION.getStatus(), e.getMessage());
    }

    /**
     * 空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseData nullPointerExceptionHandler(NullPointerException e) {
        PrintExceptionInfo.printErrorInfo(e, true);
        return ResponseData.buildResponse(AppHttpStatus.NULL_POINTER_EXCEPTION);
    }

    /**
     * 类型转换异常
     */
    @ExceptionHandler(ClassCastException.class)
    public ResponseData classCastExceptionHandler(ClassCastException e) {
        PrintExceptionInfo.printErrorInfo(e, true);
        return ResponseData.buildResponse(AppHttpStatus.CLASS_CAST_EXCEPTION);
    }

    /**
     * IO异常
     */
    @ExceptionHandler(IOException.class)
    public ResponseData iOExceptionHandler(IOException e) {
        PrintExceptionInfo.printErrorInfo(e, true);
        return ResponseData.buildResponse(AppHttpStatus.IO_EXCEPTION);
    }

    /**
     * 数组越界异常
     */
    @ExceptionHandler(IndexOutOfBoundsException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseData indexOutOfBoundsExceptionHandler(IndexOutOfBoundsException e) {
        PrintExceptionInfo.printErrorInfo(e, true);
        return ResponseData.buildResponse(AppHttpStatus.INDEX_OUT_OF_BOUNDS_EXCEPTION);
    }

    /**
     * 参数类型不匹配
     */
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseData requestTypeMismatch(MethodArgumentTypeMismatchException e) {
        PrintExceptionInfo.printErrorInfo(e, true);
        return ResponseData.buildResponse(AppHttpStatus.METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTION);
    }

    /**
     * 缺少参数
     */
    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseData requestMissingServletRequest(MissingServletRequestParameterException e) {
        PrintExceptionInfo.printErrorInfo(e, true);
        return ResponseData.buildResponse(AppHttpStatus.MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION);
    }

    /**
     * 请求method不匹配
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseData requestMissingServletRequest(HttpRequestMethodNotSupportedException e) {
        PrintExceptionInfo.printErrorInfo(e, true);
        return ResponseData.buildResponse(AppHttpStatus.HTTP_REQUEST_METHOD_NOT_SUPPORTED_EXCEPTION);
    }

    /**
     * 控制器方法中@RequestBody类型参数数据类型转换异常
     */
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseData httpMessageNotReadableException(HttpMessageNotReadableException e) {
        PrintExceptionInfo.printErrorInfo(e, true);
        return ResponseData.buildResponse(AppHttpStatus.HTTP_MESSAGE_NOT_READABLE_EXCEPTION);
    }

    /**
     * 控制器方法参数异常
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseData methodArgumentNotValidException(MethodArgumentNotValidException e) {
        PrintExceptionInfo.printErrorInfo(e, true);
        return ResponseData.buildResponse(AppHttpStatus.METHOD_ARGUMENT_NOT_VALID_EXCEPTION);
    }

    /**
     * 控制器方法参数Validate异常
     */
    @ExceptionHandler({BindException.class})
    public ResponseData validModelBindException(BindException e) {
        PrintExceptionInfo.printErrorInfo(e, true);
        return ResponseData.buildResponse(AppHttpStatus.BIND_EXCEPTION.getStatus(), e.getMessage());
    }

    /**
     * @Description 如果代理异常调用方法将会抛出此异常
     * @Author
     * @Date 2019/9/2 16:43
     * @Version 1.0
     */
    @ExceptionHandler(UndeclaredThrowableException.class)
    public ResponseData undeclaredThrowableException(UndeclaredThrowableException e) {
        PrintExceptionInfo.printErrorInfo(e, true);
        return ResponseData.buildResponse(AppHttpStatus.UNDECLARED_THROWABLE_EXCEPTION);
    }

    /**
     * 数字格式异常
     */
    @ExceptionHandler(NumberFormatException.class)
    public ResponseData numberFormatException(NumberFormatException e){
        PrintExceptionInfo.printErrorInfo(e, true);
        return ResponseData.buildResponse(AppHttpStatus.NUMBER_FORMAT_EXCEPTION);
    }
    /**
     * @Description 如果代理异常调用方法将会抛出此异常
     * @Author
     * @Date 2019/9/2 16:43
     * @Version 1.0
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseData businessThrowableException(BusinessException e) {
        PrintExceptionInfo.printErrorInfo(e, true);
        return ResponseData.buildResponse(e.getStatus(), e.getErrorMessage());
    }


}

