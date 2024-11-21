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
import com.google.common.collect.Maps;
import com.otter.infrastructure.servlet.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
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
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionCustomizer.class);

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
        return new BaseResponse<>().status(status).message(message);
    }

    /**
     * 记录错误日志
     * ----------------------------------------------------------------------
     * 打印错误日志的场景：
     * 1.请求阶段标识为ServletStage.BEFORE_PARAMETER，即：参数校验异常；
     * ----------------------------------------------------------------------
     *
     * @param ex      异常对象
     * @param request 请求对象
     */
    public static void recordErrorMsg(Throwable ex, HttpServletRequest request) {
        if (LOG.isDebugEnabled()) {
            LOG.warn("全局异常拦截器：START============>>{}", request.getRequestURI());
        }
        //----------------------前置条件判断------------------------
        boolean isReturn = ServletStage.PARAMETER != LocalContextHolder.current().getServletStage();
        if (isReturn) {
            if (LOG.isDebugEnabled()) {
                LOG.warn("全局异常拦截器-不记录日志：END<<============{}", request.getRequestURI());
            }
            return;
        }
        BaseLogger baseLogger = new BaseLogger()
                //系统编号
                .systemNumber(LocalContextHolder.current().getSystemNumber())
                //事务唯一编号
                .traceId(LocalContextHolder.current().getTraceId())
                //请求URL
                .url(request.getRequestURI())
                //客户端IP
                .clientIp(RequestUtils.getClientIp())
                //服务端IP
                .serverIp(RequestUtils.getServerIp())
                //版本类型
                .appType(LocalContextHolder.current().getAppType())
                //版本号
                .appVersion(LocalContextHolder.current().getAppVersion())
                //触发时间
                .triggerTime(DateConvertUtils.format(LocalDateTime.now(), DatePatternInfo.YYYY_MM_DD_HH_MM_SS_SSS))
                //请求参数
                .requestParams(getRequestParams(ex, request))
                //响应体
                .body(PrintExceptionUtils.printErrorInfo(ex))
                //耗时(未处理任何逻辑)
                .spentTime(DateComputeUtils.minusMillis(Instant.now(), LocalContextHolder.current().getStartTime()));
        //API耗时
        LocalContextHolder.current().setSpentTime(baseLogger.getSpentTime());
        //记录日志到文件
        PrintLoggerUtils.printRequest(baseLogger);
        //--------------------------后通知特殊条件判断-------------------------
        LocalContextHolder.unbind(true);
        if (LOG.isDebugEnabled()) {
            LOG.warn("全局异常拦截器-记录日志：END<<============{}", request.getRequestURI());
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
