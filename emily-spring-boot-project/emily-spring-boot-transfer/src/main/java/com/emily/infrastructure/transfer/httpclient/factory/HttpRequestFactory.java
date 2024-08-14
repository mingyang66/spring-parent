package com.emily.infrastructure.transfer.httpclient.factory;

import com.emily.infrastructure.common.constant.AttributeInfo;
import com.emily.infrastructure.common.constant.CharacterInfo;
import com.emily.infrastructure.common.constant.CharsetInfo;
import com.emily.infrastructure.json.JsonUtils;
import com.google.common.collect.Maps;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 请求服务类
 *
 * @author Emily
 * @since 4.0.7
 */
public class HttpRequestFactory {


    /**
     * HttpClient 获取返回结果对象
     *
     * @param body 返回结果字节数组
     * @return 请求响应结果
     */
    public static Object getResponseBody(byte[] body) {
        try {
            return JsonUtils.toObject(body, Object.class);
        } catch (Exception e) {
            return IOUtils.toString(body, CharsetInfo.UTF_8);
        }
    }

    /**
     * HttpClient 获取参数对象及请求header
     *
     * @param headers 请求头
     * @param params  字节数组参数
     * @return 参数集合
     */
    public static Map<String, Object> getArgs(HttpHeaders headers, byte[] params) {
        Map<String, Object> dataMap = Maps.newLinkedHashMap();
        dataMap.put(AttributeInfo.HEADERS, headers);
        dataMap.put(AttributeInfo.PARAMS, byteArgToMap(params));
        return dataMap;
    }

    /**
     * 将byte[]转换为Map对象
     *
     * @param params 字节数组参数
     * @return 转换后的Map参数集合
     */
    protected static Map<?, ?> byteArgToMap(byte[] params) {
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

}
