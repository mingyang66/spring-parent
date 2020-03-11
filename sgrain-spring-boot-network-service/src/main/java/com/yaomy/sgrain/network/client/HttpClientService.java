package com.yaomy.sgrain.network.client;


import com.yaomy.sgrain.common.control.utils.json.JSONUtils;
import com.yaomy.sgrain.logback.utils.LoggerUtil;
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
 * @Description: 发送Http请求Client服务类
 * @Version: 1.0
 */
@Component
@SuppressWarnings("all")
public class HttpClientService {

    @Autowired
    @Lazy
    private RestTemplate restTemplate;
    /**
     * @Description 支持参数为非数组模式POST请求
     * @Version  1.0
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
     * @Description 支持参数为非数组模式POST请求
     * @Version  1.0
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
     * @Description 支持参数为非数组模式POST请求
     * @Version  1.0
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
     * @Description 支持文件上传、多参数、参数为数组模式、参数为非数组模式
     * @Version  1.0
     */
    @Deprecated
    public <T> T postMulti(String url, MultiValueMap<String, Object> params, MultiValueMap<String, String> headers, Class<T> responseType){
        StopWatch watch = StopWatch.createStarted();
        ResponseEntity<T> entity = restTemplate.postForEntity(url, getHttpHeaders(params, headers), responseType);
        watch.stop();
        //输出请求日志
        logInfo(url, HttpMethod.POST, params, headers, responseType, entity, watch.getTime());
        return entity.getBody();
    }
    /**
     * @Description 支持文件上传、多参数、参数为数组模式、参数为非数组模式
     * @Version  1.0
     */
    @Deprecated
    public <T> T  postMulti(String url, MultiValueMap<String, Object> params, MultiValueMap<String, String> headers, Class<T> responseType, Object... uriVariables){
        StopWatch watch = StopWatch.createStarted();
        ResponseEntity<T> entity = restTemplate.postForEntity(url, getHttpHeaders(params, headers), responseType, uriVariables);
        watch.stop();
        //输出请求日志
        logInfo(url, HttpMethod.POST, params, headers, responseType, entity, watch.getTime());
        return entity.getBody();
    }
    /**
     * @Description 支持文件上传、多参数、参数为数组模式、参数为非数组模式
     * @Version  1.0
     */
    @Deprecated
    public<T> T  postMulti(String url, MultiValueMap<String, Object> params, MultiValueMap<String, String> headers, Class<T> responseType, Map<String, ?> uriVariables){
        StopWatch watch = StopWatch.createStarted();
        ResponseEntity<T> entity = restTemplate.postForEntity(url, getHttpHeaders(params, headers), responseType, uriVariables);
        watch.stop();
        //输出请求日志
        logInfo(url, HttpMethod.POST, params, headers, responseType, entity, watch.getTime());
        return entity.getBody();
    }
    /**
     * -----------------------------GET----------------------------------------------
     */
    /**
     * @Description 支持参数为非数组模式POST请求
     * @Version  1.0
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
     * @Description 支持参数为非数组模式POST请求
     * @Version  1.0
     */
    public<T> T  get(String url, Class<T> responseType, Map<String, ?> uriVariables){
        StopWatch watch = StopWatch.createStarted();
        ResponseEntity<T> entity = restTemplate.getForEntity(url, responseType, uriVariables);
        watch.stop();
        //输出请求日志
        logInfo(url, HttpMethod.GET, Collections.emptyMap(), null, responseType, entity, watch.getTime());
        return entity.getBody();
    }

    /**
     *
     * @param params 参数
     * @param headers header
     * @return
     */
    private HttpEntity getHttpHeaders(Object params, MultiValueMap<String, String> headers){
        HttpEntity httpEntity = null;
        if(ObjectUtils.isEmpty(headers)){
            httpEntity = new HttpEntity(params);
        } else {
            httpEntity = new HttpEntity(params, headers);
        }
        return httpEntity;
    }
    /**
     *
     * @param params 参数
     * @param headers header
     * @return
     */
    @Deprecated
    private HttpEntity getHttpHeaders(MultiValueMap<String, Object> params, MultiValueMap<String, String> headers){
        HttpEntity httpEntity = null;
        if(ObjectUtils.isEmpty(headers)){
            httpEntity = new HttpEntity(params);
        } else {
            httpEntity = new HttpEntity(params, headers);
        }
        return httpEntity;
    }
    /**
     * @Description 日志信息
     * @Author 姚明洋
     * @Date 2019/8/30 13:06
     * @Version  1.0
     */
    private <T> void logInfo(String url, HttpMethod httpMethod, Object params, MultiValueMap<String, String> headers, Class<T> responseType, ResponseEntity<T> entity, long time){
        String log = StringUtils.join("\n", "访问URL :", url, "\n", "Method  :", httpMethod, "\n");
        if(!ObjectUtils.isEmpty(params) && (params instanceof Map)){
            log = StringUtils.join(log, "请求参数：", JSONUtils.toJSONString(params), "\n");
        } else{
            log = StringUtils.join(log, "请求参数：", params, "\n");
        }
        if(!ObjectUtils.isEmpty(headers)){
            log = StringUtils.join(log, "Headers：", JSONUtils.toJSONString(headers), "\n");
        }
        log = StringUtils.join(log, "耗    时：", time, "毫秒\n");
        log = StringUtils.join(log, "返回类型：", responseType.getTypeName(), "\n");
        log = StringUtils.join(log, "返回结果：", JSONUtils.toJSONString(entity.getBody()));
        LoggerUtil.info(HttpClientService.class, log);
    }
    /**
     * @Description 日志信息
     * @Version  1.0
     */
    @Deprecated
    private <T> void logInfo(String url, HttpMethod httpMethod, MultiValueMap<String, Object> params, MultiValueMap<String, String> headers, Class<T> responseType, ResponseEntity<T> entity, long time){
        String log = StringUtils.join("\n", "访问URL :", url, "\n", "Method  :", HttpMethod.POST, "\n");
        if(!ObjectUtils.isEmpty(params)){
            log = StringUtils.join(log, "请求参数：", JSONUtils.toJSONString(params), "\n");
        }
        if(!ObjectUtils.isEmpty(headers)){
            log = StringUtils.join(log, "Headers：", JSONUtils.toJSONString(headers), "\n");
        }
        log = StringUtils.join(log, "耗    时：", time, "毫秒\n");
        log = StringUtils.join(log, "返回类型：", responseType.getTypeName(), "\n");
        log = StringUtils.join(log, "返回结果：", JSONUtils.toJSONString(entity.getBody()), "\n");
        LoggerUtil.info(HttpClientService.class, log);
    }
}
