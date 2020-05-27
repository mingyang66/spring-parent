package com.sgrain.boot.autoconfigure.returnvalue;

import com.google.common.collect.Lists;
import com.sgrain.boot.autoconfigure.returnvalue.handler.ResponseHttpEntityMethodReturnValueHandler;
import com.sgrain.boot.autoconfigure.returnvalue.handler.ResponseHttpHeadersReturnValueHandler;
import com.sgrain.boot.autoconfigure.returnvalue.handler.ResponseMethodReturnValueHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.HttpHeadersReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.List;

/**
 * @Description: 控制器返回值配置处理类
 * @Version: 1.0
 */
@Configuration
@EnableConfigurationProperties(ReturnValueProperties.class)
@ConditionalOnProperty(prefix = "spring.sgrain.return-value", name = "enable", havingValue = "true", matchIfMissing = true)
public class ReturnValueAutoConfiguration {

    private RequestMappingHandlerAdapter handlerAdapter;

    public ReturnValueAutoConfiguration(RequestMappingHandlerAdapter handlerAdapter) {
        this.handlerAdapter = handlerAdapter;
    }

    @Bean
    public void initCustomReturnValue() {
        List<HandlerMethodReturnValueHandler> list = handlerAdapter.getReturnValueHandlers();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<HandlerMethodReturnValueHandler> handlers = Lists.newArrayList();
        list.forEach((valueHandler) -> {
            if (valueHandler instanceof RequestResponseBodyMethodProcessor) {
                handlers.add(new ResponseMethodReturnValueHandler(valueHandler));
            } else if (valueHandler instanceof HttpEntityMethodProcessor) {
                handlers.add(new ResponseHttpEntityMethodReturnValueHandler(valueHandler));
            } else if (valueHandler instanceof HttpHeadersReturnValueHandler) {
                handlers.add(new ResponseHttpHeadersReturnValueHandler(valueHandler));
            } else {
                handlers.add(valueHandler);
            }
        });
        handlerAdapter.setReturnValueHandlers(handlers);
    }
}
