package com.emily.infrastructure.autoconfigure.servlet;

import com.emily.infrastructure.autoconfigure.servlet.annotation.ApiPathPrefixIgnore;
import com.emily.infrastructure.autoconfigure.servlet.interceptor.ParameterInterceptor;
import com.emily.infrastructure.core.constant.CharacterInfo;
import com.emily.infrastructure.logger.LoggerFactory;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.config.annotation.*;

/**
 * webmvc自动化配置
 *
 * @author Emily
 * @since 2020/05/26
 */
@AutoConfiguration
@EnableConfigurationProperties(ServletProperties.class)
@ConditionalOnProperty(prefix = ServletProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class ServletAutoConfiguration implements WebMvcConfigurer, InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(ServletAutoConfiguration.class);

    private final ServletProperties properties;
    /**
     * 忽略URL前缀的控制器类
     */
    private static String[] ignoreUrlPrefixController = new String[]{"springfox.documentation.swagger.web.ApiResourceController",
            "org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController"};

    public ServletAutoConfiguration(ServletProperties properties) {
        this.properties = properties;
    }

    /**
     * 配置路由规则
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        if (!properties.getPath().isEnabled()) {
            return;
        }
        AntPathMatcher matcher = new AntPathMatcher();
        //区分大小写,默认true
        matcher.setCaseSensitive(properties.getPath().isCaseSensitive());
        //是否去除前后空格,默认false
        matcher.setTrimTokens(properties.getPath().isTrimTokens());
        //分隔符
        matcher.setPathSeparator(CharacterInfo.PATH_SEPARATOR);
        //是否缓存匹配规则,默认null等于true
        matcher.setCachePatterns(properties.getPath().isCachePatterns());
        //设置路由匹配规则
        configurer.setPathMatcher(matcher);
        //设置URL末尾是否支持斜杠，默认true,如/a/b/有效，/a/b也有效
        configurer.setUseTrailingSlashMatch(properties.getPath().isUseTrailingSlashMatch());
        //忽略URL前缀的控制器类
        ignoreUrlPrefixController = ArrayUtils.addAll(ignoreUrlPrefixController, properties.getPath().getExclude().toArray(new String[]{}));
        //给所有的接口统一添加前缀
        configurer.addPathPrefix(properties.getPath().getPrefix(), c -> {
            if (c.isAnnotationPresent(ApiPathPrefixIgnore.class) || ArrayUtils.contains(ignoreUrlPrefixController, c.getName())) {
                return false;
            } else {
                return true;
            }
        });
    }

    /**
     * 跨域设置
     * 在浏览器console控制台测试ajax示例
     * $.ajax({
     * url:"http://172.30.67.122:9000/api/void/test1",//发送的路径
     * type:"POST",//发送的方式
     * async:false,
     * data:JSON.stringify({'name':'test','age':23}),//发送的数据
     * contentType: "application/json", //提交数据类型
     * dataType:"json",//服务器返回的数据类型
     * success: function(data) {
     * console.log(data)
     * },
     * error: function (data){
     * alert("提交失败");
     * }
     * });
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (BooleanUtils.isFalse(properties.getCors().isEnabled())) {
            return;
        }
        //启用跨域匹配的路径，默认所有请求，示例：/admin或/admin/**
        CorsRegistration registration = registry.addMapping("/**");
        //允许来自所有域名请求
        if (!properties.getCors().getAllowedOrigins().isEmpty()) {
            registration.allowedOrigins(properties.getCors().getAllowedOrigins().toArray(new String[]{}));
        } else {
            registration.allowedOriginPatterns("*");
        }
        //设置所允许的HTTP请求方法，*号代表允许所有方法
        if (!properties.getCors().getAllowedMethods().isEmpty()) {
            registration.allowedMethods(properties.getCors().getAllowedMethods().toArray(new String[]{}));
        } else {
            registration.allowedMethods("OPTIONS", "GET", "PUT", "POST");
        }
        //服务器支持的所有头信息字段，多个字段用逗号分隔；默认支持所有，*号代表所有
        if (!properties.getCors().getAllowedHeaders().isEmpty()) {
            registration.allowedHeaders(properties.getCors().getAllowedHeaders().toArray(new String[]{}));
        } else {
            registration.allowedHeaders("*");
        }
        //浏览器是否应该发送凭据，如是否允许发送Cookie，true为允许
        if (BooleanUtils.isFalse(properties.getCors().isAllowCredentials())) {
            registration.allowCredentials(false);
        } else {
            registration.allowCredentials(true);
        }
        //设置响应HEAD,默认无任何设置，不可以使用*号
        if (!properties.getCors().getExposedHeaders().isEmpty()) {
            registration.exposedHeaders(properties.getCors().getExposedHeaders().toArray(new String[]{}));
        }
        //设置多长时间内不需要发送预检验请求，可以缓存该结果，默认1800秒
        registration.maxAge(properties.getCors().getMaxAge());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ParameterInterceptor()).addPathPatterns("/**");
    }

    @Override
    public void destroy() throws Exception {
        logger.info("<== 【销毁--自动化配置】----WebMvc组件【WebMvcAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("==> 【初始化--自动化配置】----WebMvc组件【WebMvcAutoConfiguration】");

    }
}
