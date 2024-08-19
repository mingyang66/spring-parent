package com.emily.infrastructure.web.exception.handler;

import com.emily.infrastructure.common.PrintExceptionUtils;
import com.emily.infrastructure.common.constant.AttributeInfo;
import com.emily.infrastructure.date.DateComputeUtils;
import com.emily.infrastructure.date.DateConvertUtils;
import com.emily.infrastructure.date.DatePatternInfo;
import com.emily.infrastructure.logback.entity.BaseLogger;
import com.emily.infrastructure.logger.utils.PrintLoggerUtils;
import com.emily.infrastructure.sensitive.SensitiveUtils;
import com.emily.infrastructure.tracing.holder.LocalContextHolder;
import com.emily.infrastructure.tracing.holder.ServletStage;
import com.emily.infrastructure.web.exception.type.AppStatusType;
import com.emily.infrastructure.web.filter.helper.ServletHelper;
import com.emily.infrastructure.web.response.annotation.ApiResponsePackIgnore;
import com.emily.infrastructure.web.response.entity.BaseResponse;
import com.emily.infrastructure.web.servlet.interceptor.ParameterInterceptor;
import com.google.common.collect.Maps;
import com.otter.infrastructure.servlet.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * 异常处理基础类
 *
 * @author Emily
 * @since Created in 2022/7/8 1:43 下午
 */
public class GlobalExceptionCustomizer {


    /**
     * 对API请求异常处理，
     * 1.如果标记了ApiResponseWrapperIgnore注解，则统一去除包装
     * 2.否则添加外层包装
     *
     * @param handlerMethod  控制器方法处理对象
     * @param httpStatusType 异常状态枚举
     * @return 包装或为包装的结果
     */
    public static Object getApiResponseWrapper(HandlerMethod handlerMethod, AppStatusType httpStatusType) {
        return getApiResponseWrapper(handlerMethod, httpStatusType.getStatus(), httpStatusType.getMessage());
    }

    /**
     * 对API请求异常处理，
     * 1.如果标记了ApiResponseWrapperIgnore注解，则统一去除包装
     * 2.否则添加外层包装
     *
     * @param handlerMethod 控制器方法处理对象
     * @param status        状态码
     * @param message       异常提示消息
     * @return 包装或为包装的结果
     */
    public static Object getApiResponseWrapper(HandlerMethod handlerMethod, int status, String message) {
        if (Objects.nonNull(handlerMethod)) {
            // 获取控制器方法
            Method method = handlerMethod.getMethod();
            if (method.isAnnotationPresent(ApiResponsePackIgnore.class)) {
                return message;
            }
        }
        return BaseResponse.newBuilder().withStatus(status).withMessage(message).build();
    }

    /**
     * 记录错误日志
     * ----------------------------------------------------------------------
     * 打印错误日志的场景：
     * 1.请求阶段标识为ServletStage.BEFORE_PARAMETER，即：部分参数校验异常；
     * 2.抛出的错误异常为HttpRequestMethodNotSupportedException，此异常不会进入{@link ParameterInterceptor}，即：405 Method Not Allowed
     * ----------------------------------------------------------------------
     *
     * @param ex      异常对象
     * @param request 请求对象
     */
    public static void recordErrorMsg(Throwable ex, HttpServletRequest request) {
        //----------------------前置条件判断------------------------
        if (ServletStage.BEFORE_PARAMETER != LocalContextHolder.current().getServletStage()
                || !(ex instanceof HttpRequestMethodNotSupportedException)) {
            return;
        }
        BaseLogger baseLogger = BaseLogger.newBuilder()
                //系统编号
                .withSystemNumber(LocalContextHolder.current().getSystemNumber())
                //事务唯一编号
                .withTraceId(LocalContextHolder.current().getTraceId())
                //请求URL
                .withUrl(request.getRequestURI())
                //客户端IP
                .withClientIp(RequestUtils.getClientIp())
                //服务端IP
                .withServerIp(RequestUtils.getServerIp())
                //版本类型
                .withAppType(LocalContextHolder.current().getAppType())
                //版本号
                .withAppVersion(LocalContextHolder.current().getAppVersion())
                //触发时间
                .withTriggerTime(DateConvertUtils.format(LocalDateTime.now(), DatePatternInfo.YYYY_MM_DD_HH_MM_SS_SSS))
                //请求参数
                .withRequestParams(getRequestParams(ex, request))
                //响应体
                .withBody(PrintExceptionUtils.printErrorInfo(ex))
                //耗时(未处理任何逻辑)
                .withSpentTime(DateComputeUtils.minusMillis(Instant.now(), LocalContextHolder.current().getStartTime()))
                .build();
        //API耗时
        LocalContextHolder.current().setSpentTime(baseLogger.getSpentTime());
        //记录日志到文件
        PrintLoggerUtils.printRequest(baseLogger);
        //--------------------------后通知特殊条件判断-------------------------
        if (ex instanceof HttpRequestMethodNotSupportedException) {
            LocalContextHolder.unbind(true);
        }
    }

    /**
     * 获取请求参数
     *
     * @param ex      异常对象
     * @param request servlet对象
     * @return 请求参数
     */
    private static Map<String, Object> getRequestParams(Throwable ex, HttpServletRequest request) {
        Map<String, Object> paramsMap = Collections.emptyMap();
        //请求参数
        if (ex instanceof BindException) {
            BindingResult bindingResult = ((BindException) ex).getBindingResult();
            if (Objects.nonNull(bindingResult.getTarget())) {
                paramsMap = Maps.newLinkedHashMap();
                paramsMap.put(AttributeInfo.HEADERS, RequestUtils.getHeaders(request));
                paramsMap.put(AttributeInfo.PARAMS, SensitiveUtils.acquireElseGet(bindingResult.getTarget()));
            }
        }
        if (CollectionUtils.isEmpty(paramsMap)) {
            paramsMap = ServletHelper.getApiArgs(request);
        }
        return paramsMap;
    }
}
