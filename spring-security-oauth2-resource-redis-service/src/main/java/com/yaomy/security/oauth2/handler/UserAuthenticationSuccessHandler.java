package com.yaomy.security.oauth2.handler;

import com.yaomy.common.po.BaseResponse;
import com.yaomy.common.utils.HttpUtils;
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
 * @Date: 2019/7/1 15:38
 * @Version: 1.0
 */
@Component
public class UserAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        HttpUtils.writeSuccess(BaseResponse.createResponse(200, "SUCESS", authentication), response);
    }
}
