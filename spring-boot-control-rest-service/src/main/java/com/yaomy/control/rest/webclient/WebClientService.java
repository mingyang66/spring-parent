package com.yaomy.control.rest.webclient;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @Description: WebClient客户端服务类
 * @ProjectName: spring-parent
 * @Version: 1.0
 */
public class WebClientService {
    private static WebClient client = WebClient.create();
    /**
     * @Description 支持参数为非数组模式POST请求
     * @Version  1.0
     */
    public <T> T  post(String url, Map<String, Object> params, Class<T> responseType){
        Mono<T> entity = client.post().syncBody(params).retrieve().bodyToMono(responseType);
        return entity.block();
    }
    /**
     * @Description 支持文件上传、多参数、参数为数组模式、参数为非数组模式
     * @Version  1.0
     */
    public <T> T postMulti(String url, MultipartBodyBuilder params, Class<T> responseType){
        Mono<T> entity = client.post().syncBody(params).retrieve().bodyToMono(responseType);
        return entity.block();
    }
}
