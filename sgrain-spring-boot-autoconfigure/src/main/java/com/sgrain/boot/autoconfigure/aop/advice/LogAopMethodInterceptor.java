package com.sgrain.boot.autoconfigure.aop.advice;

import com.sgrain.boot.autoconfigure.aop.log.po.AsyncLogAop;
import com.sgrain.boot.autoconfigure.aop.log.service.AsyncLogAopService;
import com.sgrain.boot.common.exception.BusinessException;
import com.sgrain.boot.common.utils.RequestUtils;
import com.sgrain.boot.common.utils.UUIDUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @Description: 在接口到达具体的目标即控制器方法之前获取方法的调用权限，可以在接口方法之前或者之后做Advice(增强)处理
 * @Version: 1.0
 */
public class LogAopMethodInterceptor implements MethodInterceptor {

    private AsyncLogAopService asyncLogAopService;

    public LogAopMethodInterceptor(AsyncLogAopService asyncLogAopService) {
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
        HttpServletRequest request = RequestUtils.getRequest();
        HttpServletResponse response = RequestUtils.getResponse();
        //封装异步日志信息
        AsyncLogAop asyncLog = new AsyncLogAop();
        //事务唯一编号
        asyncLog.settId(UUIDUtils.randomUUID());
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
        //请求参数
        asyncLog.setRequestParams(RequestUtils.getParameterMap(request));
        //记录接口请求信息
        asyncLogAopService.traceRequest(asyncLog);
        //新建计时器并开始计时
        StopWatch stopWatch = StopWatch.createStarted();
        try {
            //调用真实的action方法
            Object result = invocation.proceed();
            //暂停计时
            stopWatch.stop();
            //响应结果
            asyncLog.setResponseBody(result);
            //耗时
            asyncLog.setSpentTime(stopWatch.getTime());
            //响应时间
            asyncLog.setResponseTime(new Date());
            //响应数据类型-ContentType
            asyncLog.setContentType(response.getContentType());
            //异步记录接口响应信息
            asyncLogAopService.traceResponse(asyncLog);

            return result;
        } catch (Throwable e) {
            //暂停计时
            if (stopWatch.isStarted() || stopWatch.isSuspended()) {
                stopWatch.stop();
            }
            if (e instanceof BusinessException) {
                BusinessException exception = (BusinessException) e;
                asyncLog.setException(StringUtils.join(e, " 【statusCode】", exception.getStatus(), ", 【errorMessage】", exception.getErrorMessage()));
            } else {
                asyncLog.setException(StringUtils.join(e.getStackTrace()[0], " ", e));
            }
            //异步记录错误信息
            asyncLogAopService.traceError(asyncLog);
            throw e;
        }
    }

}
