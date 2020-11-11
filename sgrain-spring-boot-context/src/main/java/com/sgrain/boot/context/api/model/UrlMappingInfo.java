package com.sgrain.boot.context.api.model;

import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Set;

/**
 * @program: spring-parent
 * @description: 路径映射信息
 * @create: 2020/07/10
 */
public class UrlMappingInfo {
    /**
     * 请求方法
     */
    private Set<RequestMethod> method;
    /**
     * 控制器方法上的路由集
     */
    private Set<String> patterns;
    /**
     * bean名称
     */
    private Object bean;
    /**
     * 控制器方法描述
     */
    private String description;

    public Set<String> getPatterns() {
        return patterns;
    }

    public void setPatterns(Set<String> patterns) {
        this.patterns = patterns;
    }

    public Set<RequestMethod> getMethod() {
        return method;
    }

    public void setMethod(Set<RequestMethod> method) {
        this.method = method;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
