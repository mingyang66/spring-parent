package com.emily.infrastructure.web.filter.helper;

import com.emily.infrastructure.aop.utils.MethodInvocationUtils;
import com.emily.infrastructure.common.constant.AttributeInfo;
import com.emily.infrastructure.common.constant.CharacterInfo;
import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.sensitive.DataMaskUtils;
import com.emily.infrastructure.sensitive.SensitiveUtils;
import com.emily.infrastructure.sensitive.annotation.JsonSimField;
import com.google.common.collect.Maps;
import com.otter.infrastructure.servlet.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求服务类
 *
 * @author Emily
 * @since 4.0.7
 */
public class MethodHelper {

    /**
     * 获取请求入参, 给API请求控制器获取入参
     *
     * @param invocation 方法反射对象
     * @return 请求入参
     */
    public static Map<String, Object> getApiArgs(MethodInvocation invocation, HttpServletRequest request) {
        Assert.notNull(invocation, () -> "MethodInvocation must not be null");
        Assert.notNull(request, () -> "HttpServletRequest must not be null");
        return new LinkedHashMap<>(Map.ofEntries(
                //获取请求头
                Map.entry(AttributeInfo.HEADERS, RequestUtils.getHeaders(request)),
                //获取Body请求参数
                Map.entry(AttributeInfo.PARAMS_BODY, getMethodArgs(invocation)),
                //获取Get、POST等URL后缀请求参数
                Map.entry(AttributeInfo.PARAMS_URL, RequestUtils.getParameters(request))
        ));
    }

    /**
     * 获取请求入参, 给API请求控制器获取入参
     *
     * @param request 请求对象
     * @return 请求入参
     */
    public static Map<String, Object> getApiArgs(HttpServletRequest request) {
        Assert.notNull(request, () -> "HttpServletRequest must not be null");
        //请求参数
        Map<String, Object> paramMap = new LinkedHashMap<>();
        if (request instanceof ContentCachingRequestWrapper requestWrapper) {
            paramMap.putAll(strArgToMap(requestWrapper.getContentAsString()));
        }
        return new LinkedHashMap<>(Map.ofEntries(
                //获取请求头
                Map.entry(AttributeInfo.HEADERS, RequestUtils.getHeaders(request)),
                //获取Body请求参数
                Map.entry(AttributeInfo.PARAMS_BODY, paramMap),
                //获取Get、POST等URL后缀请求参数
                Map.entry(AttributeInfo.PARAMS_URL, RequestUtils.getParameters(request))
        ));
    }


    /**
     * 将byte[]转换为Map对象
     * 1. POST传递实体类参数转Map对象；
     * 2. GET传递实体类参数转Map对象，不建议，但是存在这种场景
     *
     * @param value 字节数组参数
     * @return 转换后的Map参数集合
     */
    protected static Map<String, Object> strArgToMap(String value) {
        if (value == null || value.isEmpty() || value.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return JsonUtils.toJavaBean(value, Map.class, String.class, Object.class);
        } catch (Exception e) {
            // return strToMap(IOUtils.toString(params, CharsetInfo.UTF_8));
            return Collections.emptyMap();
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
            pMap.put(AttributeInfo.PARAMS_URL, toObject(param));
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
        Assert.notNull(param, () -> "非法参数");
        if (param.startsWith(CharacterInfo.LEFT_SQ)) {
            return JsonUtils.toJavaBean(param, List.class);
        }
        return param;
    }

    /**
     * 1. 支持参数为实体类的脱敏处理；
     * 2. 支持单个参数的脱敏处理；
     *
     * @param invocation 方法切面对象
     * @return 返回调用方法的参数及参数值
     */
    public static Map<String, Object> getMethodArgs(MethodInvocation invocation) {
        return MethodInvocationUtils.getMethodArgs(invocation, value -> value instanceof HttpServletRequest || value instanceof HttpServletResponse,
                (parameter, value) -> {
                    if (value instanceof String str) {
                        if (parameter.isAnnotationPresent(JsonSimField.class)) {
                            return DataMaskUtils.doGetProperty(str, parameter.getAnnotation(JsonSimField.class).value());
                        } else {
                            return value;
                        }
                    } else {
                        return SensitiveUtils.acquireElseGet(value);
                    }
                });
    }

}
