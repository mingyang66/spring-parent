package com.yaomy.security.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: API测试类
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.auth.UserAuthAction
 * @Date: 2019/6/28 15:18
 * @Version: 1.0
 */
@RestController
@RequestMapping(value = "auth_user")
public class UserAuthAction {
    /**
     * @Description
     * @Date 2019/7/4 18:05
     * @Version  1.0
     */
    @RequestMapping(value = "get_token_info")
    public Object getTokenInfo(){
        //获取Security空间上下文,默认使用ThreadLocal存储上下文对象，如果要改变存储上下文策略可以通过spring.security.strategy更改，
        SecurityContext ctx = SecurityContextHolder.getContext();
        Authentication auth = ctx.getAuthentication();
        System.out.println(auth.getAuthorities()+"--"+auth.getCredentials()+"--"+auth.getDetails()+"--"+auth.getPrincipal()+"--"+auth.getName());
        return auth;
    }
}
