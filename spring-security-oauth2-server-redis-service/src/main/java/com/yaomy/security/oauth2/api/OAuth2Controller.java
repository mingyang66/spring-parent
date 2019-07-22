package com.yaomy.security.oauth2.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
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
    /**
     * @Description 获取token信息
     * @Date 2019/7/22 15:59
     * @Version  1.0
     */
    @RequestMapping(value = "get_token", method = RequestMethod.GET)
    public OAuth2RestTemplate getToken(){
        BaseOAuth2ProtectedResourceDetails resourceDetails = new BaseOAuth2ProtectedResourceDetails();
        resourceDetails.setClientId("client_password");
        resourceDetails.setClientSecret("secret");
        resourceDetails.setGrantType("password");

        DefaultAccessTokenRequest accessTokenRequest = new DefaultAccessTokenRequest();
        OAuth2ClientContext oAuth2ClientContext = new DefaultOAuth2ClientContext(accessTokenRequest);
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resourceDetails,oAuth2ClientContext);

        return restTemplate;
    }
}
