package com.yaomy.security.oauth2.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<SecurityContext> get(@RequestParam String username, @RequestParam int age)  {
        SecurityContext ctx = SecurityContextHolder.getContext();
        return new ResponseEntity<>(ctx, HttpStatus.OK);
    }

    @RequestMapping(value = "auth", method = RequestMethod.GET)
    @ResponseBody
    public Object getAuth(){
        SecurityContext ctx = SecurityContextHolder.getContext();
        return ctx.getAuthentication();
    }
}
