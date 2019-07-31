package com.yaomy.security.oauth2.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.handler.ApplicationListenerAuthencationSuccess
 * @Author: 姚明洋
 * @Date: 2019/7/25 11:27
 * @Version: 1.0
 */
@Component
public class AuthencationFailedListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {
    @Override
    public void onApplicationEvent(InteractiveAuthenticationSuccessEvent interactiveAuthenticationSuccessEvent) {
        System.out.println("-------interactiveAuthenticationSuccessEvent------"+interactiveAuthenticationSuccessEvent);
    }
}
