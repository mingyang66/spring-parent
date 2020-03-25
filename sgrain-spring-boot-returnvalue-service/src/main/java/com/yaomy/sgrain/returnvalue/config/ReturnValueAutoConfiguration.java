package com.yaomy.sgrain.returnvalue.config;

import com.yaomy.sgrain.returnvalue.handler.ResponseHttpEntityMethodReturnValueHandler;
import com.yaomy.sgrain.returnvalue.handler.ResponseHttpHeadersReturnValueHandler;
import com.yaomy.sgrain.returnvalue.handler.ResponseMethodReturnValueHandler;
import com.yaomy.sgrain.returnvalue.properties.ReturnValueProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.HttpHeadersReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 控制器返回值配置处理类
 * @Version: 1.0
 */
@Configuration
@ConditionalOnClass({ResponseHttpEntityMethodReturnValueHandler.class, ResponseMethodReturnValueHandler.class, ResponseHttpEntityMethodReturnValueHandler.class})
@EnableConfigurationProperties(ReturnValueProperties.class)
@ConditionalOnProperty(prefix = "spring.sgrain.return-value", name = "enable", havingValue = "true", matchIfMissing = true)
public class ReturnValueAutoConfiguration implements InitializingBean {

    private RequestMappingHandlerAdapter handlerAdapter;

    public ReturnValueAutoConfiguration(RequestMappingHandlerAdapter handlerAdapter){
        this.handlerAdapter = handlerAdapter;
    }
    @Override
    public void afterPropertiesSet()  {

        List<HandlerMethodReturnValueHandler> list = handlerAdapter.getReturnValueHandlers();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<HandlerMethodReturnValueHandler> pList = new ArrayList<>();
        for (HandlerMethodReturnValueHandler valueHandler: list) {
            if (valueHandler instanceof RequestResponseBodyMethodProcessor) {
                ResponseMethodReturnValueHandler proxy = new ResponseMethodReturnValueHandler(valueHandler);
                pList.add(proxy);
            } else if(valueHandler instanceof HttpEntityMethodProcessor){
                ResponseHttpEntityMethodReturnValueHandler proxy = new ResponseHttpEntityMethodReturnValueHandler(valueHandler);
                pList.add(proxy);
            } else if(valueHandler instanceof HttpHeadersReturnValueHandler){
                ResponseHttpHeadersReturnValueHandler proxy = new ResponseHttpHeadersReturnValueHandler(valueHandler);
                pList.add(proxy);
            } else {
                pList.add(valueHandler);
            }
        }
        handlerAdapter.setReturnValueHandlers(pList);

    }
}
