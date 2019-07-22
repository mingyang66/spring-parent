package com.yaomy.security.oauth2.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 端点访问控制包装类
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.api.OAuth2Controller
 * @Date: 2019/7/22 15:57
 * @Version: 1.0
 */
@RestController
@RequestMapping(value = "oauth")
public class OAuth2Controller {
    @Autowired
    private OAuth2RestOperations oAuth2RestOperations;
    /**
     * @Description 获取token信息
     * @Date 2019/7/22 15:59
     * @Version  1.0
     */
    @RequestMapping(value = "get_token", method = RequestMethod.GET)
    public OAuth2AccessToken getToken(){
        System.out.println(oAuth2RestOperations.getResource().getAccessTokenUri());
        return oAuth2RestOperations.getAccessToken();
    }
}
