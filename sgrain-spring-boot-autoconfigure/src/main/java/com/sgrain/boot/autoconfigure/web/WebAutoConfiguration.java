package com.sgrain.boot.autoconfigure.web;

import com.sgrain.boot.common.utils.CharacterUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @program: spring-parent
 * @description: webmvc自动化配置
 * @author: 姚明洋
 * @create: 2020/05/26
 */
@Configuration
public class WebAutoConfiguration implements WebMvcConfigurer {
    /**
     * 配置路由规则
     * @param configurer
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        AntPathMatcher matcher = new AntPathMatcher();
        //区分大小写,默认true
        matcher.setCaseSensitive(true);
        //是否去除前后空格,默认false
        matcher.setTrimTokens(true);
        //分隔符
        matcher.setPathSeparator(CharacterUtils.PATH_SEPARATOR);
        //是否缓存匹配规则,默认null等于true
        matcher.setCachePatterns(true);
        //设置路由匹配规则
        configurer.setPathMatcher(matcher);
        //设置URL末尾是否支持斜杠，默认true,如/a/b/有效，/a/b也有效
        configurer.setUseTrailingSlashMatch(true);
        //给所有的接口统一添加前缀
        configurer.addPathPrefix("api", c->c.isAnnotationPresent(RestController.class));
    }

}
