package com.yaomy.security.oauth2.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.api.ExceptionController
 * @Author: 姚明洋
 * @Date: 2019/7/17 14:18
 * @Version: 1.0
 */
@RestController
public class ExceptionController {
    @RequestMapping("/oauth/error")
    public void error(HttpServletRequest request, HttpServletResponse response){
        System.out.println("------------------");
    }
}
