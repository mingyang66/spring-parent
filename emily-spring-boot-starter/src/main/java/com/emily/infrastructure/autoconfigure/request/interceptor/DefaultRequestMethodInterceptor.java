package com.emily.infrastructure.autoconfigure.request.interceptor;

import com.emily.infrastructure.common.ObjectUtils;
import com.emily.infrastructure.core.constant.AopOrderInfo;
import com.emily.infrastructure.core.constant.CharacterInfo;
import com.emily.infrastructure.core.context.holder.ContextTransmitter;
import com.emily.infrastructure.core.context.holder.LocalContextHolder;
import com.emily.infrastructure.core.context.holder.ServletStage;
import com.emily.infrastructure.core.entity.BaseLogger;
import com.emily.infrastructure.core.entity.BaseResponse;
import com.emily.infrastructure.autoconfigure.exception.entity.BasicException;
import com.emily.infrastructure.autoconfigure.exception.type.AppStatusType;
import com.emily.infrastructure.common.PrintExceptionUtils;
import com.emily.infrastructure.core.utils.PrintLoggerUtils;
import com.emily.infrastructure.core.utils.RequestUtils;
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
        //备份、设置当前阶段标识，标记后如果发生异常，全局异常处理控制器不会记录日志
        ContextTransmitter.replay(ServletStage.BEFORE_CONTROLLER);
        //获取请求参数
        Map<String, Object> paramsMap = ServletHelper.getApiArgs(invocation);
        //封装异步日志信息
        BaseLogger.Builder builder = BaseLogger.newBuilder();
        try {
            //调用真实的action方法
            Object response = invocation.proceed();
            // 返回值类型为ResponseEntity时，特殊处理
            return handleResponse(response, builder);
        } catch (Exception ex) {
            //响应码
            builder.withStatus((ex instanceof BasicException) ? ((BasicException) ex).getStatus() : AppStatusType.EXCEPTION.getStatus())
                    //响应描述
                    .withMessage((ex instanceof BasicException) ? ex.getMessage() : AppStatusType.EXCEPTION.getMessage())
                    //异常响应体
                    .withBody(PrintExceptionUtils.printErrorInfo(ex));
            throw ex;
        } finally {
            BaseLogger baseLogger = builder.withSystemNumber(LocalContextHolder.current().getSystemNumber())
                    //事务唯一编号
                    .withTraceId(LocalContextHolder.current().getTraceId())
                    //请求参数
                    .withRequestParams(paramsMap)
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
                    .withSpentTime(DateComputeUtils.minusMillis(Instant.now(), LocalContextHolder.current().getStartTime()))
                    .build();
            //API耗时--用于返回值耗时字段设置
            LocalContextHolder.current().setSpentTime(baseLogger.getSpentTime());
            //异步记录接口响应信息
            PrintLoggerUtils.printRequest(baseLogger);

        }

    }

    /**
     * 对返回是ResponseEntity类型异常类型特殊处理，如：404 Not Fund接口处理
     *
     * @param response 接口返回值
     * @param builder  日志信息封装器
     * @return 接口返回值
     */
    private Object handleResponse(Object response, BaseLogger.Builder builder) {
        if (ObjectUtils.isEmpty(response)) {
            return response;
        }
        if (response instanceof ResponseEntity) {
            ResponseEntity<?> entity = ((ResponseEntity<?>) response);
            if (entity.getStatusCode().is2xxSuccessful()) {
                builder.withBody(SensitiveUtils.acquireElseGet(entity.getBody(), BaseResponse.class));
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
            builder.withBody(baseResponse);
            return new ResponseEntity<>(baseResponse, entity.getHeaders(), entity.getStatusCode());
        }
        // 设置响应体
        builder.withBody(SensitiveUtils.acquireElseGet(response, BaseResponse.class));
        return response;
    }

    @Override
    public int getOrder() {
        return AopOrderInfo.REQUEST_INTERCEPTOR;
    }
}
