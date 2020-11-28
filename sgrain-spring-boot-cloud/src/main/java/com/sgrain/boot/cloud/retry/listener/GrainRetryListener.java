package com.sgrain.boot.cloud.retry.listener;

import com.sgrain.boot.common.utils.RequestUtils;
import com.sgrain.boot.context.httpclient.po.AsyncLogHttpClientResponse;
import com.sgrain.boot.context.httpclient.service.AsyncLogHttpClientService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryContext;
import org.springframework.http.MediaType;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;

import java.util.Date;

/**
 * @program: spring-parent
 * @description: ribbon重试监听器
 * @create: 2020/11/13
 */
public class GrainRetryListener implements RetryListener {
    private AsyncLogHttpClientService asyncLogHttpClientService;

    public GrainRetryListener(AsyncLogHttpClientService asyncLogHttpClientService) {
        this.asyncLogHttpClientService = asyncLogHttpClientService;
    }

    /**
     * 在第一次重试之前调用，
     *
     * @param context
     * @param callback
     * @param <T>
     * @param <E>
     * @return
     */
    @Override
    public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
        return true;
    }

    /**
     * 在最后一次尝试后调用（成功或失败），在控件返回到重试调用方之前，允许监听器清除它所持有的任何资源
     *
     * @param context   当前的RetryContext
     * @param callback  当前的RetryCallback
     * @param throwable 回调引发的最后一个异常
     * @param <T>       返回值
     * @param <E>       抛出的异常
     */
    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        if (context.getRetryCount() == 0) {
            return;
        }
        if(context instanceof LoadBalancedRetryContext){
            LoadBalancedRetryContext retryContext = (LoadBalancedRetryContext) context;
            //实例ID
            String instanceId = retryContext.getServiceInstance().getInstanceId();
            //服务ID
            String serviceId = retryContext.getServiceInstance().getServiceId();

            AsyncLogHttpClientResponse client = new AsyncLogHttpClientResponse();
            //唯一标识
            client.setTraceId(RequestUtils.getRequest().getAttribute("T_ID") == null ? "" : String.valueOf(RequestUtils.getRequest().getAttribute("T_ID")));
            //类型
            client.setTraceType(retryContext.getRetryCount());
            //响应时间
            client.setResponseTime(new Date());
            //请求URL
            client.setRequestUrl(StringUtils.replaceIgnoreCase(retryContext.getRequest().getURI().toString(), serviceId, instanceId));
            //响应方法
            client.setMethod(retryContext.getRequest().getMethod().name());
            //报文类型
            client.setContentType(retryContext.getRequest().getHeaders().getContentType() != null ? retryContext.getRequest().getHeaders().getContentType().toString() : MediaType.APPLICATION_JSON_VALUE);
            //协议
            client.setProtocol(RequestUtils.getRequest().getProtocol());
            //响应报文
            client.setResponseBody(StringUtils.join("关闭重试操作，共重试", context.getRetryCount(), "次，", throwable == null ? "" : throwable.getMessage()));
            asyncLogHttpClientService.traceResponse(client);

        }
    }

    /**
     * 每次失败后重试
     *
     * @param context   当前的RetryContext
     * @param callback  当前的RetryCallback回调
     * @param throwable 回调引发的最后一个异常
     * @param <T>       返回值
     * @param <E>       抛出的异常
     */
    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        if(context instanceof LoadBalancedRetryContext){
            LoadBalancedRetryContext retryContext = (LoadBalancedRetryContext) context;
            //实例ID
            String instanceId = retryContext.getServiceInstance().getInstanceId();
            //服务ID
            String serviceId = retryContext.getServiceInstance().getServiceId();

            AsyncLogHttpClientResponse client = new AsyncLogHttpClientResponse();
            //唯一标识
            client.setTraceId(RequestUtils.getRequest().getAttribute("T_ID") == null ? "" : String.valueOf(RequestUtils.getRequest().getAttribute("T_ID")));
            //类型
            client.setTraceType(retryContext.getRetryCount());
            //响应时间
            client.setResponseTime(new Date());
            //请求URL
            client.setRequestUrl(StringUtils.replaceIgnoreCase(retryContext.getRequest().getURI().toString(), serviceId, instanceId));
            //请求方法
            client.setMethod(retryContext.getRequest().getMethod().name());
            //响应报文类型
            client.setContentType((retryContext.getRequest().getHeaders().getContentType() != null) ? retryContext.getRequest().getHeaders().getContentType().toString() : MediaType.APPLICATION_JSON_VALUE);
            //协议
            client.setProtocol(RequestUtils.getRequest().getProtocol());
            //响应报文
            client.setResponseBody(StringUtils.join("第" + retryContext.getRetryCount() + "次重试，", throwable == null ? null : throwable.getMessage()));

            asyncLogHttpClientService.traceResponse(client);

        }
    }
}
