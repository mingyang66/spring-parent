package com.sgrain.boot.web.exception;


import com.sgrain.boot.common.enums.AppHttpStatus;
import com.sgrain.boot.common.exception.BusinessException;
import com.sgrain.boot.common.po.ResponseData;
import com.sgrain.boot.common.utils.LoggerUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * @Description: 控制并统一处理异常类
 * @ExceptionHandler标注的方法优先级问题，它会找到异常的最近继承关系，也就是继承关系最浅的注解方法
 * @Version: 1.0
 */
@SuppressWarnings("all")
@RestControllerAdvice
public final class ExceptionAdviceHandler {

    /**
     * @RequestBody请求body缺失异常
     */
    private static final String REQUEST_BODY = "Required request body is missing";
    /**
     * @RequestBody请求body缺失message提示
     */
    private static final String REQUEST_BODY_MESSAGE = "注解@RequestBody标识的请求体缺失";

    /**
     * 未知异常
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseData unKnowExceptionHandler(Exception e) {
        ResponseData responseData = ResponseData.buildResponse(AppHttpStatus.EXCEPTION.getStatus(), e.getMessage());
        printErrorMessage(e);
        return responseData;
    }

    /**
     * 运行时异常
     */
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseData runtimeExceptionHandler(RuntimeException e) {
        ResponseData responseData = ResponseData.buildResponse(AppHttpStatus.RUNTIME_EXCEPTION.getStatus(), e.getMessage());
        printErrorMessage(e);
        return responseData;
    }

    /**
     * 空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseData nullPointerExceptionHandler(NullPointerException e) {
        ResponseData responseData = ResponseData.buildResponse(AppHttpStatus.NULL_POINTER_EXCEPTION.getStatus(), e.getMessage());
        printErrorMessage(e);
        return responseData;
    }

    /**
     * 类型转换异常
     */
    @ExceptionHandler(ClassCastException.class)
    public ResponseData classCastExceptionHandler(ClassCastException e) {
        ResponseData responseData = ResponseData.buildResponse(AppHttpStatus.CLASS_CAST_EXCEPTION.getStatus(), e.getMessage());
        printErrorMessage(e);
        return responseData;
    }

    /**
     * IO异常
     */
    @ExceptionHandler(IOException.class)
    public ResponseData iOExceptionHandler(IOException e) {
        ResponseData responseData = ResponseData.buildResponse(AppHttpStatus.IO_EXCEPTION);
        printErrorMessage(e);
        if (LoggerUtils.isDebug()) {
            StackTraceElement[] elements = e.getStackTrace();
            String message = StringUtils.EMPTY;
            if (elements.length > 0) {
                StackTraceElement element = elements[0];
                message = StringUtils.join("类", element.getClassName(), ".", element.getMethodName(), "类的第", element.getLineNumber(), "行发生", e.toString(), "异常");
                responseData.setMessage(message);
            }
        }

        return responseData;
    }

    /**
     * 数组越界异常
     */
    @ExceptionHandler(IndexOutOfBoundsException.class)
    public ResponseData indexOutOfBoundsExceptionHandler(IndexOutOfBoundsException e) {
        ResponseData responseData = ResponseData.buildResponse(AppHttpStatus.ARRAY_OUT_OF_BOUNDS_EXCEPTION);
        printErrorMessage(e);
        if (LoggerUtils.isDebug()) {
            StackTraceElement[] elements = e.getStackTrace();
            String message = StringUtils.EMPTY;
            if (elements.length > 0) {
                StackTraceElement element = elements[0];
                message = StringUtils.join("类", element.getClassName(), ".", element.getMethodName(), "类的第", element.getLineNumber(), "行发生", e.toString(), "异常");
                responseData.setMessage(message);
            }
        }

        return responseData;
    }

    /**
     * 参数类型不匹配
     */
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseData requestTypeMismatch(MethodArgumentTypeMismatchException e) {
        ResponseData responseData = ResponseData.buildResponse(AppHttpStatus.API_METHOD_PARAM_TYPE_EXCEPTIION);
        printErrorMessage(e);
        if (LoggerUtils.isDebug()) {
            String message = StringUtils.join("参数类型不匹配，参数", e.getName(), "类型必须为", e.getRequiredType());
            responseData.setMessage(message);
        }
        return responseData;
    }

