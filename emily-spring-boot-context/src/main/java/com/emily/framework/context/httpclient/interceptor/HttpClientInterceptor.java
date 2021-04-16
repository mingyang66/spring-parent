package com.emily.framework.context.httpclient.interceptor;

import com.emily.framework.common.base.BaseLogger;
import com.emily.framework.common.enums.DateFormatEnum;
import com.emily.framework.common.utils.RequestUtils;
import com.emily.framework.common.utils.calculation.ObjectSizeUtil;
import com.emily.framework.common.utils.constant.CharacterUtils;
import com.emily.framework.context.logger.LoggerService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @program: spring-parent
 * @description: RestTemplate拦截器
 * @create: 2020/08/17
 */
public class HttpClientInterceptor implements ClientHttpRequestInterceptor {

    private LoggerService asyncLogHttpClientService;

    public HttpClientInterceptor(LoggerService asyncLogHttpClientService) {
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
        BaseLogger asyncLogHttpClientRequest = new BaseLogger();
        //生成事物流水号
        asyncLogHttpClientRequest.setTraceId(RequestUtils.getTraceId());
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
        //开始计时
        long start = System.currentTimeMillis();
        try {
            //调用接口
            ClientHttpResponse response = execution.execute(request, body);

            //响应数据
            Object responseBody = RequestUtils.getResponseBody(StreamUtils.copyToByteArray(response.getBody()));

            //耗时
            asyncLogHttpClientRequest.setSpentTime(System.currentTimeMillis() - start);
            //响应时间
            asyncLogHttpClientRequest.setTriggerTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormatEnum.YYYY_MM_DD_HH_MM_SS_SSS.getFormat())));
            //响应结果
            asyncLogHttpClientRequest.setResponseBody(responseBody);
            //
            asyncLogHttpClientRequest.setDataSize(ObjectSizeUtil.getObjectSizeUnit(responseBody));
            //记录响应日志
            asyncLogHttpClientService.traceResponse(asyncLogHttpClientRequest);

            return response;
        } catch (IOException e) {
            //耗时
            asyncLogHttpClientRequest.setSpentTime(System.currentTimeMillis() -start);
            //响应时间
            asyncLogHttpClientRequest.setTriggerTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormatEnum.YYYY_MM_DD_HH_MM_SS_SSS.getFormat())));
            //响应结果
            asyncLogHttpClientRequest.setResponseBody(e.getMessage());
            //记录响应日志
            asyncLogHttpClientService.traceResponse(asyncLogHttpClientRequest);

            throw e;
        }

    }

}
