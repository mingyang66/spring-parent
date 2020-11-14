package com.sgrain.boot.cloud.retry.listener;

import com.sgrain.boot.common.utils.RequestUtils;
import com.sgrain.boot.common.utils.UUIDUtils;
import com.sgrain.boot.common.utils.constant.CharacterUtils;
import com.sgrain.boot.context.httpclient.po.AsyncLogHttpClient;
import com.sgrain.boot.context.httpclient.service.AsyncLogHttpClientService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryContext;
import org.springframework.http.HttpRequest;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @program: spring-parent
 * @description:
 * @author: 姚明洋
 * @create: 2020/11/13
 */
public class SmallGrainRetryListener implements RetryListener {
    private AsyncLogHttpClientService asyncLogHttpClientService;

    public SmallGrainRetryListener(AsyncLogHttpClientService asyncLogHttpClientService) {
        this.asyncLogHttpClientService = asyncLogHttpClientService;
    }

    @Override
    public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
        LoadBalancedRetryContext loadBalancedRetryContext = (LoadBalancedRetryContext) context;
        HttpRequest request = loadBalancedRetryContext.getRequest();
        HttpServletRequest httpServletRequest = RequestUtils.getRequest();
        AsyncLogHttpClient client = new AsyncLogHttpClient();
        client.settId(httpServletRequest.getAttribute("T_ID") == null ? "" : String.valueOf(httpServletRequest.getAttribute("T_ID")));
        client.setRequestTime(new Date());
        client.setRequestUrl(request.getURI().toString());
        client.setMethod(request.getMethod().name());
        client.setProtocol(httpServletRequest.getProtocol());
        client.setContentType(httpServletRequest.getContentType());
        client.setRequestParams(RequestUtils.convertParameterToMap(StringUtils.substringAfter(request.getURI().toString(), CharacterUtils.ASK_SIGN_EN)));
        client.setTraceType(1);
        asyncLogHttpClientService.traceRequest(client);
        return true;
    }

    /**
     * 在最后一次尝试后调用（成功或失败），在控件返回到重试调用方之前，允许监听器清除它所持有的任何资源
     * @param context 当前的RetryContext
     * @param callback 当前的RetryCallback
     * @param throwable 回调引发的最后一个异常
     * @param <T> 返回值
     * @param <E> 抛出的异常
     */
    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        LoadBalancedRetryContext retryContext = (LoadBalancedRetryContext) context;
        HttpRequest request = retryContext.getRequest();
        HttpServletRequest httpServletRequest = RequestUtils.getRequest();
        AsyncLogHttpClient client = new AsyncLogHttpClient();
        client.settId(httpServletRequest.getAttribute("T_ID") == null ? "" : String.valueOf(httpServletRequest.getAttribute("T_ID")));
        client.setResponseTime(new Date());
        client.setRequestUrl(request.getURI().toString());
        client.setMethod(request.getMethod().name());
        client.setProtocol(httpServletRequest.getProtocol());
        client.setContentType(httpServletRequest.getContentType());
        client.setRequestParams(RequestUtils.convertParameterToMap(StringUtils.substringAfter(request.getURI().toString(), CharacterUtils.ASK_SIGN_EN)));
        client.setTraceType(1);
        client.setResponseBody(throwable == null ? null : throwable.getMessage());
        asyncLogHttpClientService.traceResponse(client);
    }

    /**
     * 每次失败后重试
     * @param context 当前的RetryContext
     * @param callback 当前的RetryCallback回调
     * @param throwable 回调引发的最后一个异常
     * @param <T> 返回值
     * @param <E> 抛出的异常
     */
    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        LoadBalancedRetryContext retryContext = (LoadBalancedRetryContext) context;
        HttpRequest request = retryContext.getRequest();
        HttpServletRequest httpServletRequest = RequestUtils.getRequest();
        AsyncLogHttpClient client = new AsyncLogHttpClient();
        client.settId(httpServletRequest.getAttribute("T_ID") == null ? "" : String.valueOf(httpServletRequest.getAttribute("T_ID")));
        client.setResponseTime(new Date());
        client.setRequestUrl(request.getURI().toString());
        client.setMethod(request.getMethod().name());
        client.setProtocol(httpServletRequest.getProtocol());
        client.setContentType(httpServletRequest.getContentType());
        client.setRequestParams(RequestUtils.convertParameterToMap(StringUtils.substringAfter(request.getURI().toString(), CharacterUtils.ASK_SIGN_EN)));
        client.setTraceType(1);
        client.setResponseBody(throwable == null ? null : throwable.getMessage());
        asyncLogHttpClientService.traceResponse(client);
    }
}
