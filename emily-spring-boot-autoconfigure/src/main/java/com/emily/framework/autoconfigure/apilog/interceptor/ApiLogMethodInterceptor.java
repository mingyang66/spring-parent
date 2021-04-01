package com.emily.framework.autoconfigure.apilog.interceptor;

import com.emily.framework.common.enums.DateFormatEnum;
import com.emily.framework.common.utils.RequestUtils;
import com.emily.framework.context.apilog.po.AsyncLogAop;
import com.emily.framework.context.apilog.service.AsyncLogAopService;
import com.emily.framework.context.request.RequestService;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Description: 在接口到达具体的目标即控制器方法之前获取方法的调用权限，可以在接口方法之前或者之后做Advice(增强)处理
 * @Version: 1.0
 */
public class ApiLogMethodInterceptor implements MethodInterceptor {

    private AsyncLogAopService asyncLogAopService;

    public ApiLogMethodInterceptor(AsyncLogAopService asyncLogAopService) {
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
        //获取HttpServletRequest对象
        HttpServletRequest request = RequestUtils.getRequest();
        //将请求唯一编号设置为属性T_ID的值
        request.setAttribute("T_ID", RequestUtils.getTraceId());
        //封装异步日志信息
        AsyncLogAop asyncLog = new AsyncLogAop();
        //事务唯一编号
        asyncLog.settId(RequestUtils.getTraceId());
        //时间
        asyncLog.setTriggerTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormatEnum.YYYY_MM_DD_HH_MM_SS_SSS.getFormat())));
        //控制器Class
        asyncLog.setClazz(invocation.getThis().getClass());
        //控制器方法名
        asyncLog.setMethodName(invocation.getMethod().getName());
        //请求url
        asyncLog.setRequestUrl(request.getRequestURL().toString());
        //请求方法
        asyncLog.setMethod(request.getMethod());
        //请求参数
        asyncLog.setRequestParams(RequestService.getParameterMap(request));

        //新建计时器并开始计时
        long start = System.currentTimeMillis();
        //调用真实的action方法
        Object result = invocation.proceed();

        //耗时
        asyncLog.setSpentTime(System.currentTimeMillis() - start);
        //响应结果
        asyncLog.setResponseBody(result);
        //时间
        asyncLog.setTriggerTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormatEnum.YYYY_MM_DD_HH_MM_SS_SSS.getFormat())));
        //异步记录接口响应信息
        asyncLogAopService.traceResponse(asyncLog);

        return result;

    }

}
