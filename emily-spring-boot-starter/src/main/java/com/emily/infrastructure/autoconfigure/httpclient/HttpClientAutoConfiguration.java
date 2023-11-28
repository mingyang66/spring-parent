package com.emily.infrastructure.autoconfigure.httpclient;

import com.emily.infrastructure.autoconfigure.httpclient.handler.CustomResponseErrorHandler;
import com.emily.infrastructure.autoconfigure.httpclient.interceptor.client.DefaultHttpClientInterceptor;
import com.emily.infrastructure.autoconfigure.httpclient.interceptor.client.HttpClientCustomizer;
import com.emily.infrastructure.logger.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * 将RestTemplate加入容器
 *
 * @author Emily
 * @see <a href="https://spring.io/blog/2023/06/07/securing-spring-boot-applications-with-ssl">官方文档案例</a>
 * @see <a href="https://github.com/scottfrederick/spring-boot-ssl-demo/tree/main">案例代码</a>
 * @see <a href="https://www.baeldung.com/spring-boot-security-ssl-bundles">证书生成</a>
 * @see <a href="https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#features.ssl">SSL官方文档</a>
 * @since 1.0
 */
@AutoConfiguration
@ConditionalOnClass(RestTemplate.class)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@EnableConfigurationProperties(HttpClientProperties.class)
@ConditionalOnProperty(prefix = HttpClientProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class HttpClientAutoConfiguration implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientAutoConfiguration.class);

    /**
     * 将RestTemplate加入容器，对异常处理进行处理，使异常也可以返回结果
     *
     * @param httpClientCustomizers 扩展点
     * @param properties            属性配置
     * @return http请求对象
     */
    //@Primary
    //@Bean(name = "restTemplate")
    //@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public RestTemplate restTemplate(ObjectProvider<HttpClientCustomizer> httpClientCustomizers, SslBundles sslBundles, HttpClientProperties properties) {
        RestTemplateBuilder builder = new RestTemplateBuilder()
                .setReadTimeout(properties.getReadTimeOut())
                .setConnectTimeout(properties.getConnectTimeOut())
                .detectRequestFactory(true)
                .errorHandler(new CustomResponseErrorHandler());
        if (properties.isInterceptor()) {
            builder = builder.interceptors(Collections.singletonList(httpClientCustomizers.orderedStream().findFirst().get()));
        }
        //开启HTTPS请求支持
        if (properties.isSsl()) {
            //builder = builder.setSslBundle(sslBundles.getBundle("client"));
        }
        RestTemplate restTemplate = builder.build();
        //设置BufferingClientHttpRequestFactory将输入流和输出流保存到内存中，允许多次读取
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(builder.buildRequestFactory()));
        return restTemplate;
    }


    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean
    public DefaultHttpClientInterceptor httpClientInterceptor() {
        return new DefaultHttpClientInterceptor();
    }

    /**
     * RestTemplate请求超时切面（单个请求）
     *
     * @param httpTimeoutCustomizers 扩展点方法
     * @return 切面对象
     */
 /*   @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Advisor httpTimeoutPointCutAdvice(ObjectProvider<HttpTimeoutCustomizer> httpTimeoutCustomizers) {
        //限定方法级别的切点
        Pointcut mpc = new AnnotationMatchingPointcut(null, TargetHttpTimeout.class, false);
        //组合切面(并集)，即只要有一个切点的条件符合，则就拦截
        Pointcut pointcut = new ComposablePointcut(mpc);
        //切面增强类
        AnnotationPointcutAdvisor advisor = new AnnotationPointcutAdvisor(httpTimeoutCustomizers.orderedStream().findFirst().get(), pointcut);
        //切面优先级顺序
        advisor.setOrder(AopOrderInfo.HTTP_CLIENT);
        return advisor;
    }*/

/*    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean
    public HttpTimeoutCustomizer httpTimeoutCustomizer() {
        return new DefaultHttpTimeoutMethodInterceptor();
    }*/
    @Override
    public void destroy() {
        logger.info("<== 【销毁--自动化配置】----RestTemplate(HttpClient)组件【HttpClientAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() {
        logger.info("==> 【初始化--自动化配置】----RestTemplate(HttpClient)组件【HttpClientAutoConfiguration】");
    }
}
