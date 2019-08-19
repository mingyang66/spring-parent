package com.yaomy.handler.config;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Parameter;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Version: 1.0
 */
public class RequestMethodArgumentsHander implements HandlerMethodArgumentResolver {
    private HandlerMethodArgumentResolver handlerMethodArgumentResolver;

    public RequestMethodArgumentsHander(HandlerMethodArgumentResolver handlerMethodArgumentResolver){
        this.handlerMethodArgumentResolver = handlerMethodArgumentResolver;
    }
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return true;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        System.out.println(parameter.getParameter()+"--"+parameter.getParameterName()+"--"+parameter.getMethod()+"--"+mavContainer.getStatus());
        Parameter parameter1 = parameter.getParameter();
        System.out.println(parameter1.getName());
        System.out.println(parameter1.getModifiers());
        System.out.println(parameter1.getType());
        System.out.println(webRequest.getParameter(parameter1.getName()));
        return "";
    }
}
