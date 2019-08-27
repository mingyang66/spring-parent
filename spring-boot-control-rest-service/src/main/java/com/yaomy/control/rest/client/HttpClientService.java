package com.yaomy.control.rest.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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
    public <T> T  post(String url, Map<String, Object> params, Class<T> responseType){
        ResponseEntity<T> entity = restTemplate.postForEntity(url, new HttpEntity(params), responseType);
        return entity.getBody();
    }
    /**
     * @Description 支持参数为非数组模式POST请求
     * @Version  1.0
     */
    public <T> T  post(String url, Map<String, Object> params, Class<T> responseType, Object... uriVariables){
        ResponseEntity<T> entity = restTemplate.postForEntity(url, new HttpEntity(params), responseType, uriVariables);
        return entity.getBody();
    }
    /**
     * @Description 支持参数为非数组模式POST请求
     * @Version  1.0
     */
    public<T> T  post(String url, Map<String, Object> params, Class<T> responseType, Map<String, ?> uriVariables){
        ResponseEntity<T> entity = restTemplate.postForEntity(url, new HttpEntity(params), responseType, uriVariables);
        return entity.getBody();
    }
    /**
     * @Description 支持文件上传、参数为数组模式、参数为非数组模式
     * @Version  1.0
     */
    public <T> T postMulti(String url, MultiValueMap<String, Object> params, Class<T> responseType){
        ResponseEntity<T> entity = restTemplate.postForEntity(url, new HttpEntity(params), responseType);
        return entity.getBody();
    }
    /**
     * @Description 支持文件上传、参数为数组模式、参数为非数组模式
     * @Version  1.0
     */
    public <T> T  postMulti(String url, Map<String, Object> params, Class<T> responseType, Object... uriVariables){
        ResponseEntity<T> entity = restTemplate.postForEntity(url, new HttpEntity(params), responseType, uriVariables);
        return entity.getBody();
    }
    /**
     * @Description 支持文件上传、参数为数组模式、参数为非数组模式
     * @Version  1.0
     */
    public<T> T  postMulti(String url, Map<String, Object> params, Class<T> responseType, Map<String, ?> uriVariables){
        ResponseEntity<T> entity = restTemplate.postForEntity(url, new HttpEntity(params), responseType, uriVariables);
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
        return entity.getBody();
    }
    /**
     * @Description 支持参数为非数组模式POST请求
     * @Version  1.0
     */
    public<T> T  get(String url, Class<T> responseType, Map<String, ?> uriVariables){
        ResponseEntity<T> entity = restTemplate.getForEntity(url, responseType, uriVariables);
        return entity.getBody();
    }
}
