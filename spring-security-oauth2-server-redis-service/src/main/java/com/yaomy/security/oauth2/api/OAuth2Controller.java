package com.yaomy.security.oauth2.api;

import com.google.common.collect.Maps;
import com.yaomy.common.enums.HttpStatusMsg;
import com.yaomy.common.po.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;

/**
 * @Description: 端点访问控制包装类
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.api.OAuth2Controller
 * @Date: 2019/7/22 15:57
 * @Version: 1.0
 */
@RestController
@RequestMapping(value = "oauth2")
public class OAuth2Controller {

    @Value("${oauth.token.uri}")
    private String tokenUri;

    @Value("${oauth.resource.id}")
    private String resourceId;

    @Value("${oauth.resource.client.id}")
    private String resourceClientId;

    @Value("${oauth.resource.client.secret}")
    private String resourceClientSecret;

    @Value("${oauth.resource.user.id}")
    private String resourceUserId;

    @Value("${oauth.resource.user.password}")
    private String resourceUserPassword;
    @Autowired
    private OAuth2RestOperations restOperations;
    /**
     * @Description 获取token信息
     * @Date 2019/7/22 15:59
     * @Version  1.0
     */
    @RequestMapping(value = "get_token", method = RequestMethod.GET)
    public ResponseEntity<BaseResponse> getToken(){
        BaseResponse response = null;
        try {
            response = BaseResponse.createResponse(HttpStatusMsg.OK, restOperations.getAccessToken());
        } catch (Exception e){
            response = BaseResponse.createResponse(HttpStatusMsg.AUTHENTICATION_EXCEPTION, e.toString());
        }
        return ResponseEntity.ok(response);
    }
    @RequestMapping(value = "refresh_token", method = RequestMethod.GET)
    public ResponseEntity refreshToken(@RequestParam String refreshToken){
        BaseResponse response = null;
        try {
            Map<String, Object> param = Maps.newHashMap();
            param.put("grant_type", "refresh_token");
            param.put("refresh_token", refreshToken);
            ResponseEntity<Map.Entry> map = restOperations.postForEntity(URI.create(tokenUri),param, Map.Entry.class);
            System.out.println(map);
            response = BaseResponse.createResponse(HttpStatusMsg.OK, restOperations.getAccessToken());
        } catch (Exception e){
            response = BaseResponse.createResponse(HttpStatusMsg.AUTHENTICATION_EXCEPTION, e.toString());
        }
        return ResponseEntity.ok(response);
    }
}
