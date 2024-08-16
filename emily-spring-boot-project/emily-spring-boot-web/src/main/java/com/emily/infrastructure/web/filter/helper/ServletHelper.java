package com.emily.infrastructure.web.filter.helper;

import com.emily.infrastructure.web.filter.filter.DelegateRequestWrapper;
import com.emily.infrastructure.common.constant.AttributeInfo;
import com.emily.infrastructure.common.constant.CharacterInfo;
import com.emily.infrastructure.common.constant.CharsetInfo;
import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.sensitive.DataMaskUtils;
import com.emily.infrastructure.sensitive.SensitiveUtils;
import com.emily.infrastructure.sensitive.annotation.JsonSimField;
import com.google.common.collect.Maps;
import com.otter.infrastructure.servlet.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.InputStreamSource;
import org.springframework.util.Assert;

import java.lang.reflect.Parameter;
import java.util.*;

/**
 * 请求服务类
 *
 * @author Emily
 * @since 4.0.7
 */
public class ServletHelper {

    /**
     * 获取请求入参, 给API请求控制器获取入参
     *
     * @param invocation 方法反射对象
     * @return 请求入参
     */
    public static Map<String, Object> getApiArgs(MethodInvocation invocation) {
        if (RequestUtils.isServlet()) {
            return getArgs(invocation, RequestUtils.getRequest());
        }
        return Collections.emptyMap();
    }

    /**
     * 获取请求入参, 给API请求控制器获取入参
     *
     * @param request 请求对象
     * @return 请求入参
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
     * 获取请求入参
     */
    private static Map<String, Object> getArgs(MethodInvocation invocation, HttpServletRequest request) {
        //请求参数
        Map<String, Object> paramMap = new LinkedHashMap<>();
        if (Objects.isNull(invocation)) {
            if (request instanceof DelegateRequestWrapper requestWrapper) {
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
        dataMap.put(AttributeInfo.HEADERS, RequestUtils.getHeaders(request));
        // 参数
        dataMap.put(AttributeInfo.PARAMS, paramMap);
        return dataMap;
    }


    /**
     * 将byte[]转换为Map对象
     *
     * @param params 字节数组参数
     * @return 转换后的Map参数集合
     */
    protected static Map<String, Object> byteArgToMap(byte[] params) {
        if (params == null) {
            return Collections.emptyMap();
        }
        try {
            return JsonUtils.toObject(params, Map.class);
        } catch (Exception e) {
            return strToMap(IOUtils.toString(params, CharsetInfo.UTF_8));
        }
    }

    /**
     * 将参数转换为Map类型
     *
     * @param param 字符串参数
     * @return 转换后的参数集合
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
        if (pMap.isEmpty()) {
            pMap.put(AttributeInfo.PARAMS, toObject(param));
        }
        return pMap;
    }

    /**
     * 将参数转为对象
     *
     * @param param 字符串参数
     * @return 转换后的对象
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
     * 获取方法参数，支持指定字段脱敏处理
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
                continue;
            }
            if (checkServletStream(value)) {
                continue;
            }
            if (value instanceof String valueStr) {
                if (parameter.isAnnotationPresent(JsonSimField.class)) {
                    paramMap.put(name, DataMaskUtils.doGetProperty(valueStr, parameter.getAnnotation(JsonSimField.class).value()));
                } else {
                    paramMap.put(name, value);
                }
            } else {
                paramMap.put(name, SensitiveUtils.acquireElseGet(value));
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
