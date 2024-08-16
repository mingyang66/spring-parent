package com.emily.infrastructure.web.servlet;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

/**
 * Web配置文件
 *
 * @author Emily
 * @since 2020/05/28
 */
@ConfigurationProperties(prefix = ServletProperties.PREFIX)
public class ServletProperties {
    /**
     * 属性配置前缀
     */
    public static final String PREFIX = "spring.emily.servlet";
    /**
     * WebMvc组件是否启用，默认：true
     */
    private boolean enabled = true;
    /**
     * API路由配置属性
     */
    private Path path = new Path();
    /**
     * 跨域配置
     */
    private CorsRegister cors = new CorsRegister();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public CorsRegister getCors() {
        return cors;
    }

    public void setCors(CorsRegister cors) {
        this.cors = cors;
    }

    public static class Path {
        /**
         * 是否开启所有接口的前缀
         */
        private boolean enabled = false;
        //区分大小写,默认true
        private boolean caseSensitive = true;
        //是否去除前后空格,默认false
        private boolean trimTokens = false;
        //是否缓存匹配规则,默认null等于true
        private boolean cachePatterns = true;
        //设置URL末尾是否支持斜杠，默认true,如/a/b/有效，/a/b也有效
        private boolean useTrailingSlashMatch = true;
        //URL默认添加前缀
        private String prefix = "api";
        //忽略指定的路由
        private Set<String> exclude = new HashSet<>();

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isCaseSensitive() {
            return caseSensitive;
        }

        public void setCaseSensitive(boolean caseSensitive) {
            this.caseSensitive = caseSensitive;
        }

        public boolean isTrimTokens() {
            return trimTokens;
        }

        public void setTrimTokens(boolean trimTokens) {
            this.trimTokens = trimTokens;
        }

        public boolean isCachePatterns() {
            return cachePatterns;
        }

        public void setCachePatterns(boolean cachePatterns) {
            this.cachePatterns = cachePatterns;
        }

        public boolean isUseTrailingSlashMatch() {
            return useTrailingSlashMatch;
        }

        public void setUseTrailingSlashMatch(boolean useTrailingSlashMatch) {
            this.useTrailingSlashMatch = useTrailingSlashMatch;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public Set<String> getExclude() {
            return exclude;
        }

        public void setExclude(Set<String> exclude) {
            this.exclude = exclude;
        }
    }

    /**
     * 跨域注册配置
     */
    public static class CorsRegister {
        //开启跨域设置，默认false
        private boolean enabled = false;
        private Set<String> allowedOrigins = new HashSet<>();
        //允许HTTP请求方法
        private Set<String> allowedMethods = new HashSet<>();
        private Set<String> allowedHeaders = new HashSet<>();
        private boolean allowCredentials = true;
        private Set<String> exposedHeaders = new HashSet<>();
        private long maxAge = 18000L;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public Set<String> getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(Set<String> allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }

        public Set<String> getAllowedMethods() {
            return allowedMethods;
        }

        public void setAllowedMethods(Set<String> allowedMethods) {
            this.allowedMethods = allowedMethods;
        }

        public Set<String> getAllowedHeaders() {
            return allowedHeaders;
        }

        public void setAllowedHeaders(Set<String> allowedHeaders) {
            this.allowedHeaders = allowedHeaders;
        }

        public boolean isAllowCredentials() {
            return allowCredentials;
        }

        public void setAllowCredentials(boolean allowCredentials) {
            this.allowCredentials = allowCredentials;
        }

        public Set<String> getExposedHeaders() {
            return exposedHeaders;
        }

        public void setExposedHeaders(Set<String> exposedHeaders) {
            this.exposedHeaders = exposedHeaders;
        }

        public long getMaxAge() {
            return maxAge;
        }

        public void setMaxAge(long maxAge) {
            this.maxAge = maxAge;
        }
    }
}
