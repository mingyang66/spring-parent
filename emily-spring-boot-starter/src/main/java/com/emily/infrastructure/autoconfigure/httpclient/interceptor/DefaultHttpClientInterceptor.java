package com.emily.infrastructure.autoconfigure.httpclient.interceptor;

import com.emily.infrastructure.common.constant.AopOrderInfo;
import com.emily.infrastructure.common.constant.HeaderInfo;
import com.emily.infrastructure.common.enums.DateFormat;
import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.core.context.holder.ContextHolder;
import com.emily.infrastructure.core.entity.BaseLogger;
import com.emily.infrastructure.core.helper.RequestHelper;
import com.emily.infrastructure.core.helper.ThreadPoolHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
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
public class DefaultHttpClientInterceptor implements HttpClientCustomizer {

    private static final Logger logger = LoggerFactory.getLogger(DefaultHttpClientInterceptor.class);

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
        //设置事务标识
        request.getHeaders().set(HeaderInfo.TRACE_ID, ContextHolder.peek().getTraceId());
        //创建拦截日志信息
        BaseLogger baseLogger = new BaseLogger();
        //生成事物流水号
        baseLogger.setTraceId(ContextHolder.peek().getTraceId());
        //请求URL
        baseLogger.setUrl(request.getURI().toString());
        //请求参数
        baseLogger.setRequestParams(RequestHelper.getHttpClientArgs(body));
        //开始计时
        long start = System.currentTimeMillis();
        try {
            //调用接口
            ClientHttpResponse response = execution.execute(request, body);
            //响应数据
            Object responseBody = RequestHelper.getHttpClientResponseBody(StreamUtils.copyToByteArray(response.getBody()));
            //响应结果
            baseLogger.setBody(responseBody);

            return response;
        } catch (IOException ex) {
            //响应结果
            baseLogger.setBody(PrintExceptionInfo.printErrorInfo(ex));
            throw ex;
        } finally {
            //客户端IP
            baseLogger.setClientIp(ContextHolder.peek().getClientIp());
            //服务端IP
            baseLogger.setServerIp(ContextHolder.peek().getServerIp());
            //耗时
            baseLogger.setTime(System.currentTimeMillis() - start);
            //响应时间
            baseLogger.setTriggerTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormat.YYYY_MM_DD_HH_MM_SS_SSS.getFormat())));
            //异步线程池记录日志
            ThreadPoolHelper.threadPoolTaskExecutor().submit(() -> logger.info(JSONUtils.toJSONString(baseLogger)));
            //非servlet上下文移除数据
            ContextHolder.unbind();
        }

    }

    @Override
    public int getOrder() {
        return AopOrderInfo.HTTP_CLIENT_INTERCEPTOR;
    }
}
