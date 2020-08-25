package com.sgrain.boot.autoconfigure.returnvalue;

import com.sgrain.boot.autoconfigure.returnvalue.handler.ResponseHttpEntityMethodReturnValueHandler;
import com.sgrain.boot.autoconfigure.returnvalue.handler.ResponseHttpHeadersReturnValueHandler;
import com.sgrain.boot.autoconfigure.returnvalue.handler.ResponseMethodReturnValueHandler;
import com.sgrain.boot.autoconfigure.threadpool.AsyncThreadPoolAutoConfiguration;
import com.sgrain.boot.common.utils.LoggerUtils;
import org.springframework.boot.CommandLineRunner;
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
@Configuration
@EnableConfigurationProperties(ReturnValueProperties.class)
@ConditionalOnProperty(prefix = "spring.sgrain.return-value", name = "enable", havingValue = "true", matchIfMissing = true)
public class ReturnValueAutoConfiguration implements CommandLineRunner {

    private RequestMappingHandlerAdapter handlerAdapter;

    public ReturnValueAutoConfiguration(RequestMappingHandlerAdapter handlerAdapter) {
        this.handlerAdapter = handlerAdapter;
    }

    @Bean
    public void initCustomReturnValue() {
        List<HandlerMethodReturnValueHandler> handlers = handlerAdapter.getReturnValueHandlers().stream().map(valueHandler -> {
            if (valueHandler instanceof RequestResponseBodyMethodProcessor) {
                return new ResponseMethodReturnValueHandler(valueHandler);
            }
            if (valueHandler instanceof HttpEntityMethodProcessor) {
                return new ResponseHttpEntityMethodReturnValueHandler(valueHandler);
            }
            if (valueHandler instanceof HttpHeadersReturnValueHandler) {
                return new ResponseHttpHeadersReturnValueHandler(valueHandler);
            }
            return valueHandler;
        }).collect(Collectors.toList());

        handlerAdapter.setReturnValueHandlers(handlers);
    }

    @Override
    public void run(String... args) throws Exception {
        LoggerUtils.info(ReturnValueAutoConfiguration.class, "自动化配置----返回值包装组件初始化完成...");
    }
}
