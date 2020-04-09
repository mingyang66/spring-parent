package com.sgrain.boot.web.httpclient;



import com.sgrain.boot.common.utils.HiddenUtils;
import com.sgrain.boot.common.utils.json.JSONUtils;
import com.sgrain.boot.common.utils.LoggerUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
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
     */
    public <T> T  post(String url, Object params, MultiValueMap<String, String> headers, Class<T> responseType){
        StopWatch watch = StopWatch.createStarted();
        ResponseEntity<T> entity = restTemplate.postForEntity(url, getHttpHeaders(params, headers), responseType);
        watch.stop();
        //输出请求日志
        logInfo(url, HttpMethod.POST, params, headers, responseType, entity, watch.getTime());
        return entity.getBody();
    }
    /**
     * 支持参数为非数组模式POST请求
     */
    public <T> T  post(String url, Object params, MultiValueMap<String, String> headers, Class<T> responseType, Object... uriVariables){
        StopWatch watch = StopWatch.createStarted();
        ResponseEntity<T> entity = restTemplate.postForEntity(url, getHttpHeaders(params, headers), responseType, uriVariables);
        watch.stop();
        //输出请求日志
        logInfo(url, HttpMethod.POST, params, headers, responseType, entity, watch.getTime());
        return entity.getBody();

    }
    /**
     * 支持参数为非数组模式POST请求
     */
    public<T> T  post(String url, Object params, MultiValueMap<String, String> headers, Class<T> responseType, Map<String, ?> uriVariables){
        StopWatch watch = StopWatch.createStarted();
        ResponseEntity<T> entity = restTemplate.postForEntity(url, getHttpHeaders(params, headers), responseType, uriVariables);
        watch.stop();
        //输出请求日志
        logInfo(url, HttpMethod.POST, params, headers, responseType, entity, watch.getTime());
        return entity.getBody();

    }
    /**
     * 支持参数为非数组模式POST请求
     */
    public <T> T  get(String url, Class<T> responseType, Object... uriVariables){
        StopWatch watch = StopWatch.createStarted();
        ResponseEntity<T> entity = restTemplate.getForEntity(url, responseType, uriVariables);
        watch.stop();
        //输出请求日志
        logInfo(url, HttpMethod.GET, Collections.emptyMap(), null, responseType, entity, watch.getTime());
        return entity.getBody();
    }
    /**
     * 支持参数为非数组模式POST请求
     */
    public<T> T  get(String url, Class<T> responseType, Map<String, ?> uriVariables){
        StopWatch watch = StopWatch.createStarted();
        ResponseEntity<T> entity = restTemplate.getForEntity(url, responseType, uriVariables);
        watch.stop();
        //输出请求日志
        logInfo(url, HttpMethod.GET, Collections.emptyMap(), null, responseType, entity, watch.getTime());
        return entity.getBody();
    }

    private HttpEntity getHttpHeaders(Object params, MultiValueMap<String, String> headers){
        HttpEntity httpEntity;
        if(ObjectUtils.isEmpty(headers)){
            httpEntity = new HttpEntity(params);
        } else {
            httpEntity = new HttpEntity(params, headers);
        }
        return httpEntity;
    }
    /**
     * 日志信息
     */
    private <T> void logInfo(String url, HttpMethod httpMethod, Object params, MultiValueMap<String, String> headers, Class<T> responseType, ResponseEntity<T> entity, long time){
        Object paramObj = params;
        if(params instanceof Map){
            String[] urlArray = StringUtils.split(url, "/");
            String routeStr = StringUtils.join(ArrayUtils.subarray(urlArray, 2, urlArray.length), "/");
            String route = StringUtils.prependIfMissing(routeStr, "/");
            paramObj = HiddenUtils.hidden((Map<String, Object>) params, route);
        }
        String log = StringUtils.join("\n", "访问URL :", url, "\n", "Method  :", httpMethod, "\n");
        if(!ObjectUtils.isEmpty(params) && (params instanceof Map)){
            log = StringUtils.join(log, "请求参数：", JSONUtils.toJSONString(paramObj), "\n");
        } else{
            log = StringUtils.join(log, "请求参数：", params, "\n");
        }
        if(!ObjectUtils.isEmpty(headers)){
            log = StringUtils.join(log, "Headers：", JSONUtils.toJSONString(headers), "\n");
        }
        log = StringUtils.join(log, "耗    时：", time, "毫秒\n");
        log = StringUtils.join(log, "返回类型：", responseType.getTypeName(), "\n");
        log = StringUtils.join(log, "返回结果：", JSONUtils.toJSONString(entity.getBody()));
        LoggerUtils.info(HttpClientService.class, log);
    }

}
