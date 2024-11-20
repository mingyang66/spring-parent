package com.emily.infrastructure.sample.web.controller;

import com.emily.infrastructure.test.entity.UrlMappingInfo;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;
import java.util.Map;

/**
 * @author Emily
 * web应用程序之中映射关系控制器
 * @since 2020/07/09
 */
@RequestMapping("handler")
@RestController
public class HandlerMappingController {
    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @GetMapping("requestMappingInfo")
    public List<UrlMappingInfo> requestMappingInfo() {
        List<UrlMappingInfo> result = Lists.newArrayList();
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
        map.forEach((info, method) -> {
            UrlMappingInfo urlMappingInfo = new UrlMappingInfo();
            urlMappingInfo.setPatterns(info.getPathPatternsCondition().getPatternValues());
            urlMappingInfo.getPatterns();
            urlMappingInfo.setMethod(info.getMethodsCondition().getMethods());
            urlMappingInfo.setBean(method.getBean());
            urlMappingInfo.setDescription(method.toString());
            result.add(urlMappingInfo);
        });
        return result;
    }
}
