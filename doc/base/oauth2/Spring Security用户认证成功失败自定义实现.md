### Spring Security用户认证成功失败自定义实现

【一】[Spring boot Security OAuth2用户登录失败事件发布及监听](https://github.com/mingyang66/spring-parent/blob/master/spring-security-oauth2-server-redis-service/event.md)<br>
【二】[Spring Security用户认证成功失败源码分析](https://github.com/mingyang66/spring-parent/blob/master/spring-security-oauth2-server-redis-service/eventUpgrade.md)<br>

[上一篇文章讲解了用户认证成功或者失败事件发布的整个流程](https://github.com/mingyang66/spring-parent/blob/master/spring-security-oauth2-server-redis-service/eventUpgrade.md)，
这一篇就讲解下自定义的实现方式。首先看一下认证的异常都有哪些：

在org.springframework.security.authentication.event包下定义了发生认证时的所有事件类型，其中AbstractAuthenticationEvent是所有事件的父类，其它事件
都继承于AbstractAuthenticationEvent，其子类有AbstractAuthenticationFailureEvent、AuthenticationFailureBadCredentialsEvent、AuthenticationFailureCredentialsExpiredEvent
、AuthenticationFailureDisabledEvent、AuthenticationFailureExpiredEvent、AuthenticationFailureLockedEvent、AuthenticationFailureProviderNotFoundEvent
、AuthenticationFailureProxyUntrustedEvent、AuthenticationFailureServiceExceptionEvent、AuthenticationSuccessEvent、InteractiveAuthenticationSuccessEvent；
而AbstractAuthenticationFailureEvent又是所有认证异常发布事件的抽象类，这样就可以方便的分开成两个监听器；

#### 1.定义认证成功发布事件监听器

 ```
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
 public class AuthencationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {
 
     @Override
     public void onApplicationEvent(AuthenticationSuccessEvent event) {
             //用户通过输入用户名和密码登录成功
             System.out.println("---AuthenticationSuccessEvent---");
     }
 
 }
```

当然如果有需要可以将AuthenticationSuccessEvent更换为InteractiveAuthenticationSuccessEvent，都是认证成功，但是InteractiveAuthenticationSuccessEvent表示通过自动交互的手段来登录成功，比如cookie自动登录

#### 2.定义认证失败事件发布监听器

```
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
```

GitHub源码：[https://github.com/mingyang66/spring-parent/blob/master/spring-security-oauth2-server-redis-service/eventUpgradeCode.md](https://github.com/mingyang66/spring-parent/blob/master/spring-security-oauth2-server-redis-service/eventUpgradeCode.md)    
    