package com.yaomy.security.oauth2.event.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.*;
import org.springframework.stereotype.Component;

/**
 * @Description: 用户登录成功监听器事件
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.handler.ApplicationListenerAuthencationSuccess
 * @Date: 2019/7/25 11:27
 * @Version: 1.0
 */
@Component
public class AuthencationFailureListener implements ApplicationListener<AbstractAuthenticationFailureEvent> {
    @Override
    public void onApplicationEvent(AbstractAuthenticationFailureEvent event) {
        if(event instanceof AuthenticationFailureBadCredentialsEvent){
            //提供的凭据是错误的，用户名或者密码错误
            System.out.println("---AuthenticationFailureBadCredentialsEvent---");
        } else if(event instanceof AuthenticationFailureCredentialsExpiredEvent){
            //验证通过，但是密码过期
            System.out.println("---AuthenticationFailureCredentialsExpiredEvent---");
        } else if(event instanceof AuthenticationFailureDisabledEvent){
            //验证过了但是账户被禁用
            System.out.println("---AuthenticationFailureDisabledEvent---");
        } else if(event instanceof AuthenticationFailureExpiredEvent){
            //验证通过了，但是账号已经过期
            System.out.println("---AuthenticationFailureExpiredEvent---");
        }  else if(event instanceof AuthenticationFailureLockedEvent){
            //账户被锁定
            System.out.println("---AuthenticationFailureLockedEvent---");
        } else if(event instanceof AuthenticationFailureProviderNotFoundEvent){
            //配置错误，没有合适的AuthenticationProvider来处理登录验证
            System.out.println("---AuthenticationFailureProviderNotFoundEvent---");
        } else if(event instanceof AuthenticationFailureProxyUntrustedEvent){
            //代理不受信任，用于Oauth、CAS这类三方验证的情形，多属于配置错误
            System.out.println("---AuthenticationFailureProxyUntrustedEvent---");
        } else if(event instanceof AuthenticationFailureServiceExceptionEvent){
            //其他任何在AuthenticationManager中内部发生的异常都会被封装成此类
            System.out.println("---AuthenticationFailureServiceExceptionEvent---");
        }
    }

}
