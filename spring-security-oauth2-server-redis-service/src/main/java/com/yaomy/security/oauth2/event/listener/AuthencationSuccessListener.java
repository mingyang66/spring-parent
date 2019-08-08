package com.yaomy.security.oauth2.event.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/**
 * @Description: 用户登录成功监听器事件
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.handler.ApplicationListenerAuthencationSuccess
 * @Date: 2019/7/25 11:27
 * @Version: 1.0
 */
@Component
public class AuthencationSuccessListener implements ApplicationListener<AbstractAuthenticationEvent> {

    @Override
    public void onApplicationEvent(AbstractAuthenticationEvent event) {
        if(event instanceof AuthenticationSuccessEvent){
            //用户通过输入用户名和密码登录成功
            System.out.println("---AuthenticationSuccessEvent---");
        } else if(event instanceof InteractiveAuthenticationSuccessEvent){
            //同样是登录成功，但表示通过自动交互的手段来登录成功，比如cookie自动登录
            System.out.println("---InteractiveAuthenticationSuccessEvent---");
        }
    }

}
