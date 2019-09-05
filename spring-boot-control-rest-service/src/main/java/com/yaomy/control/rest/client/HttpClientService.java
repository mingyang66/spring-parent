package com.yaomy.control.rest.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaomy.control.logback.utils.LoggerUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class HttpClientService {

    @Autowired
    private RestTemplate restTemplate;
    /**
     * @Description 支持参数为非数组模式POST请求
     * @Version  1.0
     */
    public <T> T  post(String url, Map<String, Object> params, MultiValueMap<String, String> headers, Class<T> responseType){
        ResponseEntity<T> entity = restTemplate.postForEntity(url, getHttpHeaders(params, headers), responseType);
        //输出请求日志
        logInfo(url, HttpMethod.POST, params, headers, responseType, entity);
        return entity.getBody();
    }
    /**
     * @Description 支持参数为非数组模式POST请求
     * @Version  1.0
     */
    public <T> T  post(String url, Map<String, Object> params, MultiValueMap<String, String> headers, Class<T> responseType, Object... uriVariables){
        ResponseEntity<T> entity = restTemplate.postForEntity(url, getHttpHeaders(params, headers), responseType, uriVariables);
        //输出请求日志
        logInfo(url, HttpMethod.POST, params, headers, responseType, entity);
        return entity.getBody();

    }
    /**
     * @Description 支持参数为非数组模式POST请求
     * @Version  1.0
     */
    public<T> T  post(String url, Map<String, Object> params, MultiValueMap<String, String> headers, Class<T> responseType, Map<String, ?> uriVariables){
        ResponseEntity<T> entity = restTemplate.postForEntity(url, getHttpHeaders(params, headers), responseType, uriVariables);
        //输出请求日志
        logInfo(url, HttpMethod.POST, params, headers, responseType, entity);
        return entity.getBody();

    }
    /**
     * @Description 支持文件上传、多参数、参数为数组模式、参数为非数组模式
     * @Version  1.0
     */
    public <T> T postMulti(String url, MultiValueMap<String, Object> params, MultiValueMap<String, String> headers, Class<T> responseType){
        ResponseEntity<T> entity = restTemplate.postForEntity(url, getHttpHeaders(params, headers), responseType);
        //输出请求日志
        logInfo(url, HttpMethod.POST, params, headers, responseType, entity);
        return entity.getBody();
    }
    /**
     * @Description 支持文件上传、多参数、参数为数组模式、参数为非数组模式
     * @Version  1.0
     */
    public <T> T  postMulti(String url, MultiValueMap<String, Object> params, MultiValueMap<String, String> headers, Class<T> responseType, Object... uriVariables){
        ResponseEntity<T> entity = restTemplate.postForEntity(url, getHttpHeaders(params, headers), responseType, uriVariables);
        //输出请求日志
        logInfo(url, HttpMethod.POST, params, headers, responseType, entity);
        return entity.getBody();
    }
    /**
     * @Description 支持文件上传、多参数、参数为数组模式、参数为非数组模式
     * @Version  1.0
     */
    public<T> T  postMulti(String url, MultiValueMap<String, Object> params, MultiValueMap<String, String> headers, Class<T> responseType, Map<String, ?> uriVariables){
        ResponseEntity<T> entity = restTemplate.postForEntity(url, getHttpHeaders(params, headers), responseType, uriVariables);
        //输出请求日志
        logInfo(url, HttpMethod.POST, params, headers, responseType, entity);
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
        ResponseEntity<T> entity = restTemplate.getForEntity(url, responseType, uriVariables);
        //输出请求日志
        logInfo(url, HttpMethod.GET, Collections.emptyMap(), null, responseType, entity);
        return entity.getBody();
    }
    /**
     * @Description 支持参数为非数组模式POST请求
     * @Version  1.0
     */
    public<T> T  get(String url, Class<T> responseType, Map<String, ?> uriVariables){
        ResponseEntity<T> entity = restTemplate.getForEntity(url, responseType, uriVariables);
        //输出请求日志
        logInfo(url, HttpMethod.GET, Collections.emptyMap(), null, responseType, entity);
        return entity.getBody();
    }

    /**
     *
     * @param params 参数
     * @param headers header
     * @return
     */
    private HttpEntity getHttpHeaders(Map<String, Object> params, MultiValueMap<String, String> headers){
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
     * @Version  1.0
     */
    private <T> void logInfo(String url, HttpMethod httpMethod, Map<String, Object> params, MultiValueMap<String, String> headers, Class<T> responseType, ResponseEntity<T> entity){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            String log = StringUtils.join("\n", "访问URL :", url, "\n", "Method  :", httpMethod, "\n");
            if(!ObjectUtils.isEmpty(params)){
                log = StringUtils.join(log, "请求参数：", objectMapper.writeValueAsString(params), "\n");
            }
            if(!ObjectUtils.isEmpty(headers)){
                log = StringUtils.join(log, "Headers：", objectMapper.writeValueAsString(headers), "\n");
            }
            log = StringUtils.join(log, "返回类型：", responseType.getTypeName(), "\n");
            log = StringUtils.join(log, "返回结果：", objectMapper.writeValueAsString(entity.getBody()), "\n");
            LoggerUtil.info(HttpClientService.class, log);
        } catch (JsonProcessingException e){
            e.printStackTrace();
            LoggerUtil.error(HttpClientService.class, e.toString());
        }
    }
    /**
     * @Description 日志信息
     * @Version  1.0
     */
    private <T> void logInfo(String url, HttpMethod httpMethod, MultiValueMap<String, Object> params, MultiValueMap<String, String> headers, Class<T> responseType, ResponseEntity<T> entity){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            String log = StringUtils.join("\n", "访问URL :", url, "\n", "Method  :", HttpMethod.POST, "\n");
            if(!ObjectUtils.isEmpty(params)){
                log = StringUtils.join(log, "请求参数：", objectMapper.writeValueAsString(params), "\n");
            }
            if(!ObjectUtils.isEmpty(headers)){
                log = StringUtils.join(log, "Headers：", objectMapper.writeValueAsString(headers), "\n");
            }
            log = StringUtils.join(log, "返回类型：", responseType.getTypeName(), "\n");
            log = StringUtils.join(log, "返回结果：", objectMapper.writeValueAsString(entity.getBody()), "\n");
            LoggerUtil.info(HttpClientService.class, log);
        } catch (JsonProcessingException e){
            e.printStackTrace();
            LoggerUtil.error(HttpClientService.class, e.toString());
        }
    }
}
