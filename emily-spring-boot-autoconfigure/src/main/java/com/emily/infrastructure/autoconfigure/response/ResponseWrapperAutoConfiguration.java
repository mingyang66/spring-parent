package com.emily.infrastructure.autoconfigure.response;

import com.emily.infrastructure.autoconfigure.response.handler.ResponseHttpEntityMethodReturnValueHandler;
import com.emily.infrastructure.autoconfigure.response.handler.ResponseHttpHeadersReturnValueHandler;
import com.emily.infrastructure.autoconfigure.response.handler.ResponseMethodReturnValueHandler;
import com.emily.infrastructure.autoconfigure.logger.common.LoggerUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.HttpHeadersReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 控制器返回值配置处理类
 * @Version: 1.0
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ResponseWrapperProperties.class)
@ConditionalOnProperty(prefix = "spring.emily.response.wrapper", name = "enable", havingValue = "true", matchIfMissing = true)
public class ResponseWrapperAutoConfiguration implements InitializingBean, DisposableBean {

    private RequestMappingHandlerAdapter handlerAdapter;
    private ResponseWrapperProperties returnValueProperties;

    public ResponseWrapperAutoConfiguration(RequestMappingHandlerAdapter handlerAdapter, ResponseWrapperProperties returnValueProperties) {
        this.handlerAdapter = handlerAdapter;
        this.returnValueProperties =returnValueProperties;
    }

    @Bean
    public void initCustomReturnValue() {
        List<HandlerMethodReturnValueHandler> handlers = handlerAdapter.getReturnValueHandlers().stream().map(valueHandler -> {
            if (valueHandler instanceof RequestResponseBodyMethodProcessor) {
                return new ResponseMethodReturnValueHandler(valueHandler, returnValueProperties);
            }
            if (valueHandler instanceof HttpEntityMethodProcessor) {
                return new ResponseHttpEntityMethodReturnValueHandler(valueHandler, returnValueProperties);
            }
            if (valueHandler instanceof HttpHeadersReturnValueHandler) {
                return new ResponseHttpHeadersReturnValueHandler(valueHandler);
            }
            return valueHandler;
        }).collect(Collectors.toList());

        handlerAdapter.setReturnValueHandlers(handlers);
    }

    @Override
    public void destroy() throws Exception {
        LoggerUtils.info(ResponseWrapperAutoConfiguration.class, "<== 【销毁--自动化配置】----Response返回值包装组件【ResponseWrapperAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerUtils.info(ResponseWrapperAutoConfiguration.class, "==> 【初始化--自动化配置】----Response返回值包装组件【ResponseWrapperAutoConfiguration】");
    }
}