    /**
     * 缺少参数
     */
    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseData requestMissingServletRequest(MissingServletRequestParameterException e) {
        ResponseData responseData = ResponseData.buildResponse(AppHttpStatus.API_PARAM_MISSING_EXCEPTION);
        printErrorMessage(e);
        if (LoggerUtils.isDebug()) {
            String message = StringUtils.join("缺少必要参数，参数名称为", e.getParameterName());
            responseData.setMessage(message);
        }
        return responseData;
    }

    /**
     * 请求method不匹配
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseData requestMissingServletRequest(HttpRequestMethodNotSupportedException e) {
        ResponseData responseData = ResponseData.buildResponse(AppHttpStatus.API_METHOD_NOT_SUPPORTED_EXCEPTION);
        printErrorMessage(e);
        if (LoggerUtils.isDebug()) {
            String message = StringUtils.join("不支持", e.getMethod(), "方法，支持", StringUtils.join(e.getSupportedMethods(), ","), "类型");
            responseData.setMessage(message);
        }
        return responseData;
    }

    /**
     * 类方法中@RequestBody类型参数数据类型转换异常
     */
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseData httpMessageNotReadableException(HttpMessageNotReadableException e) {
        ResponseData responseData = ResponseData.buildResponse(AppHttpStatus.API_PARAM_EXCEPTION);
        printErrorMessage(e);
        String message = e.getMessage();
        if (StringUtils.contains(message, REQUEST_BODY)) {
            message = REQUEST_BODY_MESSAGE;
            responseData.setMessage(message);
        }
        return responseData;
    }

    /**
     * 类方法参数异常
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseData methodArgumentNotValidException(MethodArgumentNotValidException e) {
        printErrorMessage(e);
        BindingResult bindingResult = e.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        String message = StringUtils.join(fieldError.getDefaultMessage());
        return ResponseData.buildResponse(AppHttpStatus.API_PARAM_EXCEPTION.getStatus(), message);
    }

    /**
     * 如果代理异常调用方法将会抛出此异常
     */
    @ExceptionHandler(UndeclaredThrowableException.class)
    public ResponseData undeclaredThrowableException(UndeclaredThrowableException e) {
        ResponseData responseData = ResponseData.buildResponse(AppHttpStatus.FAILED.getStatus());
        printErrorMessage(e);
        if (LoggerUtils.isDebug()) {
            String message = StringUtils.EMPTY;
            Throwable throwable = e.getCause().getCause();
            StackTraceElement[] elements = throwable.getStackTrace();
            if (elements.length > 0) {
                StackTraceElement element = elements[0];
                message = StringUtils.join("类", element.getClassName(), ".", element.getMethodName(), "的第", element.getLineNumber(), "行发生", throwable.toString(), "异常");
                responseData.setMessage(message);
            }
        }
        return responseData;
    }

    /**
     * 业务异常处理类
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseData undeclaredThrowableException(BusinessException e) {
        printBusinessErrorMessage(e);
        return ResponseData.buildResponse(e.getStatus(), e.getErrorMessage());
    }

    /**
     * @Description 打印错误日志信息
     * @Version 1.0
     */
    private void printErrorMessage(Throwable e) {
        String message = e.toString();
        StackTraceElement[] elements = e.getStackTrace();
        for (int i = 0; i < elements.length; i++) {
            StackTraceElement element = elements[i];
            if (i == 0) {
                message = StringUtils.join(element.toString(), " ", message);
            } else {
                message = StringUtils.join(message, "\n", element.toString());
            }
        }
        LoggerUtils.error(ExceptionAdviceHandler.class, message);
    }

    /**
     * 打印业务错误信息
     *
     * @param e
     */
    private void printBusinessErrorMessage(BusinessException e) {
        LoggerUtils.error(ExceptionAdviceHandler.class, StringUtils.join(e, " 【status】", e.getStatus(), ", 【errorMessage】", e.getErrorMessage()));
    }
}

