package com.yaomy.sgrain.aop.properties;

import com.yaomy.sgrain.aop.po.Interceptor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @program: spring-parent
 * @description: 拦截器属性配置类
 * @author: 姚明洋
 * @create: 2020/03/19
 */
@ConfigurationProperties(prefix = "spring.yaomy.sgrain")
public class InterceptorProperties {

    private Interceptor interceptor = new Interceptor();

    public Interceptor getInterceptor() {
        return interceptor;
    }

    public void setInterceptor(Interceptor interceptor) {
        this.interceptor = interceptor;
    }
}
