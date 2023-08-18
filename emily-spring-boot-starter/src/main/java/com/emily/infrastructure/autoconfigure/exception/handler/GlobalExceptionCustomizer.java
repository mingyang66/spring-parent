package com.emily.infrastructure.autoconfigure.exception.handler;

import com.emily.infrastructure.autoconfigure.response.annotation.ApiResponseWrapperIgnore;
import com.emily.infrastructure.core.constant.AttributeInfo;
import com.emily.infrastructure.core.context.holder.LocalContextHolder;
import com.emily.infrastructure.core.context.holder.ServletStage;
import com.emily.infrastructure.core.entity.BaseLoggerBuilder;
import com.emily.infrastructure.core.entity.BaseResponseBuilder;
import com.emily.infrastructure.core.exception.BasicException;
import com.emily.infrastructure.core.exception.HttpStatusType;
import com.emily.infrastructure.core.exception.PrintExceptionInfo;
import com.emily.infrastructure.core.helper.RequestHelper;
import com.emily.infrastructure.core.helper.RequestUtils;
import com.emily.infrastructure.date.DateConvertUtils;
import com.emily.infrastructure.date.DatePatternInfo;
import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.logger.LoggerFactory;
import com.emily.infrastructure.sensitive.SensitiveUtils;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * 异常处理基础类
 *
 * @author Emily
 * @since Created in 2022/7/8 1:43 下午
 */
public class GlobalExceptionCustomizer {

    private static final Logger logger = LoggerFactory.getModuleLogger(GlobalExceptionCustomizer.class, "api", "request");

    /**
     * 对API请求异常处理，
     * 1.如果标记了ApiResponseWrapperIgnore注解，则统一去除包装
     * 2.否则添加外层包装
     *
     * @param handlerMethod  控制器方法处理对象
     * @param httpStatusType 异常状态枚举
     * @return 包装或为包装的结果
     */
    public static Object getApiResponseWrapper(HandlerMethod handlerMethod, HttpStatusType httpStatusType) {
        if (Objects.nonNull(handlerMethod)) {
            // 获取控制器方法
            Method method = handlerMethod.getMethod();
            if (method.isAnnotationPresent(ApiResponseWrapperIgnore.class)) {
                return httpStatusType.getMessage();
            }
        }
        return new BaseResponseBuilder<>().withStatus(httpStatusType.getStatus()).withMessage(httpStatusType.getMessage()).build();
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
            if (method.isAnnotationPresent(ApiResponseWrapperIgnore.class)) {
                return message;
            }
        }
        return new BaseResponseBuilder<>().withStatus(status).withMessage(message).build();
    }

    /**
     * 获取异常堆栈信息并记录到error文件中
     *
     * @param ex      异常对象
     * @param request 请求对象
     */
    public static void recordErrorMsg(Throwable ex, HttpServletRequest request) {
        String errorMsg = PrintExceptionInfo.printErrorInfo(ex);
        if (ex instanceof BasicException) {
            BasicException systemException = (BasicException) ex;
            errorMsg = MessageFormat.format("业务异常，异常码是【{0}】，异常消息是【{1}】，异常详情{2}", systemException.getStatus(), systemException.getMessage(), errorMsg);
        }
        //记录错误日志
        recordErrorLogger(request, ex, errorMsg);
    }

    /**
     * 记录错误日志
     *
     * @param request
     * @param errorMsg
     */
    private static void recordErrorLogger(HttpServletRequest request, Throwable ex, String errorMsg) {
        if (!ServletStage.BEFORE_PARAMETER.equals(LocalContextHolder.current().getServletStage())) {
            return;
        }
        Map<String, Object> paramsMap = null;
        //请求参数
        if (ex instanceof BindException) {
            BindingResult bindingResult = ((BindException) ex).getBindingResult();
            if (Objects.nonNull(bindingResult) && Objects.nonNull(bindingResult.getTarget())) {
                paramsMap = Maps.newLinkedHashMap();
                paramsMap.put(AttributeInfo.HEADERS, RequestHelper.getHeaders(request));
                paramsMap.put(AttributeInfo.PARAMS, SensitiveUtils.acquireElseGet(bindingResult.getTarget()));
            }
        }
        if (CollectionUtils.isEmpty(paramsMap)) {
            paramsMap = RequestHelper.getApiArgs(request);
        }
        BaseLoggerBuilder builder = BaseLoggerBuilder.create()
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
                .withRequestParams(paramsMap)
                //响应体
                .withBody(errorMsg)
                //耗时(未处理任何逻辑)
                .withSpentTime(0L);
        //记录日志到文件
        logger.info(JsonUtils.toJSONString(builder.build()));

    }
}
