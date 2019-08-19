package com.yaomy.handler.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: Description
 * @ProjectName: EM.FrontEnd.PrivateEquity.electronic-contract
 * @Package: com.uufund.ecapi.config.returnvalue.RestReturnValueHandlerConfigurer
 * @Author: 姚明洋
 * @Date: 2019/5/21 14:21
 * @Version: 1.0
 */
@Configuration
public class ResponseConfigurer implements InitializingBean {
    @Autowired
    private RequestMappingHandlerAdapter handlerAdapter;

    @Override
    public void afterPropertiesSet()  {
       /* List<HandlerMethodArgumentResolver> argsList = handlerAdapter.getArgumentResolvers();
        if(null != argsList){
            List<HandlerMethodArgumentResolver> newList = new ArrayList<>();
            for(HandlerMethodArgumentResolver argumentResolver:argsList){
                *//*if(argumentResolver instanceof RequestParamMethodArgumentResolver){
                    newList.add(new RequestArgumentsHander(argumentResolver));
                } else {*//*
                    newList.add(argumentResolver);
            *//*    }*//*
            }
            handlerAdapter.setArgumentResolvers(newList);
        }*/

        List<HandlerMethodReturnValueHandler> list = handlerAdapter.getReturnValueHandlers();
        if (null != list) {
            List<HandlerMethodReturnValueHandler> newList = new ArrayList<>();
            for (HandlerMethodReturnValueHandler valueHandler: list) {
                if (valueHandler instanceof RequestResponseBodyMethodProcessor) {
                    ResponseMethodReturnValueHandler proxy = new ResponseMethodReturnValueHandler(valueHandler);
                    newList.add(proxy);
                } else {
                    newList.add(valueHandler);
                }
            }
            handlerAdapter.setReturnValueHandlers(newList);
        }

    }
}
