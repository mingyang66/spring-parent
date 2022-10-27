package com.emily.infrastructure.core.helper;

import com.emily.infrastructure.common.constant.AttributeInfo;
import com.emily.infrastructure.common.constant.CharacterInfo;
import com.emily.infrastructure.common.constant.CharsetInfo;
import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.common.sensitive.JsonIgnore;
import com.emily.infrastructure.common.sensitive.SensitiveUtils;
import com.emily.infrastructure.common.utils.RequestUtils;
import com.emily.infrastructure.common.utils.bean.ParamNameUtils;
import com.emily.infrastructure.common.utils.io.IOUtils;
import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.core.servlet.DelegateRequestWrapper;
import com.emily.infrastructure.logger.LoggerFactory;
import com.google.common.collect.Maps;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.core.io.InputStreamSource;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Emily
 * @program: spring-parent
 * @description: 请求服务类
 * @create: 2020/11/23
 * @since 4.0.7
 */
public class RequestHelper {

    private static final Logger logger = LoggerFactory.getLogger(RequestHelper.class);

    /**
     * 获取请求入参,给API请求控制器获取入参
     *
     * @return
     */
    public static Map<String, Object> getApiArgs(MethodInvocation invocation) {
        if (RequestUtils.isServletContext()) {
            return getArgs(invocation, RequestUtils.getRequest());
        }
        return Collections.emptyMap();
    }

    /**
     * 获取请求入参,给API请求控制器获取入参
     *
     * @return
     */
    public static Map<String, Object> getApiArgs(HttpServletRequest request) {
        if (RequestUtils.isServletContext()) {
            return getArgs(null, request);
        }
        return Collections.emptyMap();
    }

    /**
     * 获取请求入参
     *
     * @param request
     * @return
     */
    private static Map<String, Object> getArgs(MethodInvocation invocation, HttpServletRequest request) {
        Map<String, Object> paramMap = new LinkedHashMap<>();
        if (Objects.isNull(invocation)) {
            if (request instanceof DelegateRequestWrapper) {
                DelegateRequestWrapper requestWrapper = (DelegateRequestWrapper) request;
                Map<String, Object> body = getHttpClientArgs(requestWrapper.getRequestBody());
                if (!CollectionUtils.isEmpty(body)) {
                    paramMap.putAll(body);
                }
            }
        } else {
            paramMap.putAll(getMethodArgs(invocation));
        }
        Enumeration<String> headerNames = request.getHeaderNames();
        Optional.ofNullable(headerNames).ifPresent(headerName -> {
            Map<String, Object> headers = Maps.newHashMap();
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                String value = request.getHeader(name);
                headers.put(name, value);
            }
            paramMap.put(AttributeInfo.HEADERS, headers);
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
    public static Object getHttpClientResponseBody(byte[] body) {
        try {
            return JSONUtils.toObject(body, Object.class);
        } catch (Exception e) {
            return IOUtils.toString(body, CharsetInfo.UTF_8);
        }
    }

    /**
     * HttpClient 获取参数对象
     *
     * @param params
     * @return
     */
    public static Map<String, Object> getHttpClientArgs(byte[] params) {
        try {
            return JSONUtils.toObject(params, Map.class);
        } catch (Exception e) {
            return strToMap(IOUtils.toString(params, CharsetInfo.UTF_8));
        }
    }

    /**
     * 将参数转换为Map类型
     *
     * @param param
     * @return
     */
    private static Map<String, Object> strToMap(String param) {
        if (StringUtils.isEmpty(param)) {
            return Collections.emptyMap();
        }
        Map<String, Object> pMap = Maps.newLinkedHashMap();
        String[] pArray = StringUtils.split(param, CharacterInfo.AND_AIGN);
        for (int i = 0; i < pArray.length; i++) {
            String[] array = StringUtils.split(pArray[i], CharacterInfo.EQUAL_SIGN);
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
     *
     * @param param
     * @return
     */
    private static Object toObject(String param) {
        Assert.notNull(param, "参数不可为空");
        if (param.startsWith(CharacterInfo.LEFT_SQ)) {
            return JSONUtils.toJavaBean(param, List.class);
        }
        return param;
    }

    /**
     * 获取方法参数，支持指定字段脱敏处理
     *
     * @param invocation
     * @param field      脱敏字段
     * @return
     */
    public static Map<String, Object> getMethodArgs(MethodInvocation invocation, String... field) {
        try {
            Method method = invocation.getMethod();
            Map<String, Object> paramMap = Maps.newHashMap();
            List<String> list = ParamNameUtils.getParamNames(method);
            Annotation[][] annotations = method.getParameterAnnotations();
            Object[] obj = invocation.getArguments();
            for (int i = 0; i < list.size(); i++) {
                String name = list.get(i);
                Object value = obj[i];
                if (isFinal(value)) {
                    continue;
                }
                if (Arrays.asList(field).contains(name)) {
                    paramMap.put(name, AttributeInfo.PLACE_HOLDER);
                } else if (value instanceof String) {
                    // 控制器方法参数为字符串并且标记了注解
                    //是否已添加参数
                    boolean flag = true;
                    for (int j = 0; j < annotations[i].length; j++) {
                        Annotation annotation = annotations[i][j];
                        if (annotation instanceof JsonIgnore) {
                            JsonIgnore jsonIgnore = (JsonIgnore) annotation;
                            paramMap.put(name, SensitiveUtils.sensitiveField(jsonIgnore, (String) value));
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        paramMap.put(name, value);
                    }
                } else {
                    paramMap.put(name, SensitiveUtils.sensitive(value));
                }
            }
            return paramMap;
        } catch (Exception e) {
            logger.error(PrintExceptionInfo.printErrorInfo(e));
        }
        return Collections.emptyMap();
    }

    /**
     * 是否继续下一步
     *
     * @param value 对象值
     * @return
     */
    private static boolean isFinal(Object value) {
        if (Objects.isNull(value)) {
            return false;
        } else if (value instanceof HttpServletRequest) {
            return true;
        } else if (value instanceof HttpServletResponse) {
            return true;
        } else if (value instanceof InputStreamSource) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 将Object类型对象转换为Map
     *
     * @param t
     * @param field 脱敏字段--仅支持第一层脱敏
     * @param <T>
     * @return
     */
    public static <T> Map<String, Object> objectToMap(T t, String... field) {
        if (Objects.isNull(t)) {
            return Collections.emptyMap();
        }
        try {
            Map<String, Object> params = Maps.newHashMap();
            //反射获取request属性，构造入参
            Class<?> aClass = Class.forName(t.getClass().getName());
            Field[] fields = aClass.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                f.setAccessible(true);
                String name = f.getName();
                Object value = f.get(t);
                if (isFinal(value)) {
                    continue;
                }
                if (Arrays.asList(field).contains(name)) {
                    params.put(name, AttributeInfo.PLACE_HOLDER);
                } else {
                    params.put(name, value);
                }
            }
            return params;
        } catch (Exception ex) {
            logger.error(PrintExceptionInfo.printErrorInfo(ex));
        }
        return Collections.emptyMap();
    }

    /**
     * 获取耗时字段
     *
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
