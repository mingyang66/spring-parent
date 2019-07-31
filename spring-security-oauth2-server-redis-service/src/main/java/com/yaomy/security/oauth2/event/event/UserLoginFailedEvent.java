package com.yaomy.security.oauth2.event.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.security.core.Authentication;

/**
 * @Description: 定义用户登录失败事件
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.event.event.UserLoginFailedEvent
 * @Date: 2019/7/31 10:46
 * @Version: 1.0
 */
public class UserLoginFailedEvent extends ApplicationEvent {
    public UserLoginFailedEvent(Authentication authentication) {
        super(authentication);
    }
}
