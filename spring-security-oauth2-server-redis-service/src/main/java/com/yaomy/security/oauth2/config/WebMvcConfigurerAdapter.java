package com.yaomy.security.oauth2.config;

import com.yaomy.security.oauth2.interceptor.Oauth2Interceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.config.WebMvcConfigurerAdapter
 * @Author: 姚明洋
 * @Date: 2019/7/24 15:17
 * @Version: 1.0
 */
public class WebMvcConfigurerAdapter implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new Oauth2Interceptor())
                //添加Oauth2Interceptor，除了/oauth2/**下的接口都需要进行 AccessToken 的校验
                .excludePathPatterns("/oauth/**");
    }
}
