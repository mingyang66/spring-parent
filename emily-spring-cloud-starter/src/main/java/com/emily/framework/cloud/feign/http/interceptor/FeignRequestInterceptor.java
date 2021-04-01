package com.emily.framework.cloud.feign.http.interceptor;

import com.emily.framework.common.enums.DateFormatEnum;
import com.emily.framework.common.utils.RequestUtils;
import com.emily.framework.common.utils.json.JSONUtils;
import com.emily.framework.context.apilog.po.AsyncLogAop;
import com.emily.framework.context.apilog.service.AsyncLogAopService;
import feign.RequestInterceptor;
import feign.RequestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * @program: spring-parent
 * @description:
 * @create: 2021/03/31
 */
public class FeignRequestInterceptor implements RequestInterceptor {
    private AsyncLogAopService asyncLogAopService;

    public FeignRequestInterceptor(AsyncLogAopService asyncLogAopService) {
        this.asyncLogAopService = asyncLogAopService;
    }

    @Override
    public void apply(RequestTemplate template) {
        //获取HttpServletRequest对象
        HttpServletRequest request = RequestUtils.getRequest();
        //封装异步日志信息
        AsyncLogAop asyncLog = new AsyncLogAop();
        //事务唯一编号
        asyncLog.settId(RequestUtils.getTraceId());
        //时间
        asyncLog.setTriggerTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormatEnum.YYYY_MM_DD_HH_MM_SS_SSS.getFormat())));
        //控制器Class
        asyncLog.setClazz(template.feignTarget().type());
        //控制器方法名
        asyncLog.setMethodName(template.path());
        //请求url
        asyncLog.setRequestUrl(String.format("%s%s", template.feignTarget().url(), template.url()));
        //请求方法
        asyncLog.setMethod(template.method());
        //请求参数
        asyncLog.setRequestParams(transToMap(template.body()));
        // 将日志信息放入请求对象
        request.setAttribute("feignLog", asyncLog);
    }

    /**
     * 参数转换
     */
    private Map<String, Object> transToMap(byte[] params) {
        if (params == null) {
            return null;
        }
        try {
            return JSONUtils.toJavaBean(new String(params, StandardCharsets.UTF_8), Map.class);
        } catch (Exception e) {
            return null;
        }
    }
}
