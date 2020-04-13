package com.sgrain.boot.web.exception;


import com.sgrain.boot.common.enums.AppHttpStatus;
import com.sgrain.boot.common.exception.BusinessException;
import com.sgrain.boot.common.po.BaseResponse;
import com.sgrain.boot.common.utils.LoggerUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
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

    private Environment environment;
    /**
     * Debug调试key
     */
    private static final String DEBUG_KEY = "spring.sgrain.log-aop.debug";
    /**
     * @RequestBody请求body缺失异常
     */
    private static final String REQUEST_BODY = "Required request body is missing";
    /**
     * @RequestBody请求body缺失message提示
     */
    private static final String REQUEST_BODY_MESSAGE = "注解@RequestBody标识的请求体缺失";

    public ExceptionAdviceHandler(Environment environment){
        this.environment = environment;
    }
    /**
     * 未知异常
     */
    @ExceptionHandler(value = Exception.class)
    public BaseResponse unKnowExceptionHandler(Exception e) {
        if(environment.getProperty(DEBUG_KEY, Boolean.class, false)){
            debugMsg(e);
        }
        StackTraceElement[] elements = e.getStackTrace();
        String message = StringUtils.EMPTY;
        if(elements.length > 0){
            StackTraceElement element = elements[0];
            message = StringUtils.join("类", element.getClassName(), ".", element.getMethodName(), "类的第", element.getLineNumber(), "行发生", e.toString(), "异常");
        }
        if(StringUtils.isBlank(message)){
            message = e.toString();
        }
        return BaseResponse.createResponse(AppHttpStatus.FAILED.getStatus(), message);
    }

    /**
     * 运行时异常
     */
    @ExceptionHandler(value = RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(RuntimeException e) {
        if(environment.getProperty(DEBUG_KEY, Boolean.class, false)){
            debugMsg(e);
        }
        StackTraceElement[] elements = e.getStackTrace();
        String message = StringUtils.EMPTY;
        if(elements.length > 0){
            StackTraceElement element = elements[0];
            message = StringUtils.join("类", element.getClassName(), ".", element.getMethodName(), "类的第", element.getLineNumber(), "行发生", e.toString(), "异常");
        }
        if(StringUtils.isBlank(message)){
            message = e.toString();
        }
        return BaseResponse.createResponse(AppHttpStatus.FAILED.getStatus(), message);
    }

    /**
     * 空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public BaseResponse nullPointerExceptionHandler(NullPointerException e) {
        if(environment.getProperty(DEBUG_KEY, Boolean.class, false)){
            debugMsg(e);
        }
        StackTraceElement[] elements = e.getStackTrace();
        String message = StringUtils.EMPTY;
        if(elements.length > 0){
            StackTraceElement element = elements[0];
            message = StringUtils.join("类", element.getClassName(), ".", element.getMethodName(), "类的第", element.getLineNumber(), "行发生", e.toString(), "异常");
        }
        if(StringUtils.isBlank(message)){
            message = e.toString();
        }
        return BaseResponse.createResponse(AppHttpStatus.NULL_POINTER_EXCEPTION.getStatus(), message);
    }

    /**
     * 类型转换异常
     */
    @ExceptionHandler(ClassCastException.class)
    public BaseResponse classCastExceptionHandler(ClassCastException e) {
        if(environment.getProperty(DEBUG_KEY, Boolean.class, false)){
            debugMsg(e);
        }
        StackTraceElement[] elements = e.getStackTrace();
        String message = StringUtils.EMPTY;
        if(elements.length > 0){
            StackTraceElement element = elements[0];
            message = StringUtils.join("类", element.getClassName(), ".", element.getMethodName(), "类的第", element.getLineNumber(), "行发生", e.toString(), "异常");
        }
        if(StringUtils.isBlank(message)){
            message = e.toString();
        }
        return BaseResponse.createResponse(AppHttpStatus.CLASS_CAST_EXCEPTION.getStatus(), message);
    }

    /**
     * IO异常
     */
    @ExceptionHandler(IOException.class)
    public BaseResponse iOExceptionHandler(IOException e) {
        if(environment.getProperty(DEBUG_KEY, Boolean.class, false)){
            debugMsg(e);
        }
        StackTraceElement[] elements = e.getStackTrace();
        String message = StringUtils.EMPTY;
        if(elements.length > 0){
            StackTraceElement element = elements[0];
            message = StringUtils.join("类", element.getClassName(), ".", element.getMethodName(), "类的第", element.getLineNumber(), "行发生", e.toString(), "异常");
        }
        if(StringUtils.isBlank(message)){
            message = e.toString();
        }
        return BaseResponse.createResponse(AppHttpStatus.IO_EXCEPTION.getStatus(), message);
    }

    /**
     * 数组越界异常
     */
    @ExceptionHandler(IndexOutOfBoundsException.class)
    public BaseResponse indexOutOfBoundsExceptionHandler(IndexOutOfBoundsException e) {
        if(environment.getProperty(DEBUG_KEY, Boolean.class, false)){
            debugMsg(e);
        }
        StackTraceElement[] elements = e.getStackTrace();
        String message = StringUtils.EMPTY;
        if(elements.length > 0){
            StackTraceElement element = elements[0];
            message = StringUtils.join("类", element.getClassName(), ".", element.getMethodName(), "类的第", element.getLineNumber(), "行发生", e.toString(), "异常");
        }
        if(StringUtils.isBlank(message)){
            message = e.toString();
        }
        return BaseResponse.createResponse(AppHttpStatus.INDEX_OUTOF_BOUNDS_EXCEPTION.getStatus(), message);
    }

    /**
     * 参数类型不匹配
     */
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public BaseResponse requestTypeMismatch(MethodArgumentTypeMismatchException e){
        if(environment.getProperty(DEBUG_KEY, Boolean.class, false)){
            debugMsg(e);
        }
        String message = StringUtils.join("参数类型不匹配，参数", e.getName(), "类型必须为", e.getRequiredType());
        return BaseResponse.createResponse(AppHttpStatus.METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTIION.getStatus(), message);
    }
    /**
     * 缺少参数
     */
    @ExceptionHandler({MissingServletRequestParameterException.class})
    public BaseResponse requestMissingServletRequest(MissingServletRequestParameterException e) {
        if(environment.getProperty(DEBUG_KEY, Boolean.class, false)){
            debugMsg(e);
        }
        String message= StringUtils.join("缺少必要参数，参数名称为", e.getParameterName());
        return BaseResponse.createResponse(AppHttpStatus.MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION.getStatus(), message);
    }
    /**
     * 请求method不匹配
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public BaseResponse requestMissingServletRequest(HttpRequestMethodNotSupportedException e) {
        if(environment.getProperty(DEBUG_KEY, Boolean.class, false)){
            debugMsg(e);
        }
        String message = StringUtils.join("不支持", e.getMethod(), "方法，支持", StringUtils.join(e.getSupportedMethods(), ","), "类型");
        return BaseResponse.createResponse(AppHttpStatus.HTTP_REQUEST_METHOD_NOT_SUPPORTED_EXCEPTION.getStatus(), message);
    }
    /**
     *
     * 类方法中@RequestBody类型参数数据类型转换异常
     */
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public BaseResponse httpMessageNotReadableException(HttpMessageNotReadableException e){
        if(environment.getProperty(DEBUG_KEY, Boolean.class, false)){
            debugMsg(e);
        }
        String message = e.getMessage();
        if(StringUtils.contains(message, REQUEST_BODY)){
            message = REQUEST_BODY_MESSAGE;
        }
        return BaseResponse.createResponse(AppHttpStatus.PARAM_EXCEPTION.getStatus(), message);
    }

    /**
     *
     * 类方法参数异常
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public BaseResponse methodArgumentNotValidException(MethodArgumentNotValidException e){
        if(environment.getProperty(DEBUG_KEY, Boolean.class, false)){
            debugMsg(e);
        }
        BindingResult bindingResult = e.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        String message = StringUtils.join(fieldError.getDefaultMessage());
        return BaseResponse.createResponse(AppHttpStatus.PARAM_EXCEPTION.getStatus(), message);
    }
    /**
     * 如果代理异常调用方法将会抛出此异常
     */
    @ExceptionHandler(UndeclaredThrowableException.class)
    public BaseResponse undeclaredThrowableException(UndeclaredThrowableException e) {
        String message = StringUtils.EMPTY;
        Throwable throwable = e.getCause().getCause();
        if(environment.getProperty(DEBUG_KEY, Boolean.class, false)){
            debugMsg(e);
        }
        StackTraceElement[] elements = throwable.getStackTrace();
        if(elements.length > 0){
            StackTraceElement element = elements[0];
            message = StringUtils.join("类", element.getClassName(), ".", element.getMethodName(), "的第", element.getLineNumber(), "行发生", throwable.toString(), "异常");
        }
        if(StringUtils.isBlank(message)){
            message = e.toString();
        }
        return BaseResponse.createResponse(AppHttpStatus.FAILED.getStatus(), message);
    }

    /**
     * 业务异常处理类
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public BaseResponse undeclaredThrowableException(BusinessException e) {
        if(environment.getProperty(DEBUG_KEY, Boolean.class, false)){
            debugMsg(e);
        }
        return BaseResponse.createResponse(e.getStatus(), e.getErrorMessage());
    }
    /**
     * @Description 打印错误日志信息
     * @Version  1.0
     */
    private void debugMsg(Throwable e){
        String message = e.toString();
        StackTraceElement[] elements = e.getStackTrace();
        for(int i=0;i<elements.length;i++){
            StackTraceElement element = elements[i];
            if(i == 0){
                message = StringUtils.join(element.toString(), " ", message);
            } else {
                message = StringUtils.join(message, "\n", element.toString());
            }
        }
        LoggerUtils.error(ExceptionAdviceHandler.class, message);
    }
}

