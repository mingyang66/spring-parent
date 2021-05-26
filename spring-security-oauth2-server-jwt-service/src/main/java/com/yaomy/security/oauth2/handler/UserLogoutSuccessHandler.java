package com.yaomy.security.oauth2.handler;

import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.yaomy.security.oauth2.po.ResponseBody;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description: 用户成功退出
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.handler.AjaxLogoutSuccessHandler
 * @Date: 2019/7/1 15:39
 * @Version: 1.0
 */
@Component
public class UserLogoutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        ResponseBody responseBody = new ResponseBody();

        responseBody.setStatus("100");
        responseBody.setMsg("Logout Success!");
        System.out.println( request.getParameter("code"));;
        response.getWriter().write(JSONUtils.toJSONString(responseBody));

    }
}
