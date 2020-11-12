package com.sgrain.boot.context.httpclient.interceptor;

import com.sgrain.boot.common.utils.RequestUtils;
import com.sgrain.boot.common.utils.UUIDUtils;
import com.sgrain.boot.common.utils.constant.CharacterUtils;
import com.sgrain.boot.context.httpclient.po.AsyncLogHttpClient;
import com.sgrain.boot.context.httpclient.service.AsyncLogHttpClientService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.http.HttpRequest;
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
        AsyncLogHttpClient asyncLogHttpClient = new AsyncLogHttpClient();
        //生成事物流水号
        asyncLogHttpClient.settId(UUIDUtils.randomUUID());
        //请求时间
        asyncLogHttpClient.setRequestTime(new Date());
        //请求URL
        asyncLogHttpClient.setRequestUrl(StringUtils.substringBefore(request.getURI().toString(), CharacterUtils.ASK_SIGN_EN));
        //请求方法
        asyncLogHttpClient.setMethod(request.getMethodValue());
        //请求参数
        asyncLogHttpClient.setRequestParams(ArrayUtils.isNotEmpty(body) ? RequestUtils.getParameterMap(body) : RequestUtils.convertParameterToMap(StringUtils.substringAfter(request.getURI().toString(), CharacterUtils.ASK_SIGN_EN)));
        //请求类型 ContentType
        asyncLogHttpClient.setContentType(Objects.nonNull(request.getHeaders().getContentType()) ? request.getHeaders().getContentType().toString() : null);
        //请求协议
        asyncLogHttpClient.setProtocol(RequestUtils.getRequest().getProtocol());
        //记录请求日志
        asyncLogHttpClientService.traceRequest(asyncLogHttpClient);

        //新建计时器并开始计时
        StopWatch stopWatch = StopWatch.createStarted();
        //调用接口
        ClientHttpResponse response = execution.execute(request, body);
        //暂停计时
        stopWatch.stop();

        //耗时
        asyncLogHttpClient.setSpentTime(stopWatch.getTime());
        //响应时间
        asyncLogHttpClient.setResponseTime(new Date());
        //响应结果
        asyncLogHttpClient.setResponseBody(RequestUtils.getResponseBody(StreamUtils.copyToByteArray(response.getBody())));
        //记录响应日志
        asyncLogHttpClientService.traceResponse(asyncLogHttpClient);

        return response;
    }

}
