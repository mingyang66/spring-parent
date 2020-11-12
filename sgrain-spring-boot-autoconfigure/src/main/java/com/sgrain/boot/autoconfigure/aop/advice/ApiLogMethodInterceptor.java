package com.sgrain.boot.autoconfigure.aop.advice;

import com.sgrain.boot.context.apilog.po.AsyncLogAop;
import com.sgrain.boot.context.apilog.service.AsyncLogAopService;
import com.sgrain.boot.common.utils.RequestUtils;
import com.sgrain.boot.common.utils.UUIDUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.time.StopWatch;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

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
        //获取请求唯一编号
        String tId = UUIDUtils.randomUUID();
        //将请求唯一编号设置为属性T_ID的值
        request.setAttribute("T_ID", tId);
        //封装异步日志信息
        AsyncLogAop asyncLog = new AsyncLogAop();
        //事务唯一编号
        asyncLog.settId(tId);
        //请求时间
        asyncLog.setRequestTime(new Date());
        //控制器Class
        asyncLog.setClazz(invocation.getThis().getClass());
        //控制器方法名
        asyncLog.setMethodName(invocation.getMethod().getName());
        //请求数据类型-ContentType
        asyncLog.setContentType(request.getContentType());
        //请求url
        asyncLog.setRequestUrl(request.getRequestURL().toString());
        //请求方法
        asyncLog.setMethod(request.getMethod());
        //请求协议
        asyncLog.setProtocol(request.getProtocol());
        //请求参数
        asyncLog.setRequestParams(RequestUtils.getParameterMap(request));
        //记录接口请求信息
        asyncLogAopService.traceRequest(asyncLog);

        //新建计时器并开始计时
        StopWatch stopWatch = StopWatch.createStarted();
        //调用真实的action方法
        Object result = invocation.proceed();
        //暂停计时
        stopWatch.stop();

        //耗时
        asyncLog.setSpentTime(stopWatch.getTime());
        //响应结果
        asyncLog.setResponseBody(result);
        //响应时间
        asyncLog.setResponseTime(new Date());
        //响应数据类型-ContentType
        asyncLog.setContentType(RequestUtils.getResponse().getContentType());
        //异步记录接口响应信息
        asyncLogAopService.traceResponse(asyncLog);

        return result;

    }

}
