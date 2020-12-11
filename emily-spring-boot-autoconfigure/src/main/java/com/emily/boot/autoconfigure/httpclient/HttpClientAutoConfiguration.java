package com.emily.boot.autoconfigure.httpclient;

import com.emily.boot.common.utils.log.LoggerUtils;
import com.emily.boot.context.httpclient.handler.CustomResponseErrorHandler;
import com.emily.boot.context.httpclient.interceptor.HttpClientInterceptor;
import com.emily.boot.context.httpclient.service.AsyncLogHttpClientService;
import com.emily.boot.context.httpclient.service.impl.AsyncLogHttpClientServiceImpl;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
@EnableConfigurationProperties(HttpClientProperties.class)
@ConditionalOnClass(RestTemplate.class)
@ConditionalOnProperty(prefix = "spring.emily.http-client", name = "enable", havingValue = "true", matchIfMissing = true)
public class HttpClientAutoConfiguration implements InitializingBean, DisposableBean {
    /**
     * 读取配置属性服务类
     */
    @Autowired
    private HttpClientProperties httpClientProperties;

    /**
     * 创建日志异步调用服务
     */
    @Bean
    @ConditionalOnMissingBean
    public AsyncLogHttpClientService asyncLogHttpClientService() {
        return new AsyncLogHttpClientServiceImpl();
    }

    /**
     * 将RestTemplate加入容器，对异常处理进行处理，使异常也可以返回结果
     */
    @Primary
    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory clientHttpRequestFactory, AsyncLogHttpClientService asyncLogHttpClientService) {
        RestTemplate restTemplate = new RestTemplate();
        //设置BufferingClientHttpRequestFactory将输入流和输出流保存到内存中，允许多次读取
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(clientHttpRequestFactory));
        //设置自定义异常处理
        restTemplate.setErrorHandler(new CustomResponseErrorHandler());
        if (httpClientProperties.isEnableInterceptor()) {
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
        //HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        //读取超时5秒,默认无限限制,单位：毫秒
        factory.setReadTimeout(httpClientProperties.getReadTimeOut());
        //连接超时10秒，默认无限制，单位：毫秒
        factory.setConnectTimeout(httpClientProperties.getConnectTimeOut());
        return factory;
    }

    @Override
    public void destroy() throws Exception {
        LoggerUtils.info(HttpClientAutoConfiguration.class, "【销毁--自动化配置】----RestTemplate(HttpClient)组件【HttpClientAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerUtils.info(HttpClientAutoConfiguration.class, "【初始化--自动化配置】----RestTemplate(HttpClient)组件【HttpClientAutoConfiguration】");
    }
}
