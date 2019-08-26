package com.yaomy.control.returnvalue.config;

import com.yaomy.control.returnvalue.handler.ResponseHttpEntityMethodReturnValueHandler;
import com.yaomy.control.returnvalue.handler.ResponseHttpHeadersReturnValueHandler;
import com.yaomy.control.returnvalue.handler.ResponseMethodReturnValueHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
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
public class ResponseMethodReturnValueConfigurer implements InitializingBean {

    @Autowired
    private RequestMappingHandlerAdapter handlerAdapter;

    @Override
    public void afterPropertiesSet()  {

        List<HandlerMethodReturnValueHandler> list = handlerAdapter.getReturnValueHandlers();
        if (null != list) {
            List<HandlerMethodReturnValueHandler> newList = new ArrayList<>();
            for (HandlerMethodReturnValueHandler valueHandler: list) {
                if (valueHandler instanceof RequestResponseBodyMethodProcessor) {
                    ResponseMethodReturnValueHandler proxy = new ResponseMethodReturnValueHandler(valueHandler);
                    newList.add(proxy);
                } else if(valueHandler instanceof HttpEntityMethodProcessor){
                    ResponseHttpEntityMethodReturnValueHandler proxy = new ResponseHttpEntityMethodReturnValueHandler(valueHandler);
                    newList.add(proxy);
                } else if(valueHandler instanceof HttpHeadersReturnValueHandler){
                    ResponseHttpHeadersReturnValueHandler proxy = new ResponseHttpHeadersReturnValueHandler(valueHandler);
                    newList.add(proxy);
                } else {
                    newList.add(valueHandler);
                }
            }
            handlerAdapter.setReturnValueHandlers(newList);
        }

    }
}
