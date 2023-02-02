package com.emily.infrastructure.autoconfigure.handler;

import com.emily.infrastructure.autoconfigure.handler.mapping.LookupPathRequestMappingHandlerMapping;
import com.emily.infrastructure.autoconfigure.handler.mapping.LookupPathCustomizer;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Role;
import org.springframework.core.Ordered;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

/**
 * @Description : 自定义RequestMappingHandlerMapping对象，根据请求路由做不同的切换
 * 优先级高于org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
 * @Author :  Emily
 * @CreateDate :  Created in 2023/2/2 10:23 上午
 */
@AutoConfiguration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 9)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@EnableConfigurationProperties(RequestMappingProperties.class)
@ConditionalOnProperty(prefix = RequestMappingProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class RequestMappingAutoConfiguration extends DelegatingWebMvcConfiguration {

    private LookupPathCustomizer requestMappingCustomizer;

    public RequestMappingAutoConfiguration(LookupPathCustomizer requestMappingCustomizer) {
        this.requestMappingCustomizer = requestMappingCustomizer;
    }

    /**
     * 初始化RequestMappingHandlerMapping
     *
     * @param contentNegotiationManager
     * @param conversionService
     * @param resourceUrlProvider
     * @return
     */
    @Bean
    @Primary
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @Override
    public RequestMappingHandlerMapping requestMappingHandlerMapping(ContentNegotiationManager contentNegotiationManager, FormattingConversionService conversionService, ResourceUrlProvider resourceUrlProvider) {
        return super.requestMappingHandlerMapping(contentNegotiationManager, conversionService, resourceUrlProvider);
    }

    /**
     * 使用自定义RequestMappingHandlerMapping初始化
     *
     * @return
     */
    @Override
    protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
        return new LookupPathRequestMappingHandlerMapping(requestMappingCustomizer);
    }
}
