package com.emily.infrastructure.web.exception.handler;

import com.emily.infrastructure.common.PrintExceptionUtils;
import com.emily.infrastructure.common.constant.AttributeInfo;
import com.emily.infrastructure.date.DateComputeUtils;
import com.emily.infrastructure.date.DateConvertUtils;
import com.emily.infrastructure.date.DatePatternInfo;
import com.emily.infrastructure.logback.entity.BaseLogger;
import com.emily.infrastructure.logger.event.LogEventType;
import com.emily.infrastructure.logger.event.LogPrintApplicationEvent;
import com.emily.infrastructure.tracing.holder.LocalContextHolder;
import com.emily.infrastructure.tracing.holder.TracingPhase;
import com.emily.infrastructure.web.filter.helper.MethodHelper;
import com.emily.infrastructure.web.response.annotation.RawResponse;
import com.emily.infrastructure.web.response.entity.BaseResponse;
import com.emily.infrastructure.web.response.enums.ApplicationStatus;
import com.otter.infrastructure.servlet.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.catalina.util.FilterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.BindException;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
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
    private final ApplicationContext context;

    public GlobalExceptionCustomizer(ApplicationContext context) {
        this.context = context;
    }

    /**
     * 对API请求异常处理，
     * 1.如果标记了ApiResponseWrapperIgnore注解，则统一去除包装
     * 2.否则添加外层包装
     *
     * @param handlerMethod  控制器方法处理对象
     * @param httpStatusType 异常状态枚举
     * @return 包装或为包装的结果
     */
    public Object getApiResponseWrapper(HandlerMethod handlerMethod, ApplicationStatus httpStatusType) {
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
    public Object getApiResponseWrapper(HandlerMethod handlerMethod, int status, String message) {
        if (Objects.nonNull(handlerMethod)) {
            // 获取控制器方法
            Method method = handlerMethod.getMethod();
            if (method.isAnnotationPresent(RawResponse.class)) {
                return message;
            }
        }
        return new BaseResponse<>().status(status).message(message);
    }

    /**
     * 记录错误日志
     * ----------------------------------------------------------------------
     * 打印错误日志的场景：
     * 1.请求阶段标识为ServletStage.PARAMETER，即：参数校验异常；
     * ----------------------------------------------------------------------
     *
     * @param ex      异常对象
     * @param request 请求对象
     */
    public void recordErrorMsg(Throwable ex, HttpServletRequest request) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("全局异常拦截器：START============>>{}", FilterUtil.getRequestPath(request));
        }
        //----------------------前置条件判断------------------------
        boolean isReturn = TracingPhase.PARAMETER != LocalContextHolder.current().getTracingPhase();
        if (isReturn) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("全局异常拦截器-不记录日志：END<<============{}", FilterUtil.getRequestPath(request));
            }
            return;
        }
        BaseLogger baseLogger = new BaseLogger()
                //系统编号
                .systemNumber(LocalContextHolder.current().getSystemNumber())
                //事务唯一编号
                .traceId(LocalContextHolder.current().getTraceId())
                //请求URL
                .url(FilterUtil.getRequestPath(request))
                //客户端IP
                .clientIp(RequestUtils.getClientIp())
                //服务端IP
                .serverIp(RequestUtils.getServerIp())
                //版本类型
                .appType(LocalContextHolder.current().getAppType())
                //版本号
                .appVersion(LocalContextHolder.current().getAppVersion())
                //触发时间
                .traceTime(DateConvertUtils.format(LocalDateTime.now(), DatePatternInfo.YYYY_MM_DD_HH_MM_SS_SSS))
                //请求参数
                .params(getRequestParams(ex, request))
                //响应体
                .body(PrintExceptionUtils.printErrorInfo(ex))
                //耗时(未处理任何逻辑)
                .spentTime(DateComputeUtils.minusMillis(Instant.now(), LocalContextHolder.current().getStartTime()));
        //API耗时
        LocalContextHolder.current().setSpentTime(baseLogger.getSpentTime());
        //记录日志到文件
        context.publishEvent(new LogPrintApplicationEvent(context, LogEventType.REQEUST, baseLogger));
        //--------------------------后通知特殊条件判断-------------------------
        LocalContextHolder.unbind(true);
        if (LOG.isDebugEnabled()) {
            LOG.debug("全局异常拦截器-记录日志：END<<============{}", FilterUtil.getRequestPath(request));
        }
    }

    /**
     * 获取请求参数
     * 1. 参数校验异常，抛出BindException异常对参数处理-可以获取参数；
     * 2. Get请求方式，传递Body类型参数，参数校验不通过时会触发MethodArgumentNotValidException（BindException的子类）异常-可以获取参数
     * 3. HttpRequestMethodNotSupportedException Method Not Allowed-无法获取参数
     * 4. NoResourceFoundException 接口404 Not Found-无法获取参数
     *
     * @param ex      异常对象
     * @param request servlet对象
     * @return 请求参数
     */
    private Map<String, Object> getRequestParams(Throwable ex, HttpServletRequest request) {
        //1.参数校验异常，抛出BindException异常对参数处理
        if (ex instanceof BindException bindException) {
            if (Objects.nonNull(bindException.getTarget())) {
                return new LinkedHashMap<>(Map.ofEntries(
                        //获取请求头
                        Map.entry(AttributeInfo.HEADERS, RequestUtils.getHeaders(request)),
                        //获取Body请求参数
                        Map.entry(AttributeInfo.PARAMS_BODY, MethodHelper.getResult(bindException.getTarget())),
                        //获取Get、POST等URL后缀请求参数
                        Map.entry(AttributeInfo.PARAMS_URL, RequestUtils.getParameters(request))
                ));
            }
        }
        //2. HttpRequestMethodNotSupportedException Method Not Allowed，3. NoResourceFoundException 接口404 Not Found
        return MethodHelper.getApiArgs(request);
    }
}
