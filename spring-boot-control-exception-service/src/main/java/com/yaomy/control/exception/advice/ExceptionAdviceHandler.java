package com.yaomy.control.exception.advice;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaomy.control.common.control.conf.PropertyService;
import com.yaomy.control.common.control.enums.HttpStatus;
import com.yaomy.control.common.control.po.BaseResponse;
import com.yaomy.control.logback.utils.LoggerUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
public final class ExceptionAdviceHandler {
    /**
     * 日志配置文件key
     */
    private static final String LOGBACK_CONFIG = "logging.config";
    /**
     * 日志文件
     */
    private static final String LOGBACK_FILENAME = "classpath:logback-control.xml";
    /**
     * @RequestBody请求body缺失异常
     */
    private static final String REQUEST_BODY = "Required request body is missing";
    /**
     * @RequestBody请求body缺失message提示
     */
    public static final String REQUEST_BODY_MESSAGE = "注解@RequestBody标识的请求体缺失";
    /**
     * 加载配置文件属性
     */
    @Autowired
    private PropertyService propertyService;
    /**
     * 未知异常
     */
    @ExceptionHandler(value = Exception.class)
    public BaseResponse unKnowExceptionHandler(Exception e) {
        printErrorMessage(e);
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
        printErrorMessage(e);
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
        printErrorMessage(e);
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
        printErrorMessage(e);
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
        printErrorMessage(e);
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
        printErrorMessage(e);
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
        printErrorMessage(e);
        String message = StringUtils.join("参数类型不匹配，参数", e.getName(), "类型必须为", e.getRequiredType());
        return BaseResponse.createResponse(HttpStatus.METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTIION.getStatus(), message);
    }
    /**
     * 缺少参数
     */
    @ExceptionHandler({MissingServletRequestParameterException.class})
    public BaseResponse requestMissingServletRequest(MissingServletRequestParameterException e) {
        printErrorMessage(e);
        String message= StringUtils.join("缺少必要参数，参数名称为", e.getParameterName());
        return BaseResponse.createResponse(HttpStatus.MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION.getStatus(), message);
    }
    /**
     * 请求method不匹配
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public BaseResponse requestMissingServletRequest(HttpRequestMethodNotSupportedException e) {
        printErrorMessage(e);
        String message = StringUtils.join("不支持", e.getMethod(), "方法，支持", StringUtils.join(e.getSupportedMethods(), ","), "类型");
        return BaseResponse.createResponse(HttpStatus.HTTP_REQUEST_METHOD_NOT_SUPPORTED_EXCEPTION.getStatus(), message);
    }
    /**
     *
     * 控制器方法中@RequestBody类型参数数据类型转换异常
     */
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public BaseResponse httpMessageNotReadableException(HttpMessageNotReadableException e){
        printErrorMessage(e);
        String message = e.getMessage();
        if(StringUtils.contains(message, REQUEST_BODY)){
            message = REQUEST_BODY_MESSAGE;
        }
        return BaseResponse.createResponse(HttpStatus.PARAM_EXCEPTION.getStatus(), message);
    }

    /**
     *
     * 控制器方法参数异常
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public BaseResponse methodArgumentNotValidException(MethodArgumentNotValidException e){
        printErrorMessage(e);
        BindingResult bindingResult = e.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        String message = StringUtils.join(fieldError.getDefaultMessage());
        return BaseResponse.createResponse(HttpStatus.PARAM_EXCEPTION.getStatus(), message);
    }
    /**
     * 如果代理异常调用方法将会抛出此异常
     */
    @ExceptionHandler(UndeclaredThrowableException.class)
    public BaseResponse undeclaredThrowableException(UndeclaredThrowableException e) {
        String message = StringUtils.EMPTY;
        Throwable throwable = e.getCause().getCause();
        printErrorMessage(throwable);
        StackTraceElement[] elements = throwable.getStackTrace();
        if(elements.length > 0){
            StackTraceElement element = elements[0];
            message = StringUtils.join("控制器", element.getClassName(), ".", element.getMethodName(), "的第", element.getLineNumber(), "行发生", throwable.toString(), "异常");
        }
        if(StringUtils.isBlank(message)){
            message = e.toString();
        }
        return BaseResponse.createResponse(HttpStatus.FAILED.getStatus(), message);
    }
    /**
     * @Description 打印错误日志信息
     * @Version  1.0
     */
    private void printErrorMessage(Throwable e){
        if(StringUtils.equalsIgnoreCase(propertyService.getProperty(LOGBACK_CONFIG), LOGBACK_FILENAME)){
            String message = e.toString();
            for (StackTraceElement element:e.getStackTrace()){
                message = StringUtils.join(message, "\n", element.toString());
            }
            LoggerUtil.error(ExceptionAdviceHandler.class, message);
        } else {
            e.printStackTrace();
        }
    }
}

