package com.yaomy.security.oauth2.event.listener;

import com.yaomy.security.oauth2.service.PropertyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * @Description: 用户登录成功监听器事件
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.handler.ApplicationListenerAuthencationSuccess
 * @Date: 2019/7/25 11:27
 * @Version: 1.0
 */
@Component
public class AuthencationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {
    @Autowired
    private PropertyService propertyService;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent authenticationSuccessEvent) {
        Authentication authentication = authenticationSuccessEvent.getAuthentication();
        if(!StringUtils.equalsIgnoreCase(authentication.getName(), propertyService.getProperty("spring.security.oauth.resource.client.id"))){
            System.out.println("------login success----------"+authentication.getName());
        }
    }
}
