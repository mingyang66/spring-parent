package com.emily.framework.cloud.feign.loadbalancer;

import com.emily.framework.cloud.feign.common.FeignLogUtils;
import com.emily.framework.context.apilog.po.AsyncLogAop;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;

import java.util.Objects;

/**
 * @program: spring-parent
 * @description: 定义loadbalancer执行前后可以执行的操作，此处用来获取实际的请求地址
 * @create: 2021/04/01
 */
public class FeignLogLoadBalancerLifecycle implements LoadBalancerLifecycle<RequestDataContext, ResponseData, ServiceInstance> {

    @Override
    public boolean supports(Class requestContextClass, Class responseClass, Class serverTypeClass) {
        return ServiceInstance.class.isAssignableFrom(serverTypeClass);
    }

    @Override
    public void onStart(Request<RequestDataContext> request) {
    }

    @Override
    public void onStartRequest(Request<RequestDataContext> request, Response<ServiceInstance> lbResponse) {
    }

    @Override
    public void onComplete(CompletionContext<ResponseData, ServiceInstance, RequestDataContext> completionContext) {
        if (Objects.nonNull(completionContext.getClientResponse())) {
            //封装异步日志信息
            AsyncLogAop asyncLog = FeignLogUtils.getAsyncLogAop();
            //设置请求URL
            asyncLog.setRequestUrl(completionContext.getClientResponse().getRequestData().getUrl().toString());
        }
    }
}
