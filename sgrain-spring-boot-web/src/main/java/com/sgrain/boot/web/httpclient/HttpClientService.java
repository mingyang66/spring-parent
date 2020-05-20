package com.sgrain.boot.web.httpclient;


import com.google.common.collect.Maps;
import com.sgrain.boot.common.utils.CharacterUtils;
import com.sgrain.boot.common.utils.HiddenUtils;
import com.sgrain.boot.common.utils.LoggerUtils;
import com.sgrain.boot.common.utils.ObjectSizeUtil;
import com.sgrain.boot.common.utils.json.JSONUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

/**
 * 发送Http请求Client服务类
 */
@SuppressWarnings("all")
@Component
public class HttpClientService {

    @Autowired
    @Lazy
    private RestTemplate restTemplate;

    /**
     * 支持参数为非数组模式POST请求
     * @param paramsMap    参数，可以为null,
     *                     服务端接收参数为：application/json类型（@RequestBody）传递Map<String, Object> paramMap = new HashMap<>();
     *                     服务端接收参数为：application/x-www-form-urlencoded表单类型时，传递参数MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>()
     *
     * header头示例：MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
     */
    public <T> T post(String url, Object params, MultiValueMap<String, String> headers, Class<T> responseType) {
        StopWatch watch = StopWatch.createStarted();
        ResponseEntity<T> entity = restTemplate.postForEntity(url, getHttpHeaders(params, headers), responseType);
        watch.stop();
        //输出请求日志
        logInfo(url, HttpMethod.POST, params, entity, watch.getTime());
        return entity.getBody();
    }

    /**
     * 支持参数为非数组模式POST请求
     * @param paramsMap    参数，可以为null,
     *                     服务端接收参数为：application/json类型（@RequestBody）传递Map<String, Object> paramMap = new HashMap<>();
     *                     服务端接收参数为：application/x-www-form-urlencoded类型时，传递参数MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>()
     * header头示例：MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
     */
    public <T> T post(String url, Object params, MultiValueMap<String, String> headers, Class<T> responseType, Object... uriVariables) {
        StopWatch watch = StopWatch.createStarted();
        ResponseEntity<T> entity = restTemplate.postForEntity(url, getHttpHeaders(params, headers), responseType, uriVariables);
        watch.stop();
        //输出请求日志
        logInfo(url, HttpMethod.POST, params, entity, watch.getTime());
        return entity.getBody();

    }

    /**
     * 支持参数为非数组模式POST请求
     * @param paramsMap    参数，可以为null,
     *                     服务端接收参数为：application/json类型（@RequestBody）传递Map<String, Object> paramMap = new HashMap<>();
     *                     服务端接收参数为：application/x-www-form-urlencoded类型时，传递参数MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>()
     * header头示例：MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
     */
    public <T> T post(String url, Object params, MultiValueMap<String, String> headers, Class<T> responseType, Map<String, ?> uriVariables) {
        StopWatch watch = StopWatch.createStarted();
        ResponseEntity<T> entity = restTemplate.postForEntity(url, getHttpHeaders(params, headers), responseType, uriVariables);
        watch.stop();
        //输出请求日志
        logInfo(url, HttpMethod.POST, params, entity, watch.getTime());
        return entity.getBody();

    }

    /**
     * @param url          请求地址
     * @param paramsMap    参数
     * @param responseType 响应类型
     * @param uriVariables url可变参数
     * @param <T>          返回值类型
     * @return
     */
    public <T> T get(String url, Map<String, Object> paramsMap, Class<T> responseType, Object... uriVariables) {
        return get(joinUrl(url, paramsMap), responseType, uriVariables);
    }

    /**
     * 支持参数为非数组模式POST请求
     */
    public <T> T get(String url, Class<T> responseType, Object... uriVariables) {
        StopWatch watch = StopWatch.createStarted();
        ResponseEntity<T> entity = restTemplate.getForEntity(url, responseType, uriVariables);
        watch.stop();
        //输出请求日志
        logInfo(url, HttpMethod.GET, Collections.emptyMap(), entity, watch.getTime());
        return entity.getBody();
    }

    /**
     * 支持参数为非数组模式POST请求
     */
    public <T> T get(String url, Class<T> responseType, Map<String, ?> uriVariables) {
        StopWatch watch = StopWatch.createStarted();
        ResponseEntity<T> entity = restTemplate.getForEntity(url, responseType, uriVariables);
        watch.stop();
        //输出请求日志
        logInfo(url, HttpMethod.GET, Collections.emptyMap(), entity, watch.getTime());
        return entity.getBody();
    }

