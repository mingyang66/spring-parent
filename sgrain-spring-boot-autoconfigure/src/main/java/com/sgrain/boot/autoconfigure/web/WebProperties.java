package com.sgrain.boot.autoconfigure.web;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @program: spring-parent
 * @description: Web配置文件
 * @author: 姚明洋
 * @create: 2020/05/28
 */
@ConfigurationProperties(prefix = "spring.sgrain.web")
public class WebProperties {
    private Path path = new Path();

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public static class Path {
        /**
         * 是否开启所有接口的前缀
         */
        private boolean enableAllPrefix = false;
        //区分大小写
        private boolean caseSensitive = true;
        //是否去除前后空格
        private boolean trimTokens = false;
        //是否缓存匹配规则,默认null等于true
        private boolean cachePatterns = true;
        //设置URL末尾是否支持斜杠，默认true,如/a/b/有效，/a/b也有效
        private boolean useTrailingSlashMatch = true;
        //URL默认添加前缀
        private String prefix = "api";

        public boolean isEnableAllPrefix() {
            return enableAllPrefix;
        }

        public void setEnableAllPrefix(boolean enableAllPrefix) {
            this.enableAllPrefix = enableAllPrefix;
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
    }
}
