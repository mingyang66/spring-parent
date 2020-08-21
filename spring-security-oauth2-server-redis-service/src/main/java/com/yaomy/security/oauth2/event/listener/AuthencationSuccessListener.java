package com.yaomy.security.oauth2.event.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
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

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
            //用户通过输入用户名和密码登录成功
            System.out.println("---AuthenticationSuccessEvent---");
    }

}
