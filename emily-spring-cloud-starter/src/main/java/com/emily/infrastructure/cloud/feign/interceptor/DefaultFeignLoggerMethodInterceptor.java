package com.emily.infrastructure.cloud.feign.interceptor;

import com.emily.infrastructure.cloud.feign.context.FeignContextHolder;
import com.emily.infrastructure.common.constant.AopOrderInfo;
import com.emily.infrastructure.common.constant.AttributeInfo;
import com.emily.infrastructure.date.DatePatternType;
import com.emily.infrastructure.core.entity.BaseLoggerBuilder;
import com.emily.infrastructure.core.exception.BasicException;
import com.emily.infrastructure.core.exception.PrintExceptionInfo;
import com.emily.infrastructure.core.context.holder.ThreadContextHolder;
import com.emily.infrastructure.core.helper.RequestHelper;
import com.emily.infrastructure.core.helper.ThreadPoolHelper;
import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.logger.LoggerFactory;
import com.emily.infrastructure.sensitive.SensitiveUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Emily
 * @Description: 在接口到达具体的目标即控制器方法之前获取方法的调用权限，可以在接口方法之前或者之后做Advice(增强)处理
 * @Version: 1.0
 */
public class DefaultFeignLoggerMethodInterceptor implements FeignLoggerCustomizer {

    private static final Logger logger = LoggerFactory.getLogger(DefaultFeignLoggerMethodInterceptor.class);

    /**
     * 拦截接口日志
     *
     * @param invocation 接口方法切面连接点
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // 开始时间
        long start = System.currentTimeMillis();
        // 响应结果
        Object response = null;
        try {
            //调用真实的action方法
            response = invocation.proceed();
            return response;
        } catch (Exception e) {
            if (e instanceof BasicException) {
                BasicException exception = (BasicException) e;
                response = StringUtils.join(e, " 【statusCode】", exception.getStatus(), ", 【errorMessage】", exception.getMessage());
            } else {
                response = PrintExceptionInfo.printErrorInfo(e);
            }
            throw e;
        } finally {
            //封装异步日志信息
            BaseLoggerBuilder builder = FeignContextHolder.current()
                    //客户端IP
                    .clientIp(ThreadContextHolder.current().getClientIp())
                    //服务端IP
                    .serverIp(ThreadContextHolder.current().getServerIp())
                    //版本类型
                    .appType(ThreadContextHolder.current().getAppType())
                    //版本号
                    .appVersion(ThreadContextHolder.current().getAppVersion())
                    //耗时
                    .spentTime(System.currentTimeMillis() - start)
                    //触发时间
                    .triggerTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DatePatternType.YYYY_MM_DD_HH_MM_SS_SSS.getPattern())))
                    //响应结果
                    .body(SensitiveUtils.acquire(response));
            //请求参数
            builder.getRequestParams().put(AttributeInfo.PARAMS, RequestHelper.getMethodArgs(invocation));
            //异步记录接口响应信息
            ThreadPoolHelper.defaultThreadPoolTaskExecutor().submit(() -> logger.info(JsonUtils.toJSONString(builder.build())));
            //删除线程上下文中的数据，防止内存溢出
            FeignContextHolder.unbind();
            //非servlet上下文移除数据
            ThreadContextHolder.unbind();
        }
    }

    @Override
    public int getOrder() {
        return AopOrderInfo.FEIGN_INTERCEPTOR;
    }
}
