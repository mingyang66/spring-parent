package com.yaomy.security.oauth2.enhancer;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.Map;
import java.util.UUID;

/**
 * @Description: 用户自定义token令牌，包括access_token和refresh_token
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.enhancer.UserTokenEnhancer
 * @Date: 2019/7/9 19:43
 * @Version: 1.0
 */
public class UserTokenEnhancer implements TokenEnhancer {
    /**
     * @Description 重新定义令牌token
     * @Date 2019/7/9 19:56
     * @Version  1.0
     */
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
       if(accessToken instanceof DefaultOAuth2AccessToken){
           DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
           token.setValue(getToken());
           OAuth2RefreshToken refreshToken = token.getRefreshToken();
           if(refreshToken instanceof DefaultOAuth2RefreshToken){
               token.setRefreshToken(new DefaultOAuth2RefreshToken(getToken()));
           }
           Map<String, Object> additionalInformation = Maps.newHashMap();
           additionalInformation.put("client_id", authentication.getOAuth2Request().getClientId());
           token.setAdditionalInformation(additionalInformation);
           return token;
       }
        return accessToken;
    }
    /**
     * @Description 生成自定义token
     * @Date 2019/7/9 19:50
     * @Version  1.0
     */
    private String getToken(){
        return StringUtils.join("Beare", UUID.randomUUID().toString().replace("-", ""));
    }
}
