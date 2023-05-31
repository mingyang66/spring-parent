package com.emily.infrastructure.autoconfigure.exception.handler;

import com.emily.infrastructure.core.constant.AttributeInfo;
import com.emily.infrastructure.core.constant.HeaderInfo;
import com.emily.infrastructure.core.context.holder.ThreadContextHolder;
import com.emily.infrastructure.core.entity.BaseLoggerBuilder;
import com.emily.infrastructure.core.exception.BasicException;
import com.emily.infrastructure.core.exception.PrintExceptionInfo;
import com.emily.infrastructure.core.helper.RequestHelper;
import com.emily.infrastructure.core.helper.RequestUtils;
import com.emily.infrastructure.core.helper.UUIDUtils;
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

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * @Description :  异常处理基础类
 * @Author : Emily
 * @CreateDate :  Created in 2022/7/8 1:43 下午
 */
public class GlobalExceptionCustomizer {

    private static final Logger logger = LoggerFactory.getLogger(DefaultGlobalExceptionHandler.class);

    /**
     * 获取异常堆栈信息并记录到error文件中
     */
    public static void recordErrorMsg(Throwable ex, HttpServletRequest request) {
        String errorMsg = PrintExceptionInfo.printErrorInfo(ex);
        if (ex instanceof BasicException) {
            BasicException systemException = (BasicException) ex;
            errorMsg = MessageFormat.format("业务异常，异常码是【{0}】，异常消息是【{1}】，异常详情{2}", systemException.getStatus(), systemException.getMessage(), errorMsg);
        }
        logger.error(errorMsg);
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
        if (Objects.isNull(request)) {
            return;
        }
        if (Objects.nonNull(request.getAttribute(AttributeInfo.STAGE))) {
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
        try {
            BaseLoggerBuilder builder = new BaseLoggerBuilder()
                    //系统编号
                    .withSystemNumber(ThreadContextHolder.current().getSystemNumber())
                    //事务唯一编号
                    .withTraceId(request.getHeader(HeaderInfo.TRACE_ID) == null ? UUIDUtils.randomSimpleUUID() : request.getHeader(HeaderInfo.TRACE_ID))
                    //请求URL
                    .withUrl(request.getRequestURI())
                    //客户端IP
                    .withClientIp(RequestUtils.getClientIp())
                    //服务端IP
                    .withServerIp(RequestUtils.getServerIp())
                    //版本类型
                    .withAppType(ThreadContextHolder.current().getAppType())
                    //版本号
                    .withAppVersion(ThreadContextHolder.current().getAppVersion())
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
        } catch (Exception exception) {
            logger.error(MessageFormat.format("记录错误日志异常：{0}", PrintExceptionInfo.printErrorInfo(exception)));
        } finally {
            //由于获取参数中会初始化上下文，清除防止OOM
            ThreadContextHolder.unbind(true);
        }
    }
}
