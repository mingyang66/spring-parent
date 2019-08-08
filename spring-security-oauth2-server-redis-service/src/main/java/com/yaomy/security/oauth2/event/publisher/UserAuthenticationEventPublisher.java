package com.yaomy.security.oauth2.event.publisher;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.event.publisher.UserAuthenticationEventPublisher
 * @Date: 2019/8/8 10:50
 * @Version: 1.0
 */
public class UserAuthenticationEventPublisher extends DefaultAuthenticationEventPublisher {

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publishAuthenticationFailure(AuthenticationException exception,
                                             Authentication authentication) {
        //this.applicationEventPublisher.publishEvent(authentication);
        System.out.println("---自定-------publishAuthenticationFailure---------");
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher){
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
