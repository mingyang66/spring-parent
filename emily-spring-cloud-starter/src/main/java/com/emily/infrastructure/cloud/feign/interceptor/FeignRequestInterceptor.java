package com.emily.infrastructure.cloud.feign.interceptor;

import com.emily.infrastructure.cloud.feign.context.FeignContextHolder;
import com.emily.infrastructure.common.constant.HeaderInfo;
import com.emily.infrastructure.common.enums.DateFormatEnum;
import com.emily.infrastructure.common.utils.constant.CharacterUtils;
import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.core.entity.BaseLogger;
import com.emily.infrastructure.core.holder.ContextHolder;
import com.google.common.collect.Maps;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

/**
 * @author Emily
 * @program: spring-parent
 * @description: feign请求日志拦截
 * @create: 2021/03/31
 */
public class FeignRequestInterceptor implements RequestInterceptor, PriorityOrdered {

    @Override
    public void apply(RequestTemplate template) {
        //请求header设置事务ID
        template.header(HeaderInfo.TRACE_ID, ContextHolder.get().getTraceId());
        //封装异步日志信息
        BaseLogger baseLogger = new BaseLogger();
        //事务唯一编号
        baseLogger.setTraceId(ContextHolder.get().getTraceId());
        //时间
        baseLogger.setTriggerTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormatEnum.YYYY_MM_DD_HH_MM_SS_SSS.getFormat())));
        //请求url
        baseLogger.setUrl(String.format("%s%s", StringUtils.rightPad(template.feignTarget().url(), 1, CharacterUtils.PATH_SEPARATOR), RegExUtils.replaceFirst(template.url(), CharacterUtils.PATH_SEPARATOR, "")));
        //请求方法
        baseLogger.setMethod(template.method());
        //请求参数
        baseLogger.setRequestParams(transToMap(template));
        // 将日志信息放入请求对象
        FeignContextHolder.set(baseLogger);
    }

    /**
     * 参数转换
     */
    private Map<String, Object> transToMap(RequestTemplate template) {
        Map<String, Object> paramsMap = Maps.newHashMap();
        try {
            paramsMap.put("headers", template.headers());
            if (Objects.nonNull(template.body())) {
                paramsMap.put("params", JSONUtils.toJavaBean(new String(template.body(), StandardCharsets.UTF_8), Map.class));
            }
        } catch (Exception e) {
            // Get请求模式会转换异常，忽略，只取header
        }
        return paramsMap;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