    /**
     * 获取指定类型的返回指
     *
     * @param url          请求URL
     * @param paramsMap    参数，可以为null
     * @param headers      请求header
     * @param responseType 响应类型
     * @param <T>          返回值得实际类型
     * @return
     */
    public <T> T get(String url, Map<String, Object> paramsMap, MultiValueMap<String, String> headers, ParameterizedTypeReference<T> responseType) {
        StopWatch watch = StopWatch.createStarted();
        HttpEntity<?> requestEntity = getHttpHeaders(null, headers);
        ResponseEntity<T> entity = restTemplate.exchange(joinUrl(url, paramsMap), HttpMethod.GET, requestEntity, responseType);
        watch.stop();
        //输出请求日志
        logInfo(url, HttpMethod.GET, paramsMap, entity, watch.getTime());
        return entity.getBody();
    }

    /**
     * 获取指定类型的返回指
     *
     * @param url          请求URL
     * @param paramsMap    参数，可以为null,
     *                     服务端接收参数为：application/json类型（@RequestBody）传递Map<String, Object> paramMap = new HashMap<>();
     *                     服务端接收参数为：application/x-www-form-urlencoded类型时，传递参数MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>()
     * @param headers      请求header
     * @param responseType 响应类型
     * @param <T>          返回值得实际类型
     * @return
     */
    public <T> T post(String url, Object paramsMap, MultiValueMap<String, String> headers, ParameterizedTypeReference<T> responseType) {
        StopWatch watch = StopWatch.createStarted();
        ResponseEntity<T> entity = restTemplate.exchange(url, HttpMethod.POST, getHttpHeaders(paramsMap, headers), responseType);
        watch.stop();
        //输出请求日志
        logInfo(url, HttpMethod.GET, paramsMap, entity, watch.getTime());
        return entity.getBody();
    }

    public <T> HttpEntity<T> getHttpHeaders(T params, MultiValueMap<String, String> headers) {
        HttpEntity httpEntity;
        if (ObjectUtils.isEmpty(headers)) {
            httpEntity = new HttpEntity(params);
        } else {
            httpEntity = new HttpEntity(params, headers);
        }
        return httpEntity;
    }

    /**
     * 拼接url
     *
     * @param url       请求地址
     * @param paramsMap 参数
     * @return 拼接后的url
     */
    public String joinUrl(String url, Map<String, Object> paramsMap) {
        if (ObjectUtils.isNotEmpty(paramsMap)) {
            StringBuffer sb = new StringBuffer();
            paramsMap.keySet().forEach(key -> {
                if (sb.length() != 0) {
                    sb.append(CharacterUtils.AND_AIGN);
                }
                sb.append(StringUtils.join(key, CharacterUtils.EQUAL_SIGN, paramsMap.get(key)));
            });
            if (url.contains(CharacterUtils.ASK_SIGN_EN)) {
                url = StringUtils.join(url, CharacterUtils.AND_AIGN, sb.toString());
            } else {
                url = StringUtils.join(url, CharacterUtils.ASK_SIGN_EN, sb.toString());
            }
        }
        return url;
    }

    /**
     * 日志信息
     */
    private <T> void logInfo(String url, HttpMethod httpMethod, Object params, ResponseEntity<T> entity, long time) {
        Object paramObj = Collections.emptyMap();
        if (ObjectUtils.isNotEmpty(params) && (params instanceof Map)) {
            String[] urlArray = StringUtils.split(url, "/");
            String routeStr = StringUtils.join(ArrayUtils.subarray(urlArray, 2, urlArray.length), "/");
            String route = StringUtils.prependIfMissing(routeStr, "/");
            paramObj = HiddenUtils.hidden((Map<String, Object>) params, route);
        }
        Map<String, Object> logMap = Maps.newLinkedHashMap();
        logMap.put("Request Url", url);
        logMap.put("Request Method", httpMethod);
        if (httpMethod.name().matches(HttpMethod.POST.name())) {
            logMap.put("Request Params", JSONUtils.toJSONString(paramObj));
        }
        logMap.put("Spend Time", StringUtils.join(time, "ms"));
        logMap.put("DataSize", ObjectSizeUtil.getObjectSizeUnit(entity.getBody()));
        logMap.put("Response Data", entity.getBody());
        if (LoggerUtils.isDebug()) {
            LoggerUtils.info(HttpClientService.class, JSONUtils.toJSONPrettyString(logMap));
        } else {
            LoggerUtils.info(HttpClientService.class, JSONUtils.toJSONString(logMap));
        }
    }

}
