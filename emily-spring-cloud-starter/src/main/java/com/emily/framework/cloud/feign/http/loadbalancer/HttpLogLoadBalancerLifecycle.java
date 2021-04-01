package com.emily.framework.cloud.feign.http.loadbalancer;

import com.emily.framework.cloud.feign.http.common.FeignLogUtils;
import com.emily.framework.common.enums.DateFormatEnum;
import com.emily.framework.context.apilog.po.AsyncLogAop;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @program: spring-parent
 * @description: 定义loadbalancer执行前后可以执行的操作，此处用来获取实际的请求地址
 * @create: 2021/04/01
 */
public class HttpLogLoadBalancerLifecycle implements LoadBalancerLifecycle<RequestDataContext, ResponseData, ServiceInstance> {

    @Override
    public boolean supports(Class requestContextClass, Class responseClass, Class serverTypeClass) {
        return ServiceInstance.class.isAssignableFrom(serverTypeClass);
    }

    @Override
    public void onStart(Request<RequestDataContext> request) {
        request.getContext().setRequestStartTime(System.currentTimeMillis());
    }

    @Override
    public void onStartRequest(Request<RequestDataContext> request, Response<ServiceInstance> lbResponse) {
    }

    @Override
    public void onComplete(CompletionContext<ResponseData, ServiceInstance, RequestDataContext> completionContext) {
        //封装异步日志信息
        AsyncLogAop asyncLog = FeignLogUtils.getAsyncLogAop();
        //耗时
        asyncLog.setSpentTime(System.currentTimeMillis() - completionContext.getLoadBalancerRequest().getContext().getRequestStartTime());
        //时间
        asyncLog.setTriggerTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormatEnum.YYYY_MM_DD_HH_MM_SS_SSS.getFormat())));
        if (Objects.nonNull(completionContext.getClientResponse())) {
            asyncLog.setRequestUrl(completionContext.getClientResponse().getRequestData().getUrl().toString());
        }
    }
}
