package com.yaomy.security.handler;

import com.alibaba.fastjson.JSON;
import com.yaomy.security.po.ResponseBody;
import com.yaomy.security.po.AuthUserDetails;
import com.yaomy.security.util.JwtTokenUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description: 用户认证成功处理
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.handler.AjaxAuthenticationSuccessHandler
 * @Author: 姚明洋
 * @Date: 2019/7/1 15:38
 * @Version: 1.0
 */
@Component
public class UserAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        ResponseBody responseBody = new ResponseBody();

        responseBody.setStatus("200");
        responseBody.setMsg("Login Success!");

        AuthUserDetails userDetails = (AuthUserDetails) authentication.getPrincipal();

        String token = JwtTokenUtil.generateToken(userDetails.getUsername(), "_secret");
        responseBody.setToken(token);

        httpServletResponse.getWriter().write(JSON.toJSONString(responseBody));
    }
}
