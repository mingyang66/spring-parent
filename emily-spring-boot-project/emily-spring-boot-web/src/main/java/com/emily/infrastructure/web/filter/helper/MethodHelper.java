package com.emily.infrastructure.web.filter.helper;

import com.emily.infrastructure.aop.utils.MethodInvocationUtils;
import com.emily.infrastructure.common.constant.AttributeInfo;
import com.emily.infrastructure.desensitize.DataMaskUtils;
import com.emily.infrastructure.desensitize.SensitizeUtils;
import com.emily.infrastructure.desensitize.annotation.DesensitizeProperty;
import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.web.response.entity.BaseResponse;
import com.otter.infrastructure.servlet.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 请求服务类
 *
 * @author Emily
 * @since 4.0.7
 */
public class MethodHelper {
    private static final boolean COMMONS_SENSITIZE_AVAILABLE = ClassUtils.isPresent("com.emily.infrastructure.sensitize.SensitizeUtils", MethodHelper.class.getClassLoader());

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
            return Collections.emptyMap();
        }
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
                    if (COMMONS_SENSITIZE_AVAILABLE) {
                        if (value instanceof String str) {
                            if (parameter.isAnnotationPresent(DesensitizeProperty.class)) {
                                return DataMaskUtils.doGetProperty(str, parameter.getAnnotation(DesensitizeProperty.class).value());
                            } else {
                                return value;
                            }
                        } else {
                            return SensitizeUtils.acquireElseGet(value, e -> {
                                //todo 异常处理
                            });
                        }
                    } else {
                        return value;
                    }
                });
    }

    /**
     * 判定是否对返回值进行脱敏处理
     */
    public static Object getResult(Object response) {
        if (Objects.isNull(response)) {
            return null;
        }
        return COMMONS_SENSITIZE_AVAILABLE ? SensitizeUtils.acquireElseGet(response, e -> {
            //todo 异常处理
        }, BaseResponse.class) : response;
    }
}
