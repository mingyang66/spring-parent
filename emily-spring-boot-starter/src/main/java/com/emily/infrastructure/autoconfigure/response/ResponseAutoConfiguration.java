package com.emily.infrastructure.autoconfigure.response;

import com.emily.infrastructure.autoconfigure.response.handler.ResponseHttpEntityMethodReturnValueHandler;
import com.emily.infrastructure.autoconfigure.response.handler.ResponseHttpHeadersReturnValueHandler;
import com.emily.infrastructure.autoconfigure.response.handler.ResponseMethodReturnValueHandler;
import com.emily.infrastructure.autoconfigure.response.handler.ResponseWrapperAdviceHandler;
import com.emily.infrastructure.logger.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.HttpHeadersReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 控制器返回值配置处理类
 *
 * @author Emily
 * @since 1.0
 */
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
     * 基于适配器模式处理返回值包装类模式，默认：关闭
     *
     * @param handlerAdapter 适配器对象
     * @param properties     属性配置
     * @return 自定义字符串对象
     */
    @Bean
    @ConditionalOnProperty(prefix = ResponseProperties.PREFIX, name = "enabled-adapter", havingValue = "true")
    public Object initResponseWrapper(RequestMappingHandlerAdapter handlerAdapter, ResponseProperties properties) {
        properties.getExclude().addAll(defaultExclude);
        List<HandlerMethodReturnValueHandler> handlers = handlerAdapter.getReturnValueHandlers().stream().map(valueHandler -> {
            if (valueHandler instanceof RequestResponseBodyMethodProcessor) {
                return new ResponseMethodReturnValueHandler(valueHandler, properties);
            }
            if (valueHandler instanceof HttpEntityMethodProcessor) {
                return new ResponseHttpEntityMethodReturnValueHandler(valueHandler, properties);
            }
            if (valueHandler instanceof HttpHeadersReturnValueHandler) {
                return new ResponseHttpHeadersReturnValueHandler(valueHandler);
            }
            return valueHandler;
        }).collect(Collectors.toList());

        handlerAdapter.setReturnValueHandlers(handlers);
        return "UNSET";
    }

    /**
     * 基于ResponseBodyAdvice切面模式处理返回值包装类模式，默认：开启
     *
     * @param properties 属性配置
     * @return 请求响应AOP切面
     */
    @Bean
    @ConditionalOnProperty(prefix = ResponseProperties.PREFIX, name = "enabled-advice", havingValue = "true", matchIfMissing = true)
    public ResponseWrapperAdviceHandler responseWrapperAdviceHandler(ResponseProperties properties) {
        properties.getExclude().addAll(defaultExclude);
        return new ResponseWrapperAdviceHandler(properties);
    }

    @Override
    public void destroy() throws Exception {
        logger.info("<== 【销毁--自动化配置】----Response返回值包装组件【ResponseWrapperAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("==> 【初始化--自动化配置】----Response返回值包装组件【ResponseWrapperAutoConfiguration】");
    }
}
