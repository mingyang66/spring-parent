package com.sgrain.boot.autoconfigure.httpclient;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * @Description: 将RestTemplate加入容器
 * @Version: 1.0
 */
@Configuration
@EnableConfigurationProperties(HttpClientProperties.class)
@ConditionalOnProperty(prefix = "spring.sgrain.http-client", name = "enable", havingValue = "true", matchIfMissing = true)
public class HttpClientAutoConfiguration {
    /**
     * 读取配置属性服务类
     */
    private HttpClientProperties httpClientProperties;

    public HttpClientAutoConfiguration(HttpClientProperties httpClientProperties){
        this.httpClientProperties = httpClientProperties;
    }
    /**
     * 将RestTemplate加入容器，对异常处理进行处理，使异常也可以返回结果
     */
    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory){
        RestTemplate restTemplate = new RestTemplate(factory);
        ResponseErrorHandler responseErrorHandler = new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return true;
            }
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
            }
        };
        restTemplate.setErrorHandler(responseErrorHandler);
        return restTemplate;
    }
    /**
     * 定义HTTP请求工厂方法,设置超市时间
     */
    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        //读取超时5秒,默认无限限制,单位：毫秒
        factory.setReadTimeout(httpClientProperties.getReadTimeOut());
        //连接超时10秒，默认无限制，单位：毫秒
        factory.setConnectTimeout(httpClientProperties.getConnectTimeOut());
        return factory;
    }

}
