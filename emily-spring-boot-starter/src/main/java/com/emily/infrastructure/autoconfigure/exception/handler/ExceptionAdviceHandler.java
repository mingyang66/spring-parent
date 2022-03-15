package com.emily.infrastructure.autoconfigure.exception.handler;


import com.emily.infrastructure.common.enums.AppHttpStatus;
import com.emily.infrastructure.common.enums.DateFormat;
import com.emily.infrastructure.common.exception.BasicException;
import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.core.context.holder.ContextHolder;
import com.emily.infrastructure.core.entity.BaseLogger;
import com.emily.infrastructure.core.entity.BaseResponse;
import com.emily.infrastructure.core.helper.RequestHelper;
import com.emily.infrastructure.logger.LoggerFactory;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    public BaseResponse exceptionHandler(Exception e, HttpServletRequest request) {
        recordErrorMsg(e, request);
        return BaseResponse.buildResponse(AppHttpStatus.EXCEPTION, e.getMessage());
    }

    /**
     * 运行时异常
     */
    @ExceptionHandler(value = RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(RuntimeException e, HttpServletRequest request) {
        recordErrorMsg(e, request);
        return BaseResponse.buildResponse(AppHttpStatus.RUNTIME_EXCEPTION.getStatus(), e.getMessage());
    }

    /**
     * 空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    public BaseResponse nullPointerExceptionHandler(NullPointerException e, HttpServletRequest request) {
        recordErrorMsg(e, request);
        return BaseResponse.buildResponse(AppHttpStatus.NULL_POINTER);
    }

    /**
     * 类型转换异常
     */
    @ExceptionHandler(ClassCastException.class)
    public BaseResponse classCastExceptionHandler(ClassCastException e, HttpServletRequest request) {
        recordErrorMsg(e, request);
        return BaseResponse.buildResponse(AppHttpStatus.ILLEGAL_CLASS_CONVERT);
    }

    /**
     * IO异常
     */
    @ExceptionHandler(IOException.class)
    public BaseResponse ioExceptionHandler(IOException e, HttpServletRequest request) {
        recordErrorMsg(e, request);
        return BaseResponse.buildResponse(AppHttpStatus.IO_EXCEPTION);
    }

    /**
     * 数组越界异常
     */
    @ExceptionHandler(IndexOutOfBoundsException.class)
    public BaseResponse indexOutOfBoundsException(IndexOutOfBoundsException e, HttpServletRequest request) {
        recordErrorMsg(e, request);
        return BaseResponse.buildResponse(AppHttpStatus.ILLEGAL_INDEX);
    }

    /**
     * API-参数类型不匹配
     */
    @ExceptionHandler(TypeMismatchException.class)
    public BaseResponse requestTypeMismatch(TypeMismatchException e, HttpServletRequest request) {
        recordErrorMsg(e, request);
        return BaseResponse.buildResponse(AppHttpStatus.MISMATCH_PARAMETER);
    }

    /**
     * API-缺少参数
     */
    @ExceptionHandler(MissingRequestValueException.class)
    public BaseResponse requestMissingServletRequest(MissingRequestValueException e, HttpServletRequest request) {
        recordErrorMsg(e, request);
        return BaseResponse.buildResponse(AppHttpStatus.MISSING_PARAMETER);
    }


    /**
     * API-控制器方法参数Validate异常
     *
     * @throws BindException
     * @throws MethodArgumentNotValidException
     */
    @ExceptionHandler({BindException.class, IllegalArgumentException.class, HttpMessageConversionException.class})
    public BaseResponse validModelBindException(Exception e, HttpServletRequest request) {
        recordErrorMsg(e, request);
        return BaseResponse.buildResponse(AppHttpStatus.ILLEGAL_PARAMETER);
    }

    /**
     * API-请求method不匹配
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public BaseResponse httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        recordErrorMsg(e, request);
        return BaseResponse.buildResponse(AppHttpStatus.ILLEGAL_METHOD);
    }

    /**
     * 数字格式异常
     */
    @ExceptionHandler(NumberFormatException.class)
    public BaseResponse numberFormatException(NumberFormatException e, HttpServletRequest request) {
        recordErrorMsg(e, request);
        return BaseResponse.buildResponse(AppHttpStatus.ILLEGAL_NUMBER_FORMAT);
    }

    /**
     * 业务异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BasicException.class)
    public BaseResponse basicException(BasicException e, HttpServletRequest request) {
        recordErrorMsg(e, request);
        return BaseResponse.buildResponse(e.getStatus(), e.getMessage());
    }


    /**
     * 获取异常堆栈信息并记录到error文件中
     */
    private static void recordErrorMsg(Throwable ex, HttpServletRequest request) {
        String errorMsg = PrintExceptionInfo.printErrorInfo(ex);
        if (ex instanceof BasicException) {
            BasicException systemException = (BasicException) ex;
            errorMsg = MessageFormat.format("业务异常，异常码是【{0}】，异常消息是【{1}】，异常详情{2}", systemException.getStatus(), systemException.getMessage(), errorMsg);
        }
        logger.error(errorMsg);
        //记录错误日志
        recordErrorLogger(request, errorMsg);
    }

    /**
     * 记录错误日志
     *
     * @param request
     * @param errorMsg
     */
    private static void recordErrorLogger(HttpServletRequest request, String errorMsg) {
        if (!ContextHolder.get().getStage().equals(ContextHolder.Stage.MAPPING)) {
            return;
        }
        try {
            BaseLogger baseLogger = new BaseLogger();
            //系统编号
            baseLogger.setSystemNumber(ContextHolder.get().getSystemNumber());
            //事务唯一编号
            baseLogger.setTraceId(ContextHolder.get().getTraceId());
            //请求URL
            baseLogger.setUrl(request.getRequestURI());
            //客户端IP
            baseLogger.setClientIp(ContextHolder.get().getClientIp());
            //服务端IP
            baseLogger.setServerIp(ContextHolder.get().getServerIp());
            //触发时间
            baseLogger.setTriggerTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormat.YYYY_MM_DD_HH_MM_SS_SSS.getFormat())));
            //请求参数
            baseLogger.setRequestParams(RequestHelper.getApiParamsMap());
            //响应体
            baseLogger.setBody(errorMsg);
            //耗时(未处理任何逻辑)
            baseLogger.setTime(0L);
            //记录日志到文件
            logger.info(JSONUtils.toJSONString(baseLogger));
        } catch (Exception exception) {
            logger.error(MessageFormat.format("记录错误日志异常：{0}", PrintExceptionInfo.printErrorInfo(exception)));
        } finally {
            //移除线程上下文对应的变量
            ContextHolder.remove();
        }
    }
}

