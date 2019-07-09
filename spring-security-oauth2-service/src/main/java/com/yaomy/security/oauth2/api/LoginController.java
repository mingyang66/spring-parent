package com.yaomy.security.oauth2.api;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 登录测试接口
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.api.LoginController
 * @Date: 2019/7/8 17:40
 * @Version: 1.0
 */
@RestController
@RequestMapping("auth_user")
public class LoginController {
    /**
     * @Description 登录测试接口
     * @Date 2019/7/8 17:42
     * @Version  1.0
     */
    @RequestMapping(value = "get_token_info", method = RequestMethod.GET)
    public Object login(HttpServletRequest request){
        SecurityContext ctx = SecurityContextHolder.getContext();
        Authentication auth = ctx.getAuthentication();
        System.out.println(auth.getAuthorities()+"--"+auth.getCredentials()+"--"+auth.getDetails()+"--"+auth.getPrincipal()+"--"+auth.getName());
        return auth;
    }
    @RequestMapping("/home")
    public String home() {
        return "home page";
    }

    @RequestMapping("/")
    public String index() {
        return "index page";
    }

}
