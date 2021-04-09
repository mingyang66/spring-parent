package com.emily.framework.cloud.feign.interceptor;

import com.emily.framework.common.enums.DateFormatEnum;
import com.emily.framework.common.utils.RequestUtils;
import com.emily.framework.common.utils.json.JSONUtils;
import com.emily.framework.context.apilog.po.AsyncLogAop;
import com.google.common.collect.Maps;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

/**
 * @program: spring-parent
 * @description: feign请求日志拦截
 * @create: 2021/03/31
 */
public class FeignRequestInterceptor implements RequestInterceptor, PriorityOrdered {

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
        asyncLog.setRequestParams(transToMap(template));
        // 将日志信息放入请求对象
        request.setAttribute("feignLog", asyncLog);
    }

    /**
     * 参数转换
     */
    private Map<String, Object> transToMap(RequestTemplate template) {
        try {
            Map<String, Object> paramsMap = Maps.newHashMap();
            paramsMap.put("headers", template.headers());
            if (Objects.nonNull(template.body())) {
                paramsMap.put("params", JSONUtils.toJavaBean(new String(template.body(), StandardCharsets.UTF_8), Map.class));
            }
            return paramsMap;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
