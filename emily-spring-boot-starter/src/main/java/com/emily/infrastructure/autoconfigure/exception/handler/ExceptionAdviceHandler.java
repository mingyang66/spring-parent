package com.emily.infrastructure.autoconfigure.exception.handler;


import com.emily.infrastructure.common.enums.AppHttpStatus;
import com.emily.infrastructure.common.exception.BasicException;
import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.core.entity.BaseResponse;
import com.emily.infrastructure.logger.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdviceHandler.class);

    /**
     * 未知异常
     */
    @ExceptionHandler(value = Exception.class)
    public BaseResponse exceptionHandler(Exception e) {
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
        return BaseResponse.buildResponse(AppHttpStatus.NULL_POINTER);
    }

    /**
     * 类型转换异常
     */
    @ExceptionHandler(ClassCastException.class)
    public BaseResponse classCastExceptionHandler(ClassCastException e) {
        recordErrorInfo(e);
        return BaseResponse.buildResponse(AppHttpStatus.ILLEGAL_CLASS_CONVERT);
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
    public BaseResponse indexOutOfBoundsException(IndexOutOfBoundsException e) {
        recordErrorInfo(e);
        return BaseResponse.buildResponse(AppHttpStatus.ILLEGAL_INDEX);
    }

    /**
     * API-参数类型不匹配
     */
    @ExceptionHandler({TypeMismatchException.class})
    public BaseResponse requestTypeMismatch(TypeMismatchException e) {
        recordErrorInfo(e);
        return BaseResponse.buildResponse(AppHttpStatus.MISMATCH_PARAMETER);
    }

    /**
     * API-缺少参数
     */
    @ExceptionHandler(MissingRequestValueException.class)
    public BaseResponse requestMissingServletRequest(MissingRequestValueException e) {
        recordErrorInfo(e);
        return BaseResponse.buildResponse(AppHttpStatus.MISSING_PARAMETER);
    }


    /**
     * API-控制器方法参数Validate异常
     *
     * @throws BindException
     * @throws MethodArgumentNotValidException
     */
    @ExceptionHandler({BindException.class, IllegalArgumentException.class, HttpMessageConversionException.class})
    public BaseResponse validModelBindException(BindException e) {
        recordErrorInfo(e);
        return BaseResponse.buildResponse(AppHttpStatus.ILLEGAL_PARAMETER);
    }

    /**
     * API-请求method不匹配
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public BaseResponse httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        recordErrorInfo(e);
        return BaseResponse.buildResponse(AppHttpStatus.ILLEGAL_METHOD);
    }

    /**
     * 数字格式异常
     */
    @ExceptionHandler(NumberFormatException.class)
    public BaseResponse numberFormatException(NumberFormatException e) {
        recordErrorInfo(e);
        return BaseResponse.buildResponse(AppHttpStatus.ILLEGAL_NUMBER_FORMAT);
    }

    /**
     * 业务异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BasicException.class)
    public BaseResponse basicException(BasicException e) {
        recordErrorInfo(e);
        return BaseResponse.buildResponse(e.getStatus(), e.getMessage());
    }


    /**
     * 获取异常堆栈信息并记录到error文件中
     */
    public static void recordErrorInfo(Throwable ex) {
        String errorMsg = PrintExceptionInfo.printErrorInfo(ex);
        if (ex instanceof BasicException) {
            BasicException systemException = (BasicException) ex;
            errorMsg = MessageFormat.format("业务异常，异常码是【{0}】，异常消息是【{1}】，异常详情{2}", systemException.getStatus(), systemException.getMessage(), errorMsg);
        }
        logger.error(errorMsg);
    }
}

