package com.yaomy.security.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.auth.UserAuthAction
 * @Author: 姚明洋
 * @Date: 2019/6/28 15:18
 * @Version: 1.0
 */
@RestController
@RequestMapping(value = "auth_user")
public class UserAuthAction {
    @RequestMapping(value = "login")
    public String login(){
        System.out.println("===================");
        SecurityContext ctx = SecurityContextHolder.getContext();
        Authentication auth = ctx.getAuthentication();
        System.out.println(auth.getAuthorities()+"--"+auth.getCredentials()+"--"+auth.getDetails()+"--"+auth.getPrincipal()+"--"+auth.getName());
        return "hello";
    }
}
