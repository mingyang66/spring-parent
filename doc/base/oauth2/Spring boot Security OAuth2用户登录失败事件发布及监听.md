### Spring boot Security OAuth2用户登录失败事件发布及监听

【一】[Spring Security用户认证成功失败源码分析](https://github.com/mingyang66/spring-parent/blob/master/spring-security-oauth2-server-redis-service/eventUpgrade.md)<br>
【二】[Spring Security用户认证成功失败自定义实现](https://github.com/mingyang66/spring-parent/blob/master/spring-security-oauth2-server-redis-service/eventUpgradeCode.md)

#### 1.spring事件简介

spring中的事件分为三部分，事件、监听器、事件源，其中事件是核心；涉及到ApplicationEventPublisher接口、ApplicationEvent类、ApplicationListener接口；

#### 2.定义用户登录失败事件

```
package com.yaomy.security.oauth2.event.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.security.core.Authentication;

/**
 *  定义用户登录失败事件
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.event.event.UserLoginFailedEvent
 * @Date: 2019/7/31 10:46
 * @since 1.0
 */
public class UserLoginFailedEvent extends ApplicationEvent {
    public UserLoginFailedEvent(Authentication authentication) {
        super(authentication);
    }
}
```

#### 3.定义事件监听器

```
package com.yaomy.security.oauth2.event.listener;

import com.yaomy.security.oauth2.event.event.UserLoginFailedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/**
 *  用户登录失败监听器
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.handler.ApplicationListenerAuthencationSuccess
 * @Date: 2019/7/25 11:27
 * @since 1.0
 */
@Component
public class UserLoginFailedListener implements ApplicationListener<UserLoginFailedEvent> {
    @Override
    public void onApplicationEvent(UserLoginFailedEvent event) {
        System.out.println("----用户验证信息---faile----------------------");
    }
}
```

#### 4.事件发布,定义ApplicationEventPublisher对象并发布事件

```
package com.yaomy.security.oauth2.provider;

import com.yaomy.security.oauth2.event.event.UserLoginFailedEvent;
import com.yaomy.security.oauth2.exception.PasswordException;
import com.yaomy.security.oauth2.exception.UsernameException;
import com.yaomy.security.oauth2.service.UserAuthDetailsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 *  用户自定义身份认证
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.provider.MyAuthenticationProvider
 * @Date: 2019/7/2 17:17
 * @since 1.0
 */
@Component
public class UserAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserAuthDetailsService authUserDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ApplicationEventPublisher publisher;
    /**
     * @Description 认证处理，返回一个Authentication的实现类则代表认证成功，返回null则代表认证失败
     * @Date 2019/7/5 15:19
     * @Version  1.0
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        if(StringUtils.isBlank(username)){
            throw new UsernameException("username用户名不可以为空");
        }
        if(StringUtils.isBlank(password)){
            throw new PasswordException("密码不可以为空");
        }
        //获取用户信息
        UserDetails user = authUserDetailsService.loadUserByUsername(username);
        //比较前端传入的密码明文和数据库中加密的密码是否相等
        if (!passwordEncoder.matches(password, user.getPassword())) {
            //发布密码不正确事件
            publisher.publishEvent(new UserLoginFailedEvent(authentication));
            throw new PasswordException("password密码不正确");
        }
        //获取用户权限信息
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        return new UsernamePasswordAuthenticationToken(user, password, authorities);

    }
    /**
     * @Description 如果该AuthenticationProvider支持传入的Authentication对象，则返回true
     * @Date 2019/7/5 15:18
     * @Version  1.0
     */
    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }

}
```

通过以上操作就可以在用户登录失败后有效的监听到失败状态，并且很好的解耦代码；

GitHub源码：[https://github.com/mingyang66/spring-parent/blob/master/spring-security-oauth2-server-redis-service/event.md](https://github.com/mingyang66/spring-parent/blob/master/spring-security-oauth2-server-redis-service/event.md)