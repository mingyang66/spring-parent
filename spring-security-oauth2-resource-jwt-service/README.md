# spring-parent
<h3>Spring Security OAuth2 JWT资源服务器配置</h3> 

#### 1.添加资源服务器配置
```package com.yaomy.security.resource.config;
   
   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;
   import org.springframework.security.config.annotation.web.builders.HttpSecurity;
   import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
   import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
   import org.springframework.security.crypto.password.PasswordEncoder;
   import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
   import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
   import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
   import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
   import org.springframework.security.oauth2.provider.token.TokenStore;
   import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
   import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
   
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
        * @Description OAuth2 token持久化接口，jwt不会做持久化处理
        * @Date 2019/7/15 18:12
        * @Version  1.0
        */
       @Bean
       public TokenStore jwtTokenStore() {
           return new JwtTokenStore(accessTokenConverter());
       }
       /**
        * @Description 令牌服务
        * @Date 2019/7/15 18:07
        * @Version  1.0
        */
       @Bean
       public DefaultTokenServices tokenServices(){
           DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
           defaultTokenServices.setTokenStore(jwtTokenStore());
           return defaultTokenServices;
       }
       /**
        * @Description 自定义token令牌增强器
        * @Date 2019/7/11 16:22
        * @Version  1.0
        */
       @Bean
       public JwtAccessTokenConverter accessTokenConverter(){
           JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
           accessTokenConverter.setSigningKey("123");
           return accessTokenConverter;
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

@EnableResourceServer注解实际上相当于在拦截器链之中帮我们加上了OAuth2AuthenticationProcessingFilter过滤器，拦截器会拦截参数中的access_token及Header头中是否
添加有Authorization，并且Authorization是以Bearer开头的access_token才能够识别；过滤器中相关的接口有TokenExtractor，其实现类是BearerTokenExtractor。

#### 2.新增资源服务接口
```
package com.yaomy.security.resource.api;

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

#### 3.启动服务类

```
package com.yaomy.security.resource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description: 资源服务器启动类
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.resource.ResourceBootStrap
 * @Date: 2019/7/12 14:43
 * @Version: 1.0
 */
@SpringBootApplication(scanBasePackages = {"com.yaomy.security.resource"})
public class ResourceBootStrap {
    public static void main(String[] args) {
        SpringApplication.run(ResourceBootStrap.class, args);
    }
}
```
源码GitHub地址(jwt_token分支)：[https://github.com/mingyang66/spring-parent/tree/jwt_token/spring-security-oauth2-resource-service](https://github.com/mingyang66/spring-parent/tree/jwt_token/spring-security-oauth2-resource-service)