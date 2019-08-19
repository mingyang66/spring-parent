package com.yaomy.control.exception.advice;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.yaomy.control.exception.enums.HttpStatus;
import com.yaomy.control.exception.po.BaseResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Description: 控制并统一处理异常类
 * @ExceptionHandler标注的方法优先级问题，它会找到异常的最近继承关系，也就是继承关系最浅的注解方法
 * @Version: 1.0
 */
@RestControllerAdvice
public final class ExceptionAdviceHandler {

    private ObjectError objectError;

    /**
     * 未知异常
     */
    @ExceptionHandler(value = Exception.class)
    public BaseResponse unKnowExceptionHandler(Exception e) {
        e.printStackTrace();
        StackTraceElement[] elements = e.getStackTrace();
        String message = StringUtils.EMPTY;
        if(elements.length > 0){
            StackTraceElement element = elements[0];
            message = StringUtils.join("控制器", element.getClassName(), ".", element.getMethodName(), "类的第", element.getLineNumber(), "行发生", e.toString(), "异常");
        }
        if(StringUtils.isBlank(message)){
            message = e.toString();
        }
        return BaseResponse.createResponse(HttpStatus.UNKNOW_EXCEPTION.getStatus(), message);
    }

    /**
     * 运行时异常
     */
    @ExceptionHandler(value = RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(RuntimeException e) {
        e.printStackTrace();
        StackTraceElement[] elements = e.getStackTrace();
        String message = StringUtils.EMPTY;
        if(elements.length > 0){
            StackTraceElement element = elements[0];
            message = StringUtils.join("控制器", element.getClassName(), ".", element.getMethodName(), "类的第", element.getLineNumber(), "行发生", e.toString(), "异常");
        }
        if(StringUtils.isBlank(message)){
            message = e.toString();
        }
        return BaseResponse.createResponse(HttpStatus.RUNTIME_EXCEPTION.getStatus(), message);
    }

    /**
     * 空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public BaseResponse nullPointerExceptionHandler(NullPointerException e) {
        e.printStackTrace();
        StackTraceElement[] elements = e.getStackTrace();
        String message = StringUtils.EMPTY;
        if(elements.length > 0){
            StackTraceElement element = elements[0];
            message = StringUtils.join("控制器", element.getClassName(), ".", element.getMethodName(), "类的第", element.getLineNumber(), "行发生", e.toString(), "异常");
        }
        if(StringUtils.isBlank(message)){
            message = e.toString();
        }
        return BaseResponse.createResponse(HttpStatus.NULL_POINTER_EXCEPTION.getStatus(), message);
    }

    /**
     * 类型转换异常
     */
    @ExceptionHandler(ClassCastException.class)
    public BaseResponse classCastExceptionHandler(ClassCastException e) {
        e.printStackTrace();
        StackTraceElement[] elements = e.getStackTrace();
        String message = StringUtils.EMPTY;
        if(elements.length > 0){
            StackTraceElement element = elements[0];
            message = StringUtils.join("控制器", element.getClassName(), ".", element.getMethodName(), "类的第", element.getLineNumber(), "行发生", e.toString(), "异常");
        }
        if(StringUtils.isBlank(message)){
            message = e.toString();
        }
        return BaseResponse.createResponse(HttpStatus.CLASS_CAST_EXCEPTION.getStatus(), message);
    }

    /**
     * IO异常
     */
    @ExceptionHandler(IOException.class)
    public BaseResponse iOExceptionHandler(IOException e) {
        e.printStackTrace();
        StackTraceElement[] elements = e.getStackTrace();
        String message = StringUtils.EMPTY;
        if(elements.length > 0){
            StackTraceElement element = elements[0];
            message = StringUtils.join("控制器", element.getClassName(), ".", element.getMethodName(), "类的第", element.getLineNumber(), "行发生", e.toString(), "异常");
        }
        if(StringUtils.isBlank(message)){
            message = e.toString();
        }
        return BaseResponse.createResponse(HttpStatus.IO_EXCEPTION.getStatus(), message);
    }

    /**
     * 数组越界异常
     */
    @ExceptionHandler(IndexOutOfBoundsException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponse indexOutOfBoundsExceptionHandler(IndexOutOfBoundsException e) {
        e.printStackTrace();
        StackTraceElement[] elements = e.getStackTrace();
        String message = StringUtils.EMPTY;
        if(elements.length > 0){
            StackTraceElement element = elements[0];
            message = StringUtils.join("控制器", element.getClassName(), ".", element.getMethodName(), "类的第", element.getLineNumber(), "行发生", e.toString(), "异常");
        }
        if(StringUtils.isBlank(message)){
            message = e.toString();
        }
        return BaseResponse.createResponse(HttpStatus.INDEX_OUTOF_BOUNDS_EXCEPTION.getStatus(), message);
    }

    /**
     * 参数类型不匹配
     */
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public BaseResponse requestTypeMismatch(MethodArgumentTypeMismatchException e){
        return BaseResponse.createResponse(HttpStatus.METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTIION.getStatus(), "参数类型不匹配，参数"+e.getName()+"类型必须为"+e.getRequiredType());
    }
    /**
     * 缺少参数
     */
    @ExceptionHandler({MissingServletRequestParameterException.class})
    public BaseResponse requestMissingServletRequest(MissingServletRequestParameterException e) {
        return BaseResponse.createResponse(HttpStatus.MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION.getStatus(), "缺少必要参数，参数名称为"+e.getParameterName());
    }
    /**
     * 请求method不匹配
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public BaseResponse requestMissingServletRequest(HttpRequestMethodNotSupportedException e) {
        return BaseResponse.createResponse(HttpStatus.HTTP_REQUEST_METHOD_NOT_SUPPORTED_EXCEPTION.getStatus(), "不支持"+e.getMethod()+"方法，支持"+ StringUtils.join(e.getSupportedMethods(), ",")+"类型");
    }

    /**
     *
     * 控制器方法中@RequestBody类型参数数据类型转换异常
     */
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public BaseResponse httpMessageNotReadableException(HttpMessageNotReadableException e, WebRequest wq){
        e.printStackTrace();
        Throwable throwable = e.getRootCause();
        return BaseResponse.createResponse(HttpStatus.PARAM_EXCEPTION.getStatus(), throwable.getMessage());
    }

    /**
     *
     * 控制器方法参数异常
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public BaseResponse methodArgumentNotValidException(MethodArgumentNotValidException e){
        e.printStackTrace();
        BindingResult bindingResult = e.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        String message = StringUtils.join(fieldError.getDefaultMessage());
        return BaseResponse.createResponse(HttpStatus.PARAM_EXCEPTION.getStatus(), message);
    }

}

