package com.yaomy.security.oauth2.api;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yaomy.common.enums.GrantTypeEnum;
import com.yaomy.common.enums.HttpStatusMsg;
import com.yaomy.common.po.BaseResponse;
import com.yaomy.security.oauth2.service.PropertyService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * @Description: 端点访问控制包装类 示例：https://www.programcreek.com/java-api-examples/?code=h819/spring-boot/spring-boot-master/spring-security-oauth/spring-security-oauth2-client/src/main/java/com/base/oauth2/client/controller/SpringOauth2ClientController.java#
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.api.OAuth2Controller
 * @Date: 2019/7/22 15:57
 * @Version: 1.0
 */
@RestController
@RequestMapping(value = "oauth2")
public class OAuth2Controller {

    @Autowired
    private PropertyService propertyService;
    @Autowired
    @Lazy
    private TokenStore tokenStore;

    /**
     * @Description /oauth/token(令牌端点) 获取用户token信息
     * @Date 2019/7/22 15:59
     * @Version  1.0
     */
    @RequestMapping(value = "token", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse> getToken(@RequestParam String username, @RequestParam String password){

        ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
        resource.setId(propertyService.getProperty("spring.security.oauth.resource.id"));
        resource.setClientId(propertyService.getProperty("spring.security.oauth.resource.client.id"));
        resource.setClientSecret(propertyService.getProperty("spring.security.oauth.resource.client.secret"));
        resource.setGrantType(GrantTypeEnum.PASSWORD.getGrant_type());
        resource.setAccessTokenUri(propertyService.getProperty("spring.security.oauth.token.uri"));
        resource.setUsername(username);
        resource.setPassword(password);
        resource.setScope(Arrays.asList("all"));

        OAuth2RestTemplate template = new OAuth2RestTemplate(resource);
        ResourceOwnerPasswordAccessTokenProvider provider = new ResourceOwnerPasswordAccessTokenProvider();
        template.setAccessTokenProvider(provider);
        try {
            OAuth2AccessToken accessToken = template.getAccessToken();
            Map<String, Object> result = Maps.newHashMap();
            result.put("access_token", accessToken.getValue());
            result.put("token_type", accessToken.getTokenType());
            result.put("refresh_token", accessToken.getRefreshToken().getValue());
            result.put("expires_in", accessToken.getExpiresIn());
            result.put("scope", StringUtils.join(accessToken.getScope(), ","));
            result.putAll(accessToken.getAdditionalInformation());

            Collection<? extends GrantedAuthority> authorities = tokenStore.readAuthentication(template.getAccessToken()).getUserAuthentication().getAuthorities();
            JSONObject jsonObject = new JSONObject();
            for(GrantedAuthority authority:authorities){
                jsonObject.putAll(JSONObject.parseObject(authority.getAuthority()));
            }
            result.put("authorities", jsonObject);

            return ResponseEntity.ok(BaseResponse.createResponse(HttpStatusMsg.OK, result));
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.ok(BaseResponse.createResponse(HttpStatusMsg.AUTHENTICATION_EXCEPTION, e.toString()));
        }
    }
    /**
     * @Description /oauth/token（令牌端点）刷新token信息
     * @Date 2019/7/25 16:13
     * @Version  1.0
     */
    @RequestMapping(value = "refresh_token", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse> refreshToken(@RequestParam String refresh_token){
        try {
            ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
            resource.setId(propertyService.getProperty("spring.security.oauth.resource.id"));
            resource.setClientId(propertyService.getProperty("spring.security.oauth.resource.client.id"));
            resource.setClientSecret(propertyService.getProperty("spring.security.oauth.resource.client.secret"));
            resource.setGrantType(GrantTypeEnum.REFRESH_TOKEN.getGrant_type());
            resource.setAccessTokenUri(propertyService.getProperty("spring.security.oauth.token.uri"));

            ResourceOwnerPasswordAccessTokenProvider provider = new ResourceOwnerPasswordAccessTokenProvider();
            OAuth2RefreshToken refreshToken = tokenStore.readRefreshToken(refresh_token);
            OAuth2AccessToken accessToken = provider.refreshAccessToken(resource, refreshToken, new DefaultAccessTokenRequest());

            Map<String, Object> result = Maps.newLinkedHashMap();
            result.put("access_token", accessToken.getValue());
            result.put("token_type", accessToken.getTokenType());
            result.put("refresh_token", accessToken.getRefreshToken().getValue());
            result.put("expires_in", accessToken.getExpiresIn());
            result.put("scope", StringUtils.join(accessToken.getScope(), ","));
            result.putAll(accessToken.getAdditionalInformation());

            Collection<? extends GrantedAuthority> authorities = tokenStore.readAuthentication(accessToken).getUserAuthentication().getAuthorities();
            JSONObject jsonObject = new JSONObject();
            for(GrantedAuthority authority:authorities){
                jsonObject.putAll(JSONObject.parseObject(authority.getAuthority()));
            }
            result.put("authorities", jsonObject);

            return ResponseEntity.ok(BaseResponse.createResponse(HttpStatusMsg.OK, result));
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.ok(BaseResponse.createResponse(HttpStatusMsg.AUTHENTICATION_EXCEPTION, e.toString()));
        }
    }
    /**
     * @Description oauth/check_token（端点校验）token有效性
     * @Date 2019/7/25 16:22
     * @Version  1.0
     */
    @RequestMapping(value = "check_token", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse> checkToken(String access_token){
        OAuth2AccessToken accessToken = tokenStore.readAccessToken(access_token);
        OAuth2Authentication auth2Authentication = tokenStore.readAuthentication(access_token);
        Map<String, Object> map = Maps.newHashMap();
        //用户名
        map.put("username", auth2Authentication.getUserAuthentication().getName());
        //是否过期
        map.put("isExpired", accessToken.isExpired());
        //过期时间
        map.put("expiration", DateFormatUtils.format(accessToken.getExpiration(), "yyyy-MM-dd HH:mm:ss"));
        BaseResponse response = null;
        try {
            response = BaseResponse.createResponse(HttpStatusMsg.OK, map);
        } catch (Exception e){
            response = BaseResponse.createResponse(HttpStatusMsg.AUTHENTICATION_EXCEPTION, e.toString());
        }
        return ResponseEntity.ok(response);
    }
    /**
     * @Description 账号退出
     * @Date 2019/7/25 17:47
     * @Version  1.0
     */
    @RequestMapping(value = "logout", method = RequestMethod.POST)
    public ResponseEntity<BaseResponse> logOut(String access_token){
        if(StringUtils.isNotBlank(access_token)){
            OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(access_token);
            if(oAuth2AccessToken != null){
                System.out.println("----access_token是："+oAuth2AccessToken.getValue());
                tokenStore.removeAccessToken(oAuth2AccessToken);
                OAuth2RefreshToken oAuth2RefreshToken = oAuth2AccessToken.getRefreshToken();
                tokenStore.removeRefreshToken(oAuth2RefreshToken);
                tokenStore.removeAccessTokenUsingRefreshToken(oAuth2RefreshToken);
            }
        }
        return ResponseEntity.ok(BaseResponse.createResponse(HttpStatusMsg.OK));
    }
}
