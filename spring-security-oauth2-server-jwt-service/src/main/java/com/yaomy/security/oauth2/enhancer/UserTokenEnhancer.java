package com.yaomy.security.oauth2.enhancer;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.Map;

/**
 * @Description: 用户自定义token令牌，包括access_token和refresh_token
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.enhancer.UserTokenEnhancer
 * @Date: 2019/7/9 19:43
 * @Version: 1.0
 */
public class UserTokenEnhancer extends JwtAccessTokenConverter {
    /**
     * @Description 重新定义令牌token
     * @Date 2019/7/9 19:56
     * @Version  1.0
     */
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        //授权类型 authorization_code、password、client_credentials、refresh_token、implicit
        String grantType = authentication.getOAuth2Request().getGrantType();
        if(!StringUtils.equals(grantType, "client_credentials")){
            String userName = authentication.getUserAuthentication().getName();
            // 与登录时候放进去的UserDetail实现类一直查看link{SecurityConfiguration}
            Object principal = authentication.getUserAuthentication().getPrincipal();
            /**
             自定义一些token属性
             **/
            Map<String, Object> additionalInformation = Maps.newHashMap();
            additionalInformation.put("username", userName);
            if(principal instanceof User){
                additionalInformation.put("principal", ((User)principal).getAuthorities());
            } else {
                additionalInformation.put("principal", principal);
            }
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformation);

        }
        OAuth2AccessToken enhancedToken = super.enhance(accessToken, authentication);
        return enhancedToken;
    }
}
