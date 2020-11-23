package com.sgrain.boot.autoconfigure.aop.advice;

import com.sgrain.boot.common.exception.BusinessException;
import com.sgrain.boot.common.utils.RequestUtils;
import com.sgrain.boot.context.apilog.po.AsyncLogAop;
import com.sgrain.boot.context.apilog.service.AsyncLogAopService;
import com.sgrain.boot.context.request.RequestService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.ThrowsAdvice;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * @Description: 在接口到达具体的目标即控制器方法之前获取方法的调用权限，可以在接口方法之前或者之后做Advice(增强)处理
 * @Version: 1.0
 */
public class ApiLogThrowsAdvice implements ThrowsAdvice {

    private AsyncLogAopService asyncLogAopService;

    public ApiLogThrowsAdvice(AsyncLogAopService asyncLogAopService) {
        this.asyncLogAopService = asyncLogAopService;
    }

    public void afterThrowing(Method method, Object[] args, Object target, Exception e) {
        HttpServletRequest request = RequestUtils.getRequest();
        //封装异步日志信息
        AsyncLogAop asyncLog = new AsyncLogAop();
        //事务唯一编号
        asyncLog.settId(String.valueOf(request.getAttribute("T_ID")));
        //请求时间
        asyncLog.setResponseTime(new Date());
        //控制器Class
        asyncLog.setClazz(target.getClass());
        //控制器方法名
        asyncLog.setMethodName(method.getName());
        //请求数据类型-ContentType
        asyncLog.setContentType(request.getContentType());
        //请求url
        asyncLog.setRequestUrl(request.getRequestURL().toString());
        //请求方法
        asyncLog.setMethod(request.getMethod());
        //请求协议
        asyncLog.setProtocol(request.getProtocol());
        //请求参数
        asyncLog.setRequestParams(RequestService.getParameterMap(request));
        if (e instanceof BusinessException) {
            BusinessException exception = (BusinessException) e;
            asyncLog.setException(StringUtils.join(e, " 【statusCode】", exception.getStatus(), ", 【errorMessage】", exception.getErrorMessage()));
        } else {
            asyncLog.setException(StringUtils.join(e.getStackTrace()[0], " ", e));
        }
        //记录异常日志
        asyncLogAopService.traceError(asyncLog);
    }


}
