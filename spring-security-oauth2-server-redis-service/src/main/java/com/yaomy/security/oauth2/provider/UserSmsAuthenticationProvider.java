package com.yaomy.security.oauth2.provider;

import com.yaomy.security.oauth2.event.event.UserLoginFailedEvent;
import com.yaomy.security.oauth2.exception.PasswordException;
import com.yaomy.security.oauth2.exception.UsernameException;
import com.yaomy.security.oauth2.service.UserAuthDetailsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @Description: 用户自定义身份认证,短信验证码模式
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.provider.MyAuthenticationProvider
 * @Date: 2019/7/2 17:17
 * @Version: 1.0
 */
@Component
public class UserSmsAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserAuthDetailsService authUserDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    //@Autowired
    //private ApplicationEventPublisher publisher;
    /**
     * @Description 认证处理，返回一个Authentication的实现类则代表认证成功，返回null则代表认证失败
     * @Date 2019/7/5 15:19
     * @Version  1.0
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //测试，正式环境应该根据登录认证的通道来进行认证
        String username = authentication.getName();
        String smscode = (String) authentication.getCredentials();
        if(StringUtils.isBlank(username)){
            throw new UsernameNotFoundException("username用户名不可以为空");
        }
        if(StringUtils.isBlank(smscode)){
            throw new BadCredentialsException("验证码不可以为空");
        }
        //获取用户信息
        UserDetails user = authUserDetailsService.loadUserByUsername(username);
        String smscodeCache = "1234";
        //比较前端传入的密码明文和数据库中加密的密码是否相等
        if (!passwordEncoder.matches(smscode, smscodeCache)) {
            //发布密码不正确事件
            //publisher.publishEvent(new UserLoginFailedEvent(authentication));
            throw new BadCredentialsException("sms_code验证码不正确");
        }
        //获取用户权限信息
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        return new UsernamePasswordAuthenticationToken(user, smscode, authorities);

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
