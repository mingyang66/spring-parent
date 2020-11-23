package com.sgrain.boot.actuator.filter;

import com.sgrain.boot.common.utils.log.LoggerUtils;
import org.springframework.boot.actuate.endpoint.EndpointFilter;
import org.springframework.boot.actuate.endpoint.web.ExposableWebEndpoint;
import org.springframework.stereotype.Component;

/**
 * @program: spring-parent
 * @description: 自定义端点策略
 * @create: 2020/08/13
 */
@Component
public class CustomEndpointFilter implements EndpointFilter<ExposableWebEndpoint> {

    @Override
    public boolean match(ExposableWebEndpoint endpoint) {
        LoggerUtils.info(CustomEndpointFilter.class, "端点ID："+endpoint.getEndpointId());
        return true;
    }
}
