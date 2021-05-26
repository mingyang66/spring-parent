package com.yaomy.security.oauth2.handler;

import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.yaomy.security.oauth2.po.ResponseBody;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description: 用户认证失败处理
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.handler.AjaxAuthenticationFailureHandler
 * @Date: 2019/7/1 15:37
 * @Version: 1.0
 */
@Component
public class UserAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        ResponseBody responseBody = new ResponseBody();

        responseBody.setStatus("400");
        responseBody.setMsg("Login Failure!");

        httpServletResponse.getWriter().write(JSONUtils.toJSONString(responseBody));

    }
}
