package com.emily.framework.context.request;

import com.emily.framework.context.servlet.RequestWrapper;
import com.emily.framework.common.utils.RequestUtils;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @program: spring-parent
 * @description: 请求服务类
 * @create: 2020/11/23
 */
public class RequestService {
    /**
     * 获取请求入参
     *
     * @param request
     * @return
     */
    public static Map<String, Object> getParameterMap(HttpServletRequest request) {
        Map<String, Object> paramMap = new LinkedHashMap<>();
        if(request instanceof RequestWrapper){
            RequestWrapper requestWrapper = (RequestWrapper) request;
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
