package com.yaomy.security.oauth2.event.listener;

import com.yaomy.security.oauth2.event.event.UserLoginFailedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/**
 * @Description: 用户登录失败监听器
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.handler.ApplicationListenerAuthencationSuccess
 * @Author: 姚明洋
 * @Date: 2019/7/25 11:27
 * @Version: 1.0
 */
@Component
public class UserLoginFailedListener implements ApplicationListener<UserLoginFailedEvent> {
    @Override
    public void onApplicationEvent(UserLoginFailedEvent event) {
        System.out.println("----用户验证信息---faile----------------------");
    }
}
