package com.emily.infrastructure.autoconfigure.http.client;

import com.emily.infrastructure.autoconfigure.http.HttpClientProperties;
import com.emily.infrastructure.logback.utils.LoggerUtils;
import com.emily.infrastructure.context.httpclient.handler.CustomResponseErrorHandler;
import com.emily.infrastructure.context.httpclient.interceptor.HttpClientInterceptor;
import com.emily.infrastructure.context.logger.LoggerService;
import com.emily.infrastructure.context.logger.impl.LoggerServiceImpl;
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
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.function.Supplier;

/**
 * @author Emily
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
     * 日志记录服务
     */
    @Bean
    @ConditionalOnMissingBean
    public LoggerService loggerService() {
        Supplier<LoggerService> supplier = LoggerServiceImpl::new;
        return supplier.get();
    }

    /**
     * 将RestTemplate加入容器，对异常处理进行处理，使异常也可以返回结果
     */
    @Primary
    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory clientHttpRequestFactory, LoggerService loggerService) {
        RestTemplate restTemplate = new RestTemplate();
        //设置BufferingClientHttpRequestFactory将输入流和输出流保存到内存中，允许多次读取
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(clientHttpRequestFactory));
        //设置自定义异常处理
        restTemplate.setErrorHandler(new CustomResponseErrorHandler());
        if (httpClientProperties.isEnableInterceptor()) {
            //添加拦截器
            restTemplate.setInterceptors(Collections.singletonList(new HttpClientInterceptor(loggerService)));
        }

        return restTemplate;
    }

    /**
     * 定义HTTP请求工厂方法,设置超市时间
     */
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        //SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        //读取超时5秒,默认无限限制,单位：毫秒
        factory.setReadTimeout(httpClientProperties.getReadTimeOut());
        //连接超时10秒，默认无限制，单位：毫秒
        factory.setConnectTimeout(httpClientProperties.getConnectTimeOut());
        return factory;
    }

    @Override
    public void destroy() {
        LoggerUtils.info(HttpClientAutoConfiguration.class, "<== 【销毁--自动化配置】----RestTemplate(HttpClient)组件【HttpClientAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() {
        LoggerUtils.info(HttpClientAutoConfiguration.class, "==> 【初始化--自动化配置】----RestTemplate(HttpClient)组件【HttpClientAutoConfiguration】");
    }
}
