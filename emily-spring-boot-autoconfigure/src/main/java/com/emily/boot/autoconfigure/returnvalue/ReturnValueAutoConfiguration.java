package com.emily.boot.autoconfigure.returnvalue;

import com.emily.boot.autoconfigure.returnvalue.handler.ResponseHttpEntityMethodReturnValueHandler;
import com.emily.boot.autoconfigure.returnvalue.handler.ResponseHttpHeadersReturnValueHandler;
import com.emily.boot.autoconfigure.returnvalue.handler.ResponseMethodReturnValueHandler;
import com.emily.boot.common.utils.log.LoggerUtils;
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
@EnableConfigurationProperties(ReturnValueProperties.class)
@ConditionalOnProperty(prefix = "spring.emily.return-value", name = "enable", havingValue = "true", matchIfMissing = true)
public class ReturnValueAutoConfiguration implements InitializingBean, DisposableBean {

    private RequestMappingHandlerAdapter handlerAdapter;
    private ReturnValueProperties returnValueProperties;

    public ReturnValueAutoConfiguration(RequestMappingHandlerAdapter handlerAdapter, ReturnValueProperties returnValueProperties) {
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
        LoggerUtils.info(ReturnValueAutoConfiguration.class, "【销毁--自动化配置】----返回值包装组件【ReturnValueAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerUtils.info(ReturnValueAutoConfiguration.class, "【初始化--自动化配置】----返回值包装组件【ReturnValueAutoConfiguration】");
    }
}
