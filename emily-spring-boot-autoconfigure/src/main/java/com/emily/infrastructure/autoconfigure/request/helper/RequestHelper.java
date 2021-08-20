package com.emily.infrastructure.autoconfigure.request.helper;

import com.emily.infrastructure.common.utils.RequestUtils;
import com.emily.infrastructure.autoconfigure.request.servlet.DelegateRequestWrapper;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

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
     * @param request
     * @return
     */
    public static Map<String, Object> getParameterMap(HttpServletRequest request) {
        Map<String, Object> paramMap = new LinkedHashMap<>();
        if(request instanceof DelegateRequestWrapper){
            DelegateRequestWrapper requestWrapper = (DelegateRequestWrapper) request;
            Map<String, Object> body = RequestUtils.getParameterMap(requestWrapper.getRequestBody());
            if (!CollectionUtils.isEmpty(body)) {
                paramMap.putAll(body);
            }
        }
        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String key = names.nextElement();
            paramMap.put(key, request.getParameter(key));
        }

        return paramMap;
    }
}
