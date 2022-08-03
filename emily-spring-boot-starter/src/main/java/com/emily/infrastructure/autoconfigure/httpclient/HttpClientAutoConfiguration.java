package com.emily.infrastructure.autoconfigure.httpclient;

import com.emily.infrastructure.autoconfigure.httpclient.annotation.TargetHttpTimeout;
import com.emily.infrastructure.autoconfigure.httpclient.factory.HttpContextFactory;
import com.emily.infrastructure.autoconfigure.httpclient.handler.CustomResponseErrorHandler;
import com.emily.infrastructure.autoconfigure.httpclient.interceptor.client.DefaultHttpClientInterceptor;
import com.emily.infrastructure.autoconfigure.httpclient.interceptor.client.HttpClientCustomizer;
import com.emily.infrastructure.autoconfigure.httpclient.interceptor.timeout.HttpTimeoutMethodInterceptor;
import com.emily.infrastructure.common.constant.AopOrderInfo;
import com.emily.infrastructure.core.aop.advisor.AnnotationPointcutAdvisor;
import com.emily.infrastructure.logger.LoggerFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Role;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;

/**
 * @author Emily
 * @Description: 将RestTemplate加入容器
 * @Version: 1.0
 */
@AutoConfiguration
@ConditionalOnClass(RestTemplate.class)
@EnableConfigurationProperties(HttpClientProperties.class)
@ConditionalOnProperty(prefix = HttpClientProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class HttpClientAutoConfiguration implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientAutoConfiguration.class);

    /**
     * 将RestTemplate加入容器，对异常处理进行处理，使异常也可以返回结果
     */
    @Primary
    @Bean
    public RestTemplate restTemplate(ObjectProvider<HttpClientCustomizer> httpClientCustomizers, ClientHttpRequestFactory clientHttpRequestFactory, HttpClientProperties httpClientProperties) {
        RestTemplate restTemplate = new RestTemplate();
        //设置BufferingClientHttpRequestFactory将输入流和输出流保存到内存中，允许多次读取
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(clientHttpRequestFactory));
        //设置自定义异常处理
        restTemplate.setErrorHandler(new CustomResponseErrorHandler());
        if (httpClientProperties.isInterceptor()) {
            //添加拦截器
            restTemplate.setInterceptors(Collections.singletonList(httpClientCustomizers.orderedStream().findFirst().get()));
        }

        return restTemplate;
    }

    /**
     * 定义HTTP请求工厂方法,设置超市时间
     */
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(HttpClientProperties properties) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        //SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        //读取超时5秒,默认无限限制,单位：毫秒
        factory.setReadTimeout(properties.getReadTimeOut());
        //连接超时10秒，默认无限制，单位：毫秒
        factory.setConnectTimeout(properties.getConnectTimeOut());
        //设置HTTP进程执行状态工厂类
        factory.setHttpContextFactory(new HttpContextFactory());
        //开启HTTPS请求支持
        if (properties.isSsl()) {
            TrustStrategy acceptingTrustStrategy = (x509Certificates, authType) -> true;
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
            SSLConnectionSocketFactory connectionSocketFactory = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());

            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(connectionSocketFactory).build();
            factory.setHttpClient(httpClient);
        }
        return factory;
    }

    @Bean
    public DefaultHttpClientInterceptor httpClientInterceptor() {
        return new DefaultHttpClientInterceptor();
    }

    /**
     * RestTemplate请求超时切面（单个请求）
     */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Advisor httpTimeoutPointCutAdvice() {
        //限定方法级别的切点
        Pointcut mpc = new AnnotationMatchingPointcut(null, TargetHttpTimeout.class, false);
        //组合切面(并集)，即只要有一个切点的条件符合，则就拦截
        Pointcut pointcut = new ComposablePointcut(mpc);
        //切面增强类
        AnnotationPointcutAdvisor advisor = new AnnotationPointcutAdvisor(new HttpTimeoutMethodInterceptor(), pointcut);
        //切面优先级顺序
        advisor.setOrder(AopOrderInfo.HTTP_CLIENT_INTERCEPTOR);
        return advisor;
    }

    @Override
    public void destroy() {
        logger.info("<== 【销毁--自动化配置】----RestTemplate(HttpClient)组件【HttpClientAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() {
        logger.info("==> 【初始化--自动化配置】----RestTemplate(HttpClient)组件【HttpClientAutoConfiguration】");
    }
}
