package com.emily.infrastructure.cloud.httpclient.interceptor;

import com.emily.infrastructure.common.date.DateFormatType;
import com.emily.infrastructure.common.entity.BaseLoggerBuilder;
import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.common.object.JSONUtils;
import com.emily.infrastructure.core.context.holder.ThreadContextHolder;
import com.emily.infrastructure.core.helper.RequestHelper;
import com.emily.infrastructure.core.helper.ThreadPoolHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Emily
 * @program: spring-parent
 * @description: RestTemplate拦截器
 * @create: 2020/08/17
 */
public class HttpClientInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientInterceptor.class);

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
        //开始计时
        long start = System.currentTimeMillis();
        //创建拦截日志信息
        BaseLoggerBuilder builder = new BaseLoggerBuilder()
                //生成事物流水号
                .traceId(ThreadContextHolder.current().getTraceId())
                //请求URL
                .url(request.getURI().toString())
                //请求参数
                .requestParams(RequestHelper.getHttpClientArgs(request.getHeaders(), body));
        try {
            //调用接口
            ClientHttpResponse clientHttpResponse = execution.execute(request, body);
            //响应数据
            builder.body(RequestHelper.getHttpClientResponseBody(StreamUtils.copyToByteArray(clientHttpResponse.getBody())));
            return clientHttpResponse;
        } catch (IOException ex) {
            //响应结果
            builder.body(PrintExceptionInfo.printErrorInfo(ex));
            throw ex;
        } finally {
            //客户端IP
            builder.clientIp(ThreadContextHolder.current().getClientIp())
                    //服务端IP
                    .serverIp(ThreadContextHolder.current().getServerIp())
                    //耗时
                    .spentTime(System.currentTimeMillis() - start)
                    //响应时间
                    .triggerTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormatType.YYYY_MM_DD_HH_MM_SS_SSS.getFormat())));
            //异步线程池记录日志
            ThreadPoolHelper.defaultThreadPoolTaskExecutor().submit(() -> logger.info(JSONUtils.toJSONString(builder.build())));
            //非servlet上下文移除数据
            ThreadContextHolder.unbind();
        }

    }

}
