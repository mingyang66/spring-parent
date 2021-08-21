package com.emily.infrastructure.autoconfigure.exception;


import com.emily.infrastructure.common.base.SimpleResponse;
import com.emily.infrastructure.common.enums.AppHttpStatus;
import com.emily.infrastructure.common.exception.BusinessException;
import com.emily.infrastructure.common.exception.SystemException;
import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.common.utils.RequestUtils;
import com.emily.infrastructure.common.utils.constant.CharacterUtils;
import com.emily.infrastructure.logback.factory.LogbackFactory;
import org.apache.commons.lang3.StringUtils;
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
import java.text.MessageFormat;

/**
 * @author Emily
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
    public SimpleResponse unKnowExceptionHandler(Exception e) {
        recordErrorInfo(e);
        return SimpleResponse.buildResponse(AppHttpStatus.EXCEPTION, RequestUtils.getTime());
    }

    /**
     * 运行时异常
     */
    @ExceptionHandler(value = RuntimeException.class)
    public SimpleResponse runtimeExceptionHandler(RuntimeException e) {
        recordErrorInfo(e);
        return SimpleResponse.buildResponse(AppHttpStatus.RUNTIME_EXCEPTION.getStatus(), e.getMessage(), RequestUtils.getTime());
    }

    /**
     * 空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public SimpleResponse nullPointerExceptionHandler(NullPointerException e) {
        recordErrorInfo(e);
        return SimpleResponse.buildResponse(AppHttpStatus.NULL_POINTER_EXCEPTION, RequestUtils.getTime());
    }

    /**
     * 类型转换异常
     */
    @ExceptionHandler(ClassCastException.class)
    public SimpleResponse classCastExceptionHandler(ClassCastException e) {
        recordErrorInfo(e);
        return SimpleResponse.buildResponse(AppHttpStatus.CLASS_CAST_EXCEPTION, RequestUtils.getTime());
    }

    /**
     * IO异常
     */
    @ExceptionHandler(IOException.class)
    public SimpleResponse ioExceptionHandler(IOException e) {
        recordErrorInfo(e);
        return SimpleResponse.buildResponse(AppHttpStatus.IO_EXCEPTION, RequestUtils.getTime());
    }

    /**
     * 数组越界异常
     */
    @ExceptionHandler(IndexOutOfBoundsException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
    public SimpleResponse indexOutOfBoundsExceptionHandler(IndexOutOfBoundsException e) {
        recordErrorInfo(e);
        return SimpleResponse.buildResponse(AppHttpStatus.INDEX_OUT_OF_BOUNDS_EXCEPTION, RequestUtils.getTime());
    }

    /**
     * 参数类型不匹配
     */
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public SimpleResponse requestTypeMismatch(MethodArgumentTypeMismatchException e) {
        recordErrorInfo(e);
        return SimpleResponse.buildResponse(AppHttpStatus.METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTION, RequestUtils.getTime());
    }

    /**
     * 缺少参数
     */
    @ExceptionHandler({MissingServletRequestParameterException.class})
    public SimpleResponse requestMissingServletRequest(MissingServletRequestParameterException e) {
        recordErrorInfo(e);
        return SimpleResponse.buildResponse(AppHttpStatus.MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION, RequestUtils.getTime());
    }

    /**
     * 请求method不匹配
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public SimpleResponse requestMissingServletRequest(HttpRequestMethodNotSupportedException e) {
        recordErrorInfo(e);
        return SimpleResponse.buildResponse(AppHttpStatus.HTTP_REQUEST_METHOD_NOT_SUPPORTED_EXCEPTION, RequestUtils.getTime());
    }

    /**
     * 控制器方法中@RequestBody类型参数数据类型转换异常
     */
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public SimpleResponse httpMessageNotReadableException(HttpMessageNotReadableException e) {
        recordErrorInfo(e);
        return SimpleResponse.buildResponse(AppHttpStatus.HTTP_MESSAGE_NOT_READABLE_EXCEPTION, RequestUtils.getTime());
    }

    /**
     * 控制器方法参数异常
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public SimpleResponse methodArgumentNotValidException(MethodArgumentNotValidException e) {
        recordErrorInfo(e);
        return SimpleResponse.buildResponse(AppHttpStatus.METHOD_ARGUMENT_NOT_VALID_EXCEPTION, RequestUtils.getTime());
    }

    /**
     * 控制器方法参数Validate异常
     */
    @ExceptionHandler({BindException.class})
    public SimpleResponse validModelBindException(BindException e) {
        recordErrorInfo(e);
        return SimpleResponse.buildResponse(AppHttpStatus.BIND_EXCEPTION.getStatus(), e.getMessage(), RequestUtils.getTime());
    }

    /**
     * @Description 如果代理异常调用方法将会抛出此异常
     * @Author
     * @Date 2019/9/2 16:43
     * @Version 1.0
     */
    @ExceptionHandler(UndeclaredThrowableException.class)
    public SimpleResponse undeclaredThrowableException(UndeclaredThrowableException e) {
        recordErrorInfo(e);
        return SimpleResponse.buildResponse(AppHttpStatus.UNDECLARED_THROWABLE_EXCEPTION, RequestUtils.getTime());
    }

    /**
     * 数字格式异常
     */
    @ExceptionHandler(NumberFormatException.class)
    public SimpleResponse numberFormatException(NumberFormatException e) {
        recordErrorInfo(e);
        return SimpleResponse.buildResponse(AppHttpStatus.NUMBER_FORMAT_EXCEPTION, RequestUtils.getTime());
    }

    /**
     * @Description 如果代理异常调用方法将会抛出此异常
     * @Author
     * @Date 2019/9/2 16:43
     * @Version 1.0
     */
    @ExceptionHandler(SystemException.class)
    public SimpleResponse systemThrowableException(SystemException e) {
        recordErrorInfo(e);
        return SimpleResponse.buildResponse(e.getStatus(), e.getErrorMessage(), RequestUtils.getTime());
    }

    /**
     * 业务异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public SimpleResponse businessThrowableException(SystemException e) {
        recordErrorInfo(e);
        return SimpleResponse.buildResponse(e.getStatus(), e.getErrorMessage(), RequestUtils.getTime());
    }

    /**
     * 获取异常堆栈信息并记录到error文件中
     */
    public static void recordErrorInfo(Throwable ex) {
        String errorMsg = PrintExceptionInfo.printErrorInfo(ex);
        if (ex instanceof SystemException) {
            SystemException systemException = (SystemException) ex;
            errorMsg = MessageFormat.format("业务异常，异常码是【{0}】，异常消息是【{1}】，异常详情{2}", systemException.getStatus(), systemException.getErrorMessage(), errorMsg);
        } else if (ex instanceof BusinessException) {
            BusinessException businessException = (BusinessException) ex;
            errorMsg = MessageFormat.format("业务异常，异常码是【{0}】，异常消息是【{1}】，异常详情{2}", businessException.getStatus(), businessException.getErrorMessage(), errorMsg);
        }
        LogbackFactory.error(PrintExceptionInfo.class, errorMsg);
    }
}

