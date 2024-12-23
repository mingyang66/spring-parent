package com.emily.infrastructure.web.response;

import org.slf4j.LoggerFactory;
import com.emily.infrastructure.web.response.interceptor.DefaultResponseAdviceInterceptor;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;

import java.util.HashSet;
import java.util.Set;

/**
 * 控制器返回值配置处理类
 *
 * @author Emily
 * @since 1.0
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@AutoConfiguration(after = WebMvcAutoConfiguration.class)
@EnableConfigurationProperties(ResponseProperties.class)
@ConditionalOnProperty(prefix = ResponseProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class ResponseAutoConfiguration implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(ResponseAutoConfiguration.class);
    /**
     * 默认排除路由地址
     */
    private static final Set<String> defaultExclude = new HashSet<>() {{
        add("^/swagger-resources.*$");
        add("/v2/api-docs");
        add("/swagger-ui.html");
        add("/error");
    }};

    /**
     * 基于ResponseBodyAdvice切面模式处理返回值包装类模式，默认：开启
     *
     * @param properties 属性配置
     * @return 请求响应AOP切面
     */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public DefaultResponseAdviceInterceptor defaultResponseAdviceInterceptor(ResponseProperties properties) {
        properties.getExclude().addAll(defaultExclude);
        return new DefaultResponseAdviceInterceptor(properties);
    }

    @Override
    public void destroy() throws Exception {
        logger.info("<== 【销毁--自动化配置】----返回值包装组件【ResponseAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("==> 【初始化--自动化配置】----返回值包装组件【ResponseAutoConfiguration】");
    }
}
