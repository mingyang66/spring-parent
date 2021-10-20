package com.emily.infrastructure.cloud.feign.loadbalancer;

import com.emily.infrastructure.cloud.feign.context.FeignContextHolder;
import com.emily.infrastructure.core.entity.BaseLogger;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;

import java.util.Objects;

/**
 * @author Emily
 * @program: spring-parent
 * @description: 定义loadbalancer执行前后可以执行的操作，此处用来获取实际的请求地址
 * @create: 2021/04/01
 */
public class FeignLoggerLoadBalancerLifecycle implements LoadBalancerLifecycle<RequestDataContext, ResponseData, ServiceInstance> {

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
        if (Objects.nonNull(FeignContextHolder.get())) {
            //封装异步日志信息
            BaseLogger baseLogger = FeignContextHolder.get();
            //设置请求URL
            baseLogger.setUrl(completionContext.getClientResponse().getRequestData().getUrl().toString());
        }
    }
}
