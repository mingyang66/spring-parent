package com.sgrain.boot.swagger.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @program: spring-parent
 * @description: Swagger配置文件
 * @create: 2020/07/09
 */
@ConfigurationProperties(prefix = "spring.sgrain.swagger")
public class SwaggerProperties {
    /**
     * 是否启用swagger
     */
    private boolean enable;
    /**
     * 分组，使用英文单词，逗号隔开；如：group1,group2,group3
     */
    private String group;
    /**
     * 分组名称，使用逗号隔开,跟group一一对应；如：groupName1,groupName2,groupName3
     */
    private String groupName;
    /**
     * 扫描包，使用逗号隔开；如：com.sgrain.boot,com.sgrain.test
     */
    private String basePackage;
    /**
     * API描述信息
     */
    private ApiInfo apiInfo;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public ApiInfo getApiInfo() {
        return apiInfo;
    }

    public void setApiInfo(ApiInfo apiInfo) {
        this.apiInfo = apiInfo;
    }

    public static class ApiInfo{
        /**
         * 标题
         */
        private String title;
        /**
         * 描述
         */
        private String description;
        /**
         * 版本号
         */
        private String version;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
