# spring-parent
###  Spring Security OAuth2 Redis 资源服务器配置

#### 1.资源服务器相关依赖
```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.security.oauth</groupId>
            <artifactId>spring-security-oauth2</artifactId>
            <version>2.3.5.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
```

***

#### 2.资源服务器配置类

```
package com.yaomy.security.oauth2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * @Description: @EnableResourceServer注解实际上相当于加上OAuth2AuthenticationProcessingFilter过滤器
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.config.ResServerConfig
 * @Date: 2019/7/9 13:28
 * @Version: 1.0
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources
                .tokenServices(tokenServices())
                //资源ID
                .resourceId("resource_password_id");
        super.configure(resources);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest()
                .permitAll();

        http.csrf().disable();
    }
    /**
     * @Description OAuth2 token持久化接口
     * @Date 2019/7/15 18:12
     * @Version  1.0
     */
    @Bean
    public TokenStore tokenStore() {
        return new RedisTokenStore(redisConnectionFactory);
    }
    /**
     * @Description 令牌服务
     * @Date 2019/7/15 18:07
     * @Version  1.0
     */
    @Bean
    public DefaultTokenServices tokenServices(){
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        return defaultTokenServices;
    }
    /**
     * @Description 加密方式
     * @Date 2019/7/15 18:06
     * @Version  1.0
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
```

***

#### 3.配置文件

```
server.port=9004
##单机应用环境配置
spring.redis.host=127.0.0.1
spring.redis.port=6379
#spring.redis.password=
spring.redis.database=0
#spring.redis.timeout=

##redis连接池配置
## 连接池中的最小空闲连接，默认0
spring.redis.jedis.pool.min-idle=0
## 连接池中的最大空闲连接，默认8
spring.redis.jedis.pool.max-idle=8
## 连接池最大阻塞等待时间（使用负值表示没有限制），默认-1ms
spring.redis.jedis.pool.max-wait=-1ms
##连接池最大连接数（使用负值表示没有限制），默认8
spring.redis.jedis.pool.max-active=8
```

***

#### 4.资源控制器
```
package com.yaomy.security.oauth2.api;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 资源服务器
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.resource.api.ResourceController
 * @Date: 2019/7/12 14:59
 * @Version: 1.0
 */
@RestController
@RequestMapping("/resource")
public class ResourceController {

    @RequestMapping(value = "context", method = RequestMethod.GET)
    @ResponseBody
    public Object get(){
        SecurityContext ctx = SecurityContextHolder.getContext();
        return ctx;
    }
}
```

***

### Spring Security OAuth2认证资源服务器异常处理

#### 1.我们先看两个异常处理的接口
 - AuthenticationEntryPoint:用来解决匿名用户访问无权限资源时的异常,也就是跟token相关的资源异常
 - AccessDeniedHandler：用来解决认证过的用户访问无权限资源时的异常，主要跟权限控制相关
 
 ***
 
 #### 2.自定义AuthenticationEntryPoint异常处理类
 
 ```
 package com.yaomy.security.oauth2.handler;
 
 import com.yaomy.common.po.BaseResponse;
 import com.yaomy.common.utils.HttpUtils;
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
         HttpUtils.writeError(BaseResponse.createResponse(401, e.getMessage()), httpServletResponse);
 
     }
 }
```
***

#### 3.自定义AccessDeniedHandler接口实现类

```
package com.yaomy.security.oauth2.handler;

import com.yaomy.common.po.BaseResponse;
import com.yaomy.common.utils.HttpUtils;
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
        HttpUtils.writeError(BaseResponse.createResponse(300, e.getMessage()), httpServletResponse);

    }
}
```

***

#### 4.相关工具方法如下

```
package com.yaomy.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaomy.common.po.BaseResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Package: com.yaomy.common.utils.HttpUtils
 * @Date: 2019/7/18 9:34
 * @Version: 1.0
 */
public class HttpUtils {
    /**
     * 异常输出工具类
     */
    public static void writeError(BaseResponse bs, HttpServletResponse response) throws IOException {
        response.setContentType("application/json,charset=utf-8");
        response.setStatus(bs.getStatus());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getOutputStream(), bs);
    }
}
```

```
package com.yaomy.common.po;

import lombok.Data;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.po.AjaxResponseBody
 * @Date: 2019/7/1 15:33
 * @Version: 1.0
 */
@Data
public class BaseResponse {
    private int status;
    private String message;
    /**
     * @Description 创建响应对象
     * @Date 2019/7/18 10:10
     * @Version  1.0
     */
    public static BaseResponse createResponse(int status, String message){
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setStatus(status);
        baseResponse.setMessage(message);
        return baseResponse;
    }
}
```
***

GitHub源码：[https://github.com/mingyang66/spring-parent/tree/master/spring-security-oauth2-resource-redis-service](https://github.com/mingyang66/spring-parent/tree/master/spring-security-oauth2-resource-redis-service)
