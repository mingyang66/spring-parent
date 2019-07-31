package com.yaomy.security.oauth2.filter;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.filter.IntegrationAuthenticationFilter
 * @Author: 姚明洋
 * @Date: 2019/7/30 16:33
 * @Version: 1.0
 */
@Component
public class IntegrationAuthenticationFilter extends GenericFilterBean implements ApplicationContextAware {
    private static final String AUTH_TYPE_PARM_NAME = "auth_type";

    private static final String OAUTH_TOKEN_URL = "/oauth/token";

    private ApplicationContext applicationContext;

    private RequestMatcher requestMatcher;

    public IntegrationAuthenticationFilter(){
        this.requestMatcher = new OrRequestMatcher(
                new AntPathRequestMatcher(OAUTH_TOKEN_URL, "GET"),
                new AntPathRequestMatcher(OAUTH_TOKEN_URL, "POST")
        );
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(request, response);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
