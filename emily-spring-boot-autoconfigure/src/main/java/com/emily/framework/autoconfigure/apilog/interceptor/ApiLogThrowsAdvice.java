package com.emily.framework.autoconfigure.apilog.interceptor;

import com.emily.framework.common.enums.DateFormatEnum;
import com.emily.framework.common.exception.BusinessException;
import com.emily.framework.common.exception.PrintExceptionInfo;
import com.emily.framework.common.utils.RequestUtils;
import com.emily.framework.context.apilog.po.AsyncLogAop;
import com.emily.framework.context.apilog.service.AsyncLogAopService;
import com.emily.framework.context.request.RequestService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.ThrowsAdvice;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        asyncLog.settId(RequestUtils.getTraceId());
        //时间
        asyncLog.setTriggerTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormatEnum.YYYY_MM_DD_HH_MM_SS_SSS.getFormat())));
        //控制器Class
        asyncLog.setClazz(target.getClass());
        //控制器方法名
        asyncLog.setMethodName(method.getName());
        //请求url
        asyncLog.setRequestUrl(request.getRequestURL().toString());
        //请求方法
        asyncLog.setMethod(request.getMethod());
        //请求参数
        asyncLog.setRequestParams(RequestService.getParameterMap(request));
        if (e instanceof BusinessException) {
            BusinessException exception = (BusinessException) e;
            asyncLog.setResponseBody(StringUtils.join(e, " 【statusCode】", exception.getStatus(), ", 【errorMessage】", exception.getErrorMessage()));
        } else {
            asyncLog.setResponseBody(PrintExceptionInfo.printErrorInfo(e));
        }
        //记录异常日志
        asyncLogAopService.traceResponse(asyncLog);
    }


}
