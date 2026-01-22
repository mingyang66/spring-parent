package com.emily.infrastructure.transfer.feign.interceptor;

import com.emily.infrastructure.aop.constant.AopOrderInfo;
import com.emily.infrastructure.common.PrintExceptionUtils;
import com.emily.infrastructure.common.constant.AttributeInfo;
import com.emily.infrastructure.date.DateComputeUtils;
import com.emily.infrastructure.date.DateConvertUtils;
import com.emily.infrastructure.date.DatePatternInfo;
import com.emily.infrastructure.logback.entity.BaseLogger;
import com.emily.infrastructure.logger.event.LogEventType;
import com.emily.infrastructure.logger.event.LogPrintApplicationEvent;
import com.emily.infrastructure.tracing.holder.LocalContextHolder;
import com.emily.infrastructure.transfer.feign.context.FeignContextHolder;
import com.emily.infrastructure.transfer.feign.helper.MethodHelper;
import jakarta.annotation.Nonnull;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.ApplicationContext;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * 在接口到达具体的目标即控制器方法之前获取方法的调用权限，可以在接口方法之前或者之后做Advice(增强)处理
 *
 * @author Emily
 * @since 1.0
 */
public class DefaultFeignMethodInterceptor implements FeignCustomizer {
    private final ApplicationContext context;

    public DefaultFeignMethodInterceptor(ApplicationContext context) {
        this.context = context;
    }

    /**
     * 拦截接口日志
     *
     * @param invocation 接口方法切面连接点
     * @return 接口返回值
     * @throws Throwable 异常
     */
    @Override
    public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
        // 开始时间
        Instant start = Instant.now();
        // 响应结果
        Object response = null;
        try {
            //调用真实的action方法
            response = invocation.proceed();
            return response;
        } catch (Exception e) {
            response = PrintExceptionUtils.printErrorInfo(e);
            throw e;
        } finally {
            //封装异步日志信息
            BaseLogger baseLogger = FeignContextHolder.current()
                    //事务唯一编号
                    .traceId(LocalContextHolder.current().getTraceId())
                    //系统标识
                    .systemNumber(LocalContextHolder.current().getSystemNumber())
                    //时间
                    .traceTime(DateConvertUtils.format(LocalDateTime.now(), DatePatternInfo.YYYY_MM_DD_HH_MM_SS_SSS))
                    //客户端IP
                    .clientIp(LocalContextHolder.current().getClientIp())
                    //服务端IP
                    .serverIp(LocalContextHolder.current().getServerIp())
                    //版本类型
                    .appType(LocalContextHolder.current().getAppType())
                    //版本号
                    .appVersion(LocalContextHolder.current().getAppVersion())
                    //耗时
                    .spentTime(DateComputeUtils.minusMillis(Instant.now(), start))
                    //请求参数
                    .inParams(AttributeInfo.IN_PARAMS, MethodHelper.getMethodArgs(invocation))
                    //响应结果
                    .outParams(AttributeInfo.OUT_PARAMS,MethodHelper.getResult(response));
            //异步记录接口响应信息
            context.publishEvent(new LogPrintApplicationEvent(context, LogEventType.THIRD_PARTY, baseLogger));
            //删除线程上下文中的数据，防止内存溢出
            FeignContextHolder.unbind();
            //非servlet上下文移除数据
            LocalContextHolder.unbind();
        }
    }

    @Override
    public int getOrder() {
        return AopOrderInfo.FEIGN + 1;
    }
}
