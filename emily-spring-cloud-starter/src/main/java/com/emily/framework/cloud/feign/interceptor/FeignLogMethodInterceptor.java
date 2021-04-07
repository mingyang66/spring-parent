package com.emily.framework.cloud.feign.interceptor;

import com.emily.framework.cloud.feign.common.FeignLogUtils;
import com.emily.framework.common.enums.DateFormatEnum;
import com.emily.framework.common.utils.RequestUtils;
import com.emily.framework.context.apilog.po.AsyncLogAop;
import com.emily.framework.context.apilog.service.AsyncLogAopService;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Description: 在接口到达具体的目标即控制器方法之前获取方法的调用权限，可以在接口方法之前或者之后做Advice(增强)处理
 * @Version: 1.0
 */
public class FeignLogMethodInterceptor implements MethodInterceptor {

    private AsyncLogAopService asyncLogAopService;

    public FeignLogMethodInterceptor(AsyncLogAopService asyncLogAopService) {
        this.asyncLogAopService = asyncLogAopService;
    }

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
        //将请求开始时间放入请求上下文
        RequestUtils.getRequest().setAttribute("startTime", start);
        //调用真实的action方法
        Object result = invocation.proceed();
        //封装异步日志信息
        AsyncLogAop asyncLog = FeignLogUtils.getAsyncLogAop();
        //响应结果
        asyncLog.setResponseBody(result);
        //耗时
        asyncLog.setSpentTime(System.currentTimeMillis() - start);
        //触发时间
        asyncLog.setTriggerTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormatEnum.YYYY_MM_DD_HH_MM_SS_SSS.getFormat())));
        //异步记录接口响应信息
        asyncLogAopService.traceResponse(asyncLog);

        return result;

    }

}
