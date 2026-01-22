package com.emily.infrastructure.transfer.feign.interceptor;

import com.emily.infrastructure.common.constant.AttributeInfo;
import com.emily.infrastructure.common.constant.CharacterInfo;
import com.emily.infrastructure.common.constant.HeaderInfo;
import com.emily.infrastructure.logback.entity.BaseLogger;
import com.emily.infrastructure.tracing.holder.LocalContextHolder;
import com.emily.infrastructure.transfer.feign.context.FeignContextHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

/**
 * feign请求日志拦截
 *
 * @author Emily
 * @since 2021/03/31
 */
public class FeignRequestInterceptor implements RequestInterceptor, PriorityOrdered {

    @Override
    public void apply(RequestTemplate template) {
        template.header(HeaderInfo.TRACE_ID, LocalContextHolder.current().getTraceId());
        template.header(HeaderInfo.LANGUAGE, LocalContextHolder.current().getLanguage());
        template.header(HeaderInfo.APP_TYPE, LocalContextHolder.current().getAppType());
        template.header(HeaderInfo.APP_VERSION, LocalContextHolder.current().getAppVersion());
        //封装异步日志信息
        BaseLogger baseLogger = new BaseLogger()
                //请求url
                .url(String.format("%s%s", StringUtils.rightPad(template.feignTarget().url(), 1, CharacterInfo.PATH_SEPARATOR), RegExUtils.replaceFirst(template.url(), CharacterInfo.PATH_SEPARATOR, "")))
                //请求参数
                .params(AttributeInfo.HEADERS, template.headers());
        // 将日志信息放入请求对象
        FeignContextHolder.bind(baseLogger);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
