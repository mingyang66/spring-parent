package com.yaomy.security.oauth2.handler;

import com.yaomy.common.enums.HttpStatusMsg;
import com.yaomy.common.po.BaseResponse;
import com.yaomy.common.utils.HttpUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
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
public class ApplicationListenerAuthencationSuccess implements ApplicationListener<AuthenticationSuccessEvent> {
    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent authenticationSuccessEvent) {
        System.out.println("------ApplicationListenerAuthencationSuccess----------");
        System.out.println(authenticationSuccessEvent.getAuthentication().getDetails());
        System.out.println(authenticationSuccessEvent.getSource());
    }
}
