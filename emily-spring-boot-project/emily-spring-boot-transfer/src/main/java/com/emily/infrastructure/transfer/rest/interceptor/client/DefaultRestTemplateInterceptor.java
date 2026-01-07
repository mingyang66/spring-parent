package com.emily.infrastructure.transfer.rest.interceptor.client;

import com.emily.infrastructure.aop.constant.AopOrderInfo;
import com.emily.infrastructure.common.PrintExceptionUtils;
import com.emily.infrastructure.common.constant.HeaderInfo;
import com.emily.infrastructure.date.DateComputeUtils;
import com.emily.infrastructure.date.DateConvertUtils;
import com.emily.infrastructure.date.DatePatternInfo;
import com.emily.infrastructure.logback.entity.BaseLogger;
import com.emily.infrastructure.logger.event.LogEventType;
import com.emily.infrastructure.logger.event.LogPrintApplicationEvent;
import com.emily.infrastructure.tracing.holder.LocalContextHolder;
import com.emily.infrastructure.transfer.rest.factory.HttpRequestFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * RestTemplate拦截器
 *
 * @author Emily
 * @since 2020/08/17
 */
public class DefaultRestTemplateInterceptor implements RestTemplateCustomizer {
    private final ApplicationContext context;

    public DefaultRestTemplateInterceptor(ApplicationContext context) {
        this.context = context;
    }

    /**
     * RestTemplate拦截方法
     *
     * @param request   请求对象
     * @param body      请求体
     * @param execution 请求方法执行对象
     * @return 响应对象
     * @throws IOException 抛出异常
     */
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        //设置事务标识
        request.getHeaders().set(HeaderInfo.TRACE_ID, LocalContextHolder.current().getTraceId());
        //创建拦截日志信息
        BaseLogger baseLogger = new BaseLogger()
                //系统编号
                .systemNumber(LocalContextHolder.current().getSystemNumber())
                //生成事物流水号
                .traceId(LocalContextHolder.current().getTraceId())
                //请求URL
                .url(request.getURI().toString())
                //请求参数
                .requestParams(HttpRequestFactory.getArgs(request.getHeaders(), body));
        //开始计时
        Instant start = Instant.now();
        try {
            //调用接口
            ClientHttpResponse response = execution.execute(request, body);
            //响应结果
            baseLogger.body(HttpRequestFactory.getResponseBody(StreamUtils.copyToByteArray(response.getBody())));

            return response;
        } catch (IOException ex) {
            //响应结果
            baseLogger.body(PrintExceptionUtils.printErrorInfo(ex));
            throw ex;
        } finally {
            //客户端IP
            baseLogger.clientIp(LocalContextHolder.current().getClientIp())
                    //服务端IP
                    .serverIp(LocalContextHolder.current().getServerIp())
                    //版本类型
                    .appType(LocalContextHolder.current().getAppType())
                    //版本号
                    .appVersion(LocalContextHolder.current().getAppVersion())
                    //耗时
                    .spentTime(DateComputeUtils.minusMillis(Instant.now(), start))
                    //响应时间
                    .triggerTime(DateConvertUtils.format(LocalDateTime.now(), DatePatternInfo.YYYY_MM_DD_HH_MM_SS_SSS));
            //异步线程池记录日志
            context.publishEvent(new LogPrintApplicationEvent(LogEventType.THIRD_PARTY, baseLogger));
            //非servlet上下文移除数据
            LocalContextHolder.unbind();
        }

    }

    @Override
    public int getOrder() {
        return AopOrderInfo.REST + 1;
    }
}
