package com.emily.infrastructure.autoconfigure.request.interceptor;

import com.emily.infrastructure.common.ObjectUtils;
import com.emily.infrastructure.core.constant.AopOrderInfo;
import com.emily.infrastructure.core.constant.CharacterInfo;
import com.emily.infrastructure.core.context.holder.ContextTransmitter;
import com.emily.infrastructure.core.context.holder.LocalContextHolder;
import com.emily.infrastructure.core.context.holder.ServletStage;
import com.emily.infrastructure.core.entity.BaseLogger;
import com.emily.infrastructure.core.entity.BaseResponse;
import com.emily.infrastructure.core.exception.BasicException;
import com.emily.infrastructure.core.exception.HttpStatusType;
import com.emily.infrastructure.core.exception.PrintExceptionInfo;
import com.emily.infrastructure.core.helper.PrintLoggerUtils;
import com.emily.infrastructure.core.helper.RequestUtils;
import com.emily.infrastructure.core.helper.ServletHelper;
import com.emily.infrastructure.date.DateComputeUtils;
import com.emily.infrastructure.date.DateConvertUtils;
import com.emily.infrastructure.date.DatePatternInfo;
import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.sensitive.SensitiveUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
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
        BaseLogger.Builder builder = BaseLogger.newBuilder().withRequestParams(ServletHelper.getApiArgs(invocation));
        try {
            //调用真实的action方法
            Object response = invocation.proceed();
            //返回数据不存在
            if (ObjectUtils.isEmpty(response)) {
                return response;
            }
            if (response instanceof ResponseEntity) {
                response = handleException(response, builder);
                // 获取响应体
                builder.withBody(SensitiveUtils.acquireElseGet(((ResponseEntity) response).getBody()));
            } else {
                // 获取响应体
                builder.withBody(SensitiveUtils.acquireElseGet(response));
            }
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
            builder.withSystemNumber(LocalContextHolder.current().getSystemNumber())
                    //事务唯一编号
                    .withTraceId(LocalContextHolder.current().getTraceId())
                    //时间
                    .withTriggerTime(DateConvertUtils.format(LocalDateTime.now(), DatePatternInfo.YYYY_MM_DD_HH_MM_SS_SSS))
                    //客户端IP
                    .withClientIp(LocalContextHolder.current().getClientIp())
                    //服务端IP
                    .withServerIp(LocalContextHolder.current().getServerIp())
                    //请求URL
                    .withUrl(StringUtils.substringBefore(String.valueOf(RequestUtils.getRequest().getRequestURL()), CharacterInfo.ASK_SIGN_EN))
                    //版本类型
                    .withAppType(LocalContextHolder.current().getAppType())
                    //版本号
                    .withAppVersion(LocalContextHolder.current().getAppVersion())
                    //耗时
                    .withSpentTime(DateComputeUtils.minusMillis(Instant.now(), LocalContextHolder.current().getStartTime()));

            BaseLogger baseLogger = builder.build();
            //API耗时--用于返回值耗时字段设置
            LocalContextHolder.current().setSpentTime(baseLogger.getSpentTime());
            //异步记录接口响应信息
            PrintLoggerUtils.printRequest(baseLogger);

        }

    }

    /**
     * 对返回是ResponseEntity类型异常类型特殊处理，如：404 Not Fund接口处理
     */
    private Object handleException(Object response, BaseLogger.Builder builder) {
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
