package com.emily.infrastructure.cloud.httpclient.interceptor;

import com.emily.infrastructure.common.enums.DateFormat;
import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.core.entity.BaseLogger;
import com.emily.infrastructure.core.helper.RequestHelper;
import com.emily.infrastructure.core.helper.ThreadPoolHelper;
import com.emily.infrastructure.core.holder.ContextHolder;
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
        BaseLogger baseLogger = new BaseLogger();
        //生成事物流水号
        baseLogger.setTraceId(ContextHolder.get().getTraceId());
        //请求URL
        baseLogger.setUrl(request.getURI().toString());
        //请求方法
        baseLogger.setMethod(request.getMethodValue());
        //请求参数
        baseLogger.setRequestParams(RequestHelper.getParameterMap(body));
        try {
            //调用接口
            ClientHttpResponse clientHttpResponse = execution.execute(request, body);
            //响应数据
            baseLogger.setBody(RequestHelper.getResponseBody(StreamUtils.copyToByteArray(clientHttpResponse.getBody())));
            return clientHttpResponse;
        } catch (IOException ex) {
            //响应结果
            baseLogger.setBody(PrintExceptionInfo.printErrorInfo(ex));
            throw ex;
        } finally {
            //耗时
            baseLogger.setTime(System.currentTimeMillis() - start);
            //响应时间
            baseLogger.setTriggerTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormat.YYYY_MM_DD_HH_MM_SS_SSS.getFormat())));
            //异步线程池记录日志
            ThreadPoolHelper.threadPoolTaskExecutor().submit(() -> logger.info(JSONUtils.toJSONString(baseLogger)));
            //非servlet上下文移除数据
            ContextHolder.removeNoServletContext();
        }

    }

}
