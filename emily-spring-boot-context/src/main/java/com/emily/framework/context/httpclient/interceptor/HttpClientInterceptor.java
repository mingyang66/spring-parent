package com.emily.framework.context.httpclient.interceptor;

import com.emily.framework.common.utils.RequestUtils;
import com.emily.framework.common.utils.calculation.ObjectSizeUtil;
import com.emily.framework.common.utils.constant.CharacterUtils;
import com.emily.framework.context.httpclient.po.AsyncLogHttpClientRequest;
import com.emily.framework.context.httpclient.po.AsyncLogHttpClientResponse;
import com.emily.framework.context.httpclient.service.AsyncLogHttpClientService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

/**
 * @program: spring-parent
 * @description: RestTemplate拦截器
 * @create: 2020/08/17
 */
public class HttpClientInterceptor implements ClientHttpRequestInterceptor {

    private AsyncLogHttpClientService asyncLogHttpClientService;

    public HttpClientInterceptor(AsyncLogHttpClientService asyncLogHttpClientService) {
        this.asyncLogHttpClientService = asyncLogHttpClientService;
    }

    /**
     * RestTemplate拦截方法
     *
     * @param request
     * @param body
     * @param execution
     * @return
     * @throws IOException
     */
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        //创建拦截日志信息
        AsyncLogHttpClientRequest asyncLogHttpClientRequest = new AsyncLogHttpClientRequest();
        //生成事物流水号
        asyncLogHttpClientRequest.setTraceId(RequestUtils.getTraceId());
        //请求时间
        asyncLogHttpClientRequest.setRequestTime(new Date());
        //请求URL
        asyncLogHttpClientRequest.setRequestUrl(StringUtils.substringBefore(request.getURI().toString(), CharacterUtils.ASK_SIGN_EN));
        //请求方法
        asyncLogHttpClientRequest.setMethod(request.getMethodValue());
        //请求参数
        asyncLogHttpClientRequest.setRequestParams(ArrayUtils.isNotEmpty(body) ? RequestUtils.getParameterMap(body) : RequestUtils.convertParameterToMap(StringUtils.substringAfter(request.getURI().toString(), CharacterUtils.ASK_SIGN_EN)));
        //请求类型 ContentType
        asyncLogHttpClientRequest.setContentType(Objects.nonNull(request.getHeaders().getContentType()) ? request.getHeaders().getContentType().toString() : MediaType.APPLICATION_JSON_VALUE);
        //请求协议
        asyncLogHttpClientRequest.setProtocol(RequestUtils.getRequest().getProtocol());
        //记录请求日志
        asyncLogHttpClientService.traceRequest(asyncLogHttpClientRequest);
        try {
            //新建计时器并开始计时
            StopWatch stopWatch = StopWatch.createStarted();
            //调用接口
            ClientHttpResponse response = execution.execute(request, body);
            //暂停计时
            stopWatch.stop();

            //响应数据
            Object responseBody = RequestUtils.getResponseBody(StreamUtils.copyToByteArray(response.getBody()));

            AsyncLogHttpClientResponse asyncLogHttpClientResponse = new AsyncLogHttpClientResponse();
            //设置基础数据
            asyncLogHttpClientResponse.setBaseLog(asyncLogHttpClientRequest);
            //耗时
            asyncLogHttpClientResponse.setSpentTime(stopWatch.getTime());
            //响应时间
            asyncLogHttpClientResponse.setResponseTime(new Date());
            //响应结果
            asyncLogHttpClientResponse.setResponseBody(responseBody);
            //
            asyncLogHttpClientResponse.setDataSize(ObjectSizeUtil.getObjectSizeUnit(responseBody));
            //记录响应日志
            asyncLogHttpClientService.traceResponse(asyncLogHttpClientResponse);

            return response;
        } catch (IOException e) {
            AsyncLogHttpClientResponse asyncLogHttpClientResponse = new AsyncLogHttpClientResponse();
            //设置基础数据
            asyncLogHttpClientResponse.setBaseLog(asyncLogHttpClientRequest);
            //耗时
            asyncLogHttpClientResponse.setSpentTime(0);
            //响应时间
            asyncLogHttpClientResponse.setResponseTime(new Date());
            //响应结果
            asyncLogHttpClientResponse.setResponseBody(e.getMessage());
            //记录响应日志
            asyncLogHttpClientService.traceResponse(asyncLogHttpClientResponse);

            throw e;
        }

    }

}
