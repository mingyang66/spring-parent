package com.yaomy.security.oauth2.api;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 资源服务器
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.resource.api.ResourceController
 * @Date: 2019/7/12 14:59
 * @Version: 1.0
 */
@RestController
@RequestMapping("/resource")
public class ResourceController {

    @RequestMapping(value = "context", method = RequestMethod.GET)
    @ResponseBody
    public Object get(){
        SecurityContext ctx = SecurityContextHolder.getContext();
        return ctx;
    }
    @RequestMapping(value = "auth", method = RequestMethod.GET)
    @ResponseBody
    public Object getAuth(){
        SecurityContext ctx = SecurityContextHolder.getContext();
        return ctx.getAuthentication();
    }
}
