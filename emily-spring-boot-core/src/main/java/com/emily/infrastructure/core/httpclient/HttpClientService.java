package com.emily.infrastructure.core.httpclient;


import com.emily.infrastructure.common.constant.CharacterInfo;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * 发送Http请求Client服务类
 */
@SuppressWarnings("all")
@Deprecated
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
        ResponseEntity<T> entity = restTemplate.postForEntity(url, getHttpHeaders(params, headers), responseType);
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
        ResponseEntity<T> entity = restTemplate.postForEntity(url, getHttpHeaders(params, headers), responseType, uriVariables);
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
        ResponseEntity<T> entity = restTemplate.postForEntity(url, getHttpHeaders(params, headers), responseType, uriVariables);
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
        ResponseEntity<T> entity = restTemplate.getForEntity(url, responseType, uriVariables);
        return entity.getBody();
    }

    /**
     * 支持参数为非数组模式POST请求
     */
    public <T> T get(String url, Class<T> responseType, Map<String, ?> uriVariables) {
        ResponseEntity<T> entity = restTemplate.getForEntity(url, responseType, uriVariables);
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
        HttpEntity<?> requestEntity = getHttpHeaders(null, headers);
        ResponseEntity<T> entity = restTemplate.exchange(joinUrl(url, paramsMap), HttpMethod.GET, requestEntity, responseType);
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
        ResponseEntity<T> entity = restTemplate.exchange(url, HttpMethod.POST, getHttpHeaders(paramsMap, headers), responseType);
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
                    sb.append(CharacterInfo.AND_AIGN);
                }
                sb.append(StringUtils.join(key, CharacterInfo.EQUAL_SIGN, paramsMap.get(key)));
            });
            if (url.contains(CharacterInfo.ASK_SIGN_EN)) {
                url = StringUtils.join(url, CharacterInfo.AND_AIGN, sb.toString());
            } else {
                url = StringUtils.join(url, CharacterInfo.ASK_SIGN_EN, sb.toString());
            }
        }
        return url;
    }


}
