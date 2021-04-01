package com.emily.framework.cloud.feign.http.interceptor;

import com.emily.framework.cloud.feign.http.common.FeignLogUtils;
import com.emily.framework.common.exception.BusinessException;
import com.emily.framework.common.exception.PrintExceptionInfo;
import com.emily.framework.context.apilog.po.AsyncLogAop;
import com.emily.framework.context.apilog.service.AsyncLogAopService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.ThrowsAdvice;

import java.lang.reflect.Method;

/**
 * @Description: 在接口到达具体的目标即控制器方法之前获取方法的调用权限，可以在接口方法之前或者之后做Advice(增强)处理
 * @Version: 1.0
 */
public class HttpLogThrowsAdvice implements ThrowsAdvice {

    private AsyncLogAopService asyncLogAopService;

    public HttpLogThrowsAdvice(AsyncLogAopService asyncLogAopService) {
        this.asyncLogAopService = asyncLogAopService;
    }

    public void afterThrowing(Method method, Object[] args, Object target, Exception e) {
        //封装异步日志信息
        AsyncLogAop asyncLog = FeignLogUtils.getAsyncLogAop();

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
