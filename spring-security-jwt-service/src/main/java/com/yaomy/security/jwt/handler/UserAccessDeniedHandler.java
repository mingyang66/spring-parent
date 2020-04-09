package com.yaomy.security.jwt.handler;

import com.sgrain.boot.common.utils.json.JSONUtils;
import com.yaomy.security.jwt.po.ResponseBody;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description: 用来解决认证过的用户访问无权限资源时的异常
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.handler.AjaxAccessDeniedHandler
 * @Date: 2019/7/1 15:34
 * @Version: 1.0
 */
@Component
public class UserAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        ResponseBody responseBody = new ResponseBody();

        responseBody.setStatus("300");
        responseBody.setMsg("Need Authorities!");

        httpServletResponse.getWriter().write(JSONUtils.toJSONString(responseBody));

    }
}
