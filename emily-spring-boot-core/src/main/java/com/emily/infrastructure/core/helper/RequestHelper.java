package com.emily.infrastructure.core.helper;

import com.emily.infrastructure.common.constant.AttributeInfo;
import com.emily.infrastructure.common.constant.CharacterInfo;
import com.emily.infrastructure.common.constant.CharsetInfo;
import com.emily.infrastructure.core.servlet.filter.DelegateRequestWrapper;
import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.sensitive.DataMaskUtils;
import com.emily.infrastructure.sensitive.JsonSimField;
import com.emily.infrastructure.sensitive.SensitiveUtils;
import com.google.common.collect.Maps;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @author Emily
 * @program: spring-parent
 * @Description: 请求服务类
 * @create: 2020/11/23
 * @since 4.0.7
 */
public class RequestHelper {

    /**
     * @return 请求入参
     * @Description: 获取请求入参, 给API请求控制器获取入参
     */
    public static Map<String, Object> getApiArgs(MethodInvocation invocation) {
        if (RequestUtils.isServlet()) {
            return getArgs(invocation, RequestUtils.getRequest());
        }
        return Collections.emptyMap();
    }

    /**
     * @return 请求入参
     * @Description: 获取请求入参, 给API请求控制器获取入参
     */
    public static Map<String, Object> getApiArgs(HttpServletRequest request) {
        if (RequestUtils.isServlet()) {
            return getArgs(null, request);
        }
        return Collections.emptyMap();
    }

    /**
     * @param request servlet请求对象
     * @return 请求入参
     * @Description: 获取请求入参
     */
    private static Map<String, Object> getArgs(MethodInvocation invocation, HttpServletRequest request) {
        //请求参数
        Map<String, Object> paramMap = new LinkedHashMap<>();
        if (Objects.isNull(invocation)) {
            if (request instanceof DelegateRequestWrapper) {
                DelegateRequestWrapper requestWrapper = (DelegateRequestWrapper) request;
                paramMap.putAll(byteArgToMap(requestWrapper.getRequestBody()));
            }
        } else {
            paramMap.putAll(getMethodArgs(invocation));
        }

        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String key = names.nextElement();
            if (!paramMap.containsKey(key)) {
                paramMap.put(key, request.getParameter(key));
            }
        }
        // 请求参数&请求头
        Map<String, Object> dataMap = new LinkedHashMap<>();
        // 获取请求头
        dataMap.put(AttributeInfo.HEADERS, getHeaders(request));
        // 参数
        dataMap.put(AttributeInfo.PARAMS, paramMap);
        return dataMap;
    }

    /**
     * @param request 请求servlet对象
     * @return 请求头集合对象
     * @Description: 获取请求头
     */
    public static Map<String, Object> getHeaders(HttpServletRequest request) {
        Map<String, Object> headers = new LinkedHashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        Optional.ofNullable(headerNames).ifPresent(headerName -> {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                String value = request.getHeader(name);
                headers.put(name, value);
            }
        });
        return headers;
    }

    /**
     * HttpClient 获取返回结果对象
     *
     * @param body 返回结果字节数组
     */
    public static Object getHttpClientResponseBody(byte[] body) {
        try {
            return JsonUtils.toObject(body, Object.class);
        } catch (Exception e) {
            return IOUtils.toString(body, CharsetInfo.UTF_8);
        }
    }

    /**
     * HttpClient 获取参数对象及请求header
     */
    public static Map<String, Object> getHttpClientArgs(HttpHeaders headers, byte[] params) {
        Map<String, Object> dataMap = Maps.newLinkedHashMap();
        dataMap.put(AttributeInfo.HEADERS, headers);
        dataMap.put(AttributeInfo.PARAMS, byteArgToMap(params));
        return dataMap;
    }

    /**
     * 将byte[]转换为Map对象
     */
    protected static Map byteArgToMap(byte[] params) {
        try {
            return JsonUtils.toObject(params, Map.class);
        } catch (Exception e) {
            return strToMap(IOUtils.toString(params, CharsetInfo.UTF_8));
        }
    }

    /**
     * 将参数转换为Map类型
     */
    protected static Map<String, Object> strToMap(String param) {
        if (StringUtils.isEmpty(param)) {
            return Collections.emptyMap();
        }
        Map<String, Object> pMap = Maps.newLinkedHashMap();
        String[] pArray = StringUtils.split(param, CharacterInfo.AND_AIGN);
        for (String arr : pArray) {
            String[] array = StringUtils.split(arr, CharacterInfo.EQUAL_SIGN);
            if (array.length == 2) {
                pMap.put(array[0], array[1]);
            }
        }
        if (pMap.size() == 0) {
            pMap.put(AttributeInfo.PARAMS, toObject(param));
        }
        return pMap;
    }

    /**
     * 将参数转为对象
     */
    protected static Object toObject(String param) {
        Assert.notNull(param, "非法参数");
        if (param.startsWith(CharacterInfo.LEFT_SQ)) {
            return JsonUtils.toJavaBean(param, List.class);
        }
        return param;
    }

    /**
     * @param invocation 方法切面对象
     * @return 返回调用方法的参数及参数值
     * @Description: 获取方法参数，支持指定字段脱敏处理
     */
    public static Map<String, Object> getMethodArgs(MethodInvocation invocation) {
        if (invocation.getArguments().length == 0) {
            return Collections.emptyMap();
        }
        Object[] args = invocation.getArguments();
        Parameter[] parameters = invocation.getMethod().getParameters();
        Map<String, Object> paramMap = Maps.newLinkedHashMap();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            String name = parameter.getName();
            Object value = args[i];
            if (Objects.isNull(value)) {
                paramMap.put(name, null);
            }
            if (checkServletStream(value)) {
                continue;
            }
            if (value instanceof String) {
                if (parameter.isAnnotationPresent(JsonSimField.class)) {
                    paramMap.put(name, DataMaskUtils.doGetProperty((String) value, parameter.getAnnotation(JsonSimField.class).value()));
                } else {
                    paramMap.put(name, value);
                }
            } else {
                paramMap.put(name, SensitiveUtils.acquire(value));
            }
        }
        return paramMap;
    }

    /**
     * 是否继续下一步
     *
     * @param value 对象值
     * @return 校验参数类型是否需要处理
     */
    protected static boolean checkServletStream(Object value) {
        return (value instanceof HttpServletRequest)
                || (value instanceof HttpServletResponse)
                || (value instanceof InputStreamSource);
    }

}
