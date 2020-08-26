package com.sgrain.boot.autoconfigure.httpclient;

import com.sgrain.boot.autoconfigure.httpclient.handler.CustomResponseErrorHandler;
import com.sgrain.boot.autoconfigure.httpclient.interceptor.HttpClientInterceptor;
import com.sgrain.boot.autoconfigure.httpclient.service.AsyncLogHttpClientService;
import com.sgrain.boot.autoconfigure.returnvalue.ReturnValueAutoConfiguration;
import com.sgrain.boot.common.utils.LoggerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * @Description: 将RestTemplate加入容器
 * @Version: 1.0
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(
        value = {HttpClientProperties.class}
)
@ConditionalOnClass(
        value = {RestTemplate.class}
)
@ConditionalOnProperty(prefix = "spring.sgrain.http-client", name = "enable", havingValue = "true", matchIfMissing = true)
public class HttpClientAutoConfiguration implements CommandLineRunner {
    /**
     * 读取配置属性服务类
     */
    @Autowired
    private HttpClientProperties httpClientProperties;
    @Autowired
    private AsyncLogHttpClientService asyncLogHttpClientService;


    /**
     * 将RestTemplate加入容器，对异常处理进行处理，使异常也可以返回结果
     */
    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory clientHttpRequestFactory) {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
        //设置BufferingClientHttpRequestFactory将输入流和输出流保存到内存中，允许多次读取
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(clientHttpRequestFactory));
        //设置自定义异常处理
        restTemplate.setErrorHandler(new CustomResponseErrorHandler());
        if(httpClientProperties.isEnableInterceptor()){
            //添加拦截器
            restTemplate.setInterceptors(Collections.singletonList(new HttpClientInterceptor(asyncLogHttpClientService)));
        }

        return restTemplate;
    }

    /**
     * 定义HTTP请求工厂方法,设置超市时间
     */
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        //读取超时5秒,默认无限限制,单位：毫秒
        factory.setReadTimeout(httpClientProperties.getReadTimeOut());
        //连接超时10秒，默认无限制，单位：毫秒
        factory.setConnectTimeout(httpClientProperties.getConnectTimeOut());
        return factory;
    }

    @Override
    public void run(String... args) throws Exception {
        LoggerUtils.info(HttpClientAutoConfiguration.class, "自动化配置----RestTemplate组件初始化完成...");
    }
}
