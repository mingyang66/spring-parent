package com.yaomy.security.jwt.handler;

import com.emily.framework.common.utils.json.JSONUtils;
import com.yaomy.security.jwt.po.ResponseBody;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description: 用来解决匿名用户访问无权限资源时的异常
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.handler.AjaxAuthenticationEntryPoint
 * @Date: 2019/7/1 15:36
 * @Version: 1.0
 */
@Component
public class UserAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        ResponseBody responseBody = new ResponseBody();

        responseBody.setStatus("000");
        responseBody.setMsg("Need Authorities!");

        httpServletResponse.getWriter().write(JSONUtils.toJSONString(responseBody));

    }
}
