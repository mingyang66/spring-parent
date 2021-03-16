package com.emily.framework.autoconfigure.web;

import com.emily.framework.autoconfigure.web.annotation.ApiPrefix;
import com.emily.framework.common.utils.constant.CharacterUtils;
import com.emily.framework.common.utils.log.LoggerUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Objects;

/**
 * @program: spring-parent
 * @description: webmvc自动化配置
 * @create: 2020/05/26
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(WebProperties.class)
public class EmilyWebAutoConfiguration implements WebMvcConfigurer, InitializingBean, DisposableBean {

    private WebProperties webProperties;
    //自定义路由规则是否已加载
    private boolean enablePathMatch;
    //自定义跨域规则是否已加载
    private boolean enableCors;
    //忽略URL前缀的控制器类
    private static String[] ignoreUrlPrefixController = new String[]{"springfox.documentation.swagger.web.ApiResourceController",
            "org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController"};

    public EmilyWebAutoConfiguration(WebProperties webProperties) {
        this.webProperties = webProperties;
    }

    /**
     * 配置路由规则
     *
     * @param configurer
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        AntPathMatcher matcher = new AntPathMatcher();
        //区分大小写,默认true
        matcher.setCaseSensitive(webProperties.getPath().isCaseSensitive());
        //是否去除前后空格,默认false
        matcher.setTrimTokens(webProperties.getPath().isTrimTokens());
        //分隔符
        matcher.setPathSeparator(CharacterUtils.PATH_SEPARATOR);
        //是否缓存匹配规则,默认null等于true
        matcher.setCachePatterns(webProperties.getPath().isCachePatterns());
        //设置路由匹配规则
        configurer.setPathMatcher(matcher);
        //设置URL末尾是否支持斜杠，默认true,如/a/b/有效，/a/b也有效
        configurer.setUseTrailingSlashMatch(webProperties.getPath().isUseTrailingSlashMatch());
        //忽略URL前缀的控制器类
        ignoreUrlPrefixController = ArrayUtils.addAll(ignoreUrlPrefixController, webProperties.getPath().getExclude().toArray(new String[]{}));
        //给所有的接口统一添加前缀
        configurer.addPathPrefix(webProperties.getPath().getPrefix(), c -> {
            /**
             * 1.注解@ApiPrefix优先级最高
             * 2.isEnableAllPrefix方法优先级第二
             */
            if (c.isAnnotationPresent(ApiPrefix.class)) {
                if (BooleanUtils.isFalse(c.getAnnotation(ApiPrefix.class).ignore())) {
                    return true;
                } else {
                    return false;
                }
            }
            if (!ArrayUtils.contains(ignoreUrlPrefixController, c.getName()) && (BooleanUtils.isTrue(webProperties.getPath().isEnableAllPrefix()))) {
                return true;
            } else {
                return false;
            }
        });
        enablePathMatch = true;

    }

    /**
     * 跨域设置
     * 在浏览器console控制台测试ajax示例
     $.ajax({
     url:"http://172.30.67.122:9000/api/void/test1",//发送的路径
     type:"POST",//发送的方式
     async:false,
     data:JSON.stringify({'name':'test','age':23}),//发送的数据
     contentType: "application/json", //提交数据类型
     dataType:"json",//服务器返回的数据类型
     success: function(data) {
     console.log(data)
     },
     error: function (data){
     alert("提交失败");
     }
     });
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (BooleanUtils.isFalse(webProperties.getCors().isEnable())) {
            return;
        }
        //启用跨域匹配的路径，默认所有请求，示例：/admin或/admin/**
        CorsRegistration registration = registry.addMapping("/**");
        //允许来自所有域名请求
        if (!webProperties.getCors().getAllowedOrigins().isEmpty()) {
            registration.allowedOrigins(webProperties.getCors().getAllowedOrigins().toArray(new String[]{}));
        } else {
            registration.allowedOrigins("*");
        }
        //设置所允许的HTTP请求方法，*号代表允许所有方法
        if (!webProperties.getCors().getAllowedMethods().isEmpty()) {
            registration.allowedMethods(webProperties.getCors().getAllowedMethods().toArray(new String[]{}));
        } else {
            registration.allowedMethods("OPTIONS", "GET", "PUT", "POST");
        }
        //服务器支持的所有头信息字段，多个字段用逗号分隔；默认支持所有，*号代表所有
        if (!webProperties.getCors().getAllowedHeaders().isEmpty()) {
            registration.allowedHeaders(webProperties.getCors().getAllowedHeaders().toArray(new String[]{}));
        } else {
            registration.allowedHeaders("Origin", "X-Requested-With", "Content-Type", "Accept");
        }
        //浏览器是否应该发送凭据，如是否允许发送Cookie，true为允许
        if (BooleanUtils.isFalse(webProperties.getCors().isAllowCredentials())) {
            registration.allowCredentials(false);
        } else {
            registration.allowCredentials(true);
        }
        //设置响应HEAD,默认无任何设置，不可以使用*号
        if (!webProperties.getCors().getExposedHeaders().isEmpty()) {
            registration.exposedHeaders(webProperties.getCors().getExposedHeaders().toArray(new String[]{}));
        }
        //设置多长时间内不需要发送预检验请求，可以缓存该结果，默认1800秒
        if (Objects.nonNull(webProperties.getCors().getMaxAge())) {
            registration.maxAge(webProperties.getCors().getMaxAge());
        }
        enableCors = true;

    }

    @Override
    public void destroy() throws Exception {
            LoggerUtils.info(EmilyWebAutoConfiguration.class, "【销毁--自动化配置】----API前缀组件【EmilyWebAutoConfiguration】");
            LoggerUtils.info(EmilyWebAutoConfiguration.class, "【销毁--自动化配置】----跨域组件【EmilyWebAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (enablePathMatch) {
            LoggerUtils.info(EmilyWebAutoConfiguration.class, "【初始化--自动化配置】----API前缀组件【EmilyWebAutoConfiguration】");
        }
        if (enableCors) {
            LoggerUtils.info(EmilyWebAutoConfiguration.class, "【初始化--自动化配置】----跨域组件【EmilyWebAutoConfiguration】");
        }
    }
}
