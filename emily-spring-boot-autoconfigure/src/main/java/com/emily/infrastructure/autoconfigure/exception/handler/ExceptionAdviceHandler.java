package com.emily.infrastructure.autoconfigure.exception.handler;


import com.emily.infrastructure.common.base.BaseResponse;
import com.emily.infrastructure.common.enums.AppHttpStatus;
import com.emily.infrastructure.common.exception.BusinessException;
import com.emily.infrastructure.common.exception.CustomException;
import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.logback.factory.LogbackFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
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
    public BaseResponse unKnowExceptionHandler(Exception e) {
        recordErrorInfo(e);
        return BaseResponse.buildResponse(AppHttpStatus.EXCEPTION, e.getMessage());
    }

    /**
     * 运行时异常
     */
    @ExceptionHandler(value = RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(RuntimeException e) {
        recordErrorInfo(e);
        return BaseResponse.buildResponse(AppHttpStatus.RUNTIME_EXCEPTION.getStatus(), e.getMessage());
    }

    /**
     * 空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public BaseResponse nullPointerExceptionHandler(NullPointerException e) {
        recordErrorInfo(e);
        return BaseResponse.buildResponse(AppHttpStatus.NULL_POINTER_EXCEPTION);
    }

    /**
     * 类型转换异常
     */
    @ExceptionHandler(ClassCastException.class)
    public BaseResponse classCastExceptionHandler(ClassCastException e) {
        recordErrorInfo(e);
        return BaseResponse.buildResponse(AppHttpStatus.CLASS_CAST_EXCEPTION);
    }

    /**
     * IO异常
     */
    @ExceptionHandler(IOException.class)
    public BaseResponse ioExceptionHandler(IOException e) {
        recordErrorInfo(e);
        return BaseResponse.buildResponse(AppHttpStatus.IO_EXCEPTION);
    }

    /**
     * 数组越界异常
     */
    @ExceptionHandler(IndexOutOfBoundsException.class)
    public BaseResponse indexOutOfBoundsExceptionHandler(IndexOutOfBoundsException e) {
        recordErrorInfo(e);
        return BaseResponse.buildResponse(AppHttpStatus.INDEX_OUT_OF_BOUNDS_EXCEPTION);
    }

    /**
     * 参数类型不匹配
     */
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public BaseResponse requestTypeMismatch(MethodArgumentTypeMismatchException e) {
        recordErrorInfo(e);
        return BaseResponse.buildResponse(AppHttpStatus.PARAMETER_MISMATCH_EXCEPTION);
    }

    /**
     * 缺少参数
     */
    @ExceptionHandler({MissingServletRequestParameterException.class})
    public BaseResponse requestMissingServletRequest(MissingServletRequestParameterException e) {
        recordErrorInfo(e);
        return BaseResponse.buildResponse(AppHttpStatus.PARAMETER_MISSING_EXCEPTION);
    }


    /**
     * 控制器方法中@RequestBody类型参数数据类型转换异常
     */
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public BaseResponse httpMessageNotReadableException(HttpMessageNotReadableException e) {
        recordErrorInfo(e);
        return BaseResponse.buildResponse(AppHttpStatus.PARAMETER_TYPE_EXCEPTION);
    }

    /**
     * 控制器方法参数Validate异常
     *
     * @throws BindException
     * @throws MethodArgumentNotValidException
     */
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    public BaseResponse validModelBindException(BindException e) {
        recordErrorInfo(e);
        return BaseResponse.buildResponse(AppHttpStatus.PARAMETER_EXCEPTION);
    }

    /**
     * 请求method不匹配
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public BaseResponse requestMissingServletRequest(HttpRequestMethodNotSupportedException e) {
        recordErrorInfo(e);
        return BaseResponse.buildResponse(AppHttpStatus.METHOD_SUPPORTED_EXCEPTION);
    }

    /**
     * 数字格式异常
     */
    @ExceptionHandler(NumberFormatException.class)
    public BaseResponse numberFormatException(NumberFormatException e) {
        recordErrorInfo(e);
        return BaseResponse.buildResponse(AppHttpStatus.NUMBER_FORMAT_EXCEPTION);
    }

    /**
     * 业务异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessThrowableException(BusinessException e) {
        recordErrorInfo(e);
        return BaseResponse.buildResponse(e.getStatus(), e.getMessage());
    }

    /**
     * 自定义异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public Object businessThrowableException(CustomException e) {
        recordErrorInfo(e);
        return e.getBean();
    }

    /**
     * 获取异常堆栈信息并记录到error文件中
     */
    public static void recordErrorInfo(Throwable ex) {
        String errorMsg = PrintExceptionInfo.printErrorInfo(ex);
        if (ex instanceof BusinessException) {
            BusinessException systemException = (BusinessException) ex;
            errorMsg = MessageFormat.format("业务异常，异常码是【{0}】，异常消息是【{1}】，异常详情{2}", systemException.getStatus(), systemException.getMessage(), errorMsg);
        }
        LogbackFactory.error(PrintExceptionInfo.class, errorMsg);
    }
}

