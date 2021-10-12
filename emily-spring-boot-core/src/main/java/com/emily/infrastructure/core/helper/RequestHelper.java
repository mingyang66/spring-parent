package com.emily.infrastructure.core.helper;

import com.emily.infrastructure.common.utils.RequestUtils;
import com.emily.infrastructure.core.servlet.DelegateRequestWrapper;
import com.google.common.collect.Maps;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author Emily
 * @program: spring-parent
 * @description: 请求服务类
 * @create: 2020/11/23
 */
public class RequestHelper {
    /**
     * 获取请求入参
     *
     * @return
     */
    public static Map<String, Object> getParameterMap() {
        if (RequestUtils.isServletContext()) {
            return getParameterMap(RequestUtils.getRequest());
        }
        return Collections.emptyMap();
    }

    /**
     * 获取请求入参
     *
     * @param request
     * @return
     */
    public static Map<String, Object> getParameterMap(HttpServletRequest request) {
        Map<String, Object> paramMap = new LinkedHashMap<>();
        if (request instanceof DelegateRequestWrapper) {
            DelegateRequestWrapper requestWrapper = (DelegateRequestWrapper) request;
            Map<String, Object> body = RequestUtils.getParameterMap(requestWrapper.getRequestBody());
            if (!CollectionUtils.isEmpty(body)) {
                paramMap.putAll(body);
            }
        }

        Enumeration<String> headerNames = request.getHeaderNames();
        Optional.ofNullable(headerNames).ifPresent(headerName -> {
            Map<String, Object> headers = Maps.newHashMap();
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                String value = request.getHeader(name);
                headers.put(name, value);
            }
            paramMap.put("headers", headers);
        });
        return paramMap;
    }
}
