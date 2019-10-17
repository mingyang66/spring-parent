package com.yaomy.control.rest.config;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
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
public class NetWorkConfig {
    /**
            * 读取数据超时时间配置属性
     */
    public static final String HTTP_CLIENT_READ_TIMEOUT = "spring.emis.network.readTimeOut";
    /**
     * 连接超时时间属性设置
     */
    public static final String HTTP_CLIENT_CONNECT_TIMEOUT = "spring.emis.network.connectTimeOut";
    /**
     * 读取配置属性服务类
     */
    @Autowired
    private Environment env;
    /**
     * 将RestTemplate加入容器
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
     * 定义HTTP请求工厂方法
     */
    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        //读取超时5秒,默认无限限制,单位：毫秒
        factory.setReadTimeout(NumberUtils.toInt(env.getProperty(HTTP_CLIENT_READ_TIMEOUT), 5000));
        //连接超时10秒，默认无限制，单位：毫秒
        factory.setConnectTimeout(NumberUtils.toInt(env.getProperty(HTTP_CLIENT_CONNECT_TIMEOUT), 10000));
        return factory;
    }

}
