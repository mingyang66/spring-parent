package com.emily.infrastructure.autoconfigure.request.interceptor;

import com.emily.infrastructure.common.ObjectUtils;
import com.emily.infrastructure.core.constant.AopOrderInfo;
import com.emily.infrastructure.core.constant.CharacterInfo;
import com.emily.infrastructure.core.context.holder.ContextTransmitter;
import com.emily.infrastructure.core.context.holder.LocalContextHolder;
import com.emily.infrastructure.core.context.holder.ServletStage;
import com.emily.infrastructure.core.entity.BaseLogger;
import com.emily.infrastructure.core.entity.BaseLoggerBuilder;
import com.emily.infrastructure.core.entity.BaseResponse;
import com.emily.infrastructure.core.exception.BasicException;
import com.emily.infrastructure.core.exception.HttpStatusType;
import com.emily.infrastructure.core.exception.PrintExceptionInfo;
import com.emily.infrastructure.core.helper.RequestHelper;
import com.emily.infrastructure.core.helper.RequestUtils;
import com.emily.infrastructure.core.helper.ThreadPoolHelper;
import com.emily.infrastructure.date.DateComputeUtils;
import com.emily.infrastructure.date.DateConvertUtils;
import com.emily.infrastructure.date.DatePatternInfo;
import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.logger.LoggerFactory;
import com.emily.infrastructure.sensitive.SensitiveUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 在接口到达具体的目标即控制器方法之前获取方法的调用权限，可以在接口方法之前或者之后做Advice(增强)处理
 *
 * @author Emily
 * @since 1.0
 */
public class DefaultRequestMethodInterceptor implements RequestCustomizer {

    private static final Logger logger = LoggerFactory.getModuleLogger(DefaultRequestMethodInterceptor.class, "api", "request");

    /**
     * 拦截接口日志
     *
     * @param invocation 接口方法切面连接点
     */
    @Override
    public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
        //备份、设置当前阶段标识
        ContextTransmitter.replay(ServletStage.BEFORE_CONTROLLER);
        //封装异步日志信息
        BaseLoggerBuilder builder = BaseLogger.newBuilder();
        try {
            //系统编号
            builder.withSystemNumber(LocalContextHolder.current().getSystemNumber())
                    //事务唯一编号
                    .withTraceId(LocalContextHolder.current().getTraceId())
                    //时间
                    .withTriggerTime(DateConvertUtils.format(LocalDateTime.now(), DatePatternInfo.YYYY_MM_DD_HH_MM_SS_SSS))
                    //请求url
                    .withUrl(StringUtils.substringBefore(String.valueOf(RequestUtils.getRequest().getRequestURL()), CharacterInfo.ASK_SIGN_EN))
                    //请求参数
                    .withRequestParams(RequestHelper.getApiArgs(invocation));
            //调用真实的action方法
            Object response = invocation.proceed();
            //返回数据不存在
            if (ObjectUtils.isEmpty(response)) {
                return response;
            }
            if (response instanceof ResponseEntity) {
                response = handleException(response, builder);
            }
            //设置响应结果
            builder.withBody(SensitiveUtils.acquireElseGet(response));

            return response;
        } catch (Exception ex) {
            if (ex instanceof BasicException) {
                BasicException exception = (BasicException) ex;
                //响应码
                builder.withStatus(exception.getStatus())
                        //响应描述
                        .withMessage(exception.getMessage())
                        //异常响应体
                        .withBody(StringUtils.join("【statusCode】", exception.getStatus(), ", 【errorMessage】", exception.getMessage()));
            } else {
                //响应码
                builder.withStatus(HttpStatusType.EXCEPTION.getStatus())
                        //响应描述
                        .withMessage(HttpStatusType.EXCEPTION.getMessage())
                        //异常响应体
                        .withBody(PrintExceptionInfo.printErrorInfo(ex));
            }
            throw ex;
        } finally {
            //客户端IP
            builder.withClientIp(LocalContextHolder.current().getClientIp())
                    //服务端IP
                    .withServerIp(LocalContextHolder.current().getServerIp())
                    //版本类型
                    .withAppType(LocalContextHolder.current().getAppType())
                    //版本号
                    .withAppVersion(LocalContextHolder.current().getAppVersion())
                    //耗时
                    .withSpentTime(DateComputeUtils.minusMillis(Instant.now(), LocalContextHolder.current().getStartTime()))
                    //时间
                    .withTriggerTime(DateConvertUtils.format(LocalDateTime.now(), DatePatternInfo.YYYY_MM_DD_HH_MM_SS_SSS));

            BaseLogger baseLogger = builder.build();
            //API耗时
            LocalContextHolder.current().setSpentTime(baseLogger.getSpentTime());
            //异步记录接口响应信息
            ThreadPoolHelper.defaultThreadPoolTaskExecutor().submit(() -> logger.info(JsonUtils.toJSONString(baseLogger)));

        }

    }

    /**
     * 对返回是ResponseEntity类型异常类型特殊处理，如：404 Not Fund接口处理
     */
    private Object handleException(Object response, BaseLoggerBuilder builder) {
        ResponseEntity<?> entity = ((ResponseEntity<?>) response);
        if (entity.getStatusCode().is2xxSuccessful()) {
            return entity;
        }
        Map dataMap = JsonUtils.toJavaBean(JsonUtils.toJSONString(entity.getBody()), Map.class);
        builder.withUrl(dataMap.get("path").toString())
                .withStatus(entity.getStatusCode().value())
                .withMessage(dataMap.get("error").toString());
        BaseResponse baseResponse = BaseResponse.newBuilder()
                .withStatus(entity.getStatusCode().value())
                .withMessage(dataMap.get("error").toString())
                .build();
        return new ResponseEntity<>(baseResponse, entity.getHeaders(), entity.getStatusCode());
    }

    @Override
    public int getOrder() {
        return AopOrderInfo.REQUEST_INTERCEPTOR;
    }
}
