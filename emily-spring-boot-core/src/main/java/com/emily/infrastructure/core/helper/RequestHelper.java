package com.emily.infrastructure.core.helper;

import com.emily.infrastructure.common.constant.AttributeInfo;
import com.emily.infrastructure.common.utils.RequestUtils;
import com.emily.infrastructure.common.utils.constant.CharacterUtils;
import com.emily.infrastructure.common.utils.constant.CharsetUtils;
import com.emily.infrastructure.common.utils.io.IOUtils;
import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.core.servlet.DelegateRequestWrapper;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
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
            Map<String, Object> body = getParameterMap(requestWrapper.getRequestBody());
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

        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String key = names.nextElement();
            paramMap.put(key, request.getParameter(key));
        }
        return paramMap;
    }

    /**
     * HttpClient 获取返回结果对象
     *
     * @param body 返回结果字节数组
     * @return
     */
    public static Object getResponseBody(byte[] body) {
        try {
            return JSONUtils.toObject(body, Object.class);
        } catch (Exception e) {
            return IOUtils.toString(body, CharsetUtils.UTF_8);
        }
    }

    /**
     * 获取参数对象
     *
     * @param params
     * @return
     */
    public static Map<String, Object> getParameterMap(byte[] params) {
        try {
            return JSONUtils.toObject(params, Map.class);
        } catch (Exception e) {
            return convertParameterToMap(IOUtils.toString(params, CharsetUtils.UTF_8));
        }
    }

    /**
     * 将参数转换为Map类型
     *
     * @param param
     * @return
     */
    private static Map<String, Object> convertParameterToMap(String param) {
        if (StringUtils.isEmpty(param)) {
            return Collections.emptyMap();
        }
        Map<String, Object> pMap = Maps.newLinkedHashMap();
        String[] pArray = StringUtils.split(param, CharacterUtils.AND_AIGN);
        for (int i = 0; i < pArray.length; i++) {
            String[] array = StringUtils.split(pArray[i], CharacterUtils.EQUAL_SIGN);
            if (array.length == 2) {
                pMap.put(array[0], array[1]);
            }
        }
        return pMap;
    }

    /**
     * 获取耗时字段
     * @return
     */
    public static long getTime() {
        if (!RequestUtils.isServletContext()) {
            return 0L;
        }
        Object time = RequestUtils.getRequest().getAttribute(AttributeInfo.TIME);
        if (Objects.nonNull(time)) {
            return Long.valueOf(String.valueOf(time));
        }
        return 0L;
    }
}
