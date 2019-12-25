package com.yaomy.control.rest.config;

import com.yaomy.control.common.control.conf.MetaDataProperties;
import org.springframework.beans.factory.annotation.Autowired;
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
public class NetWorkConfig {
    /**
     * 读取配置属性服务类
     */
    @Autowired
    private MetaDataProperties metaDataProperties;


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
        factory.setReadTimeout(metaDataProperties.getHttpClient().getReadTimeOut());
        //连接超时10秒，默认无限制，单位：毫秒
        factory.setConnectTimeout(metaDataProperties.getHttpClient().getConnectTimeOut());
        return factory;
    }

}
