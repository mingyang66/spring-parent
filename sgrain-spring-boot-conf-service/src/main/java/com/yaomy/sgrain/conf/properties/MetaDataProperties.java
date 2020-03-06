package com.yaomy.sgrain.conf.properties;

import com.yaomy.sgrain.common.control.test.School;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Map;

/**
 * 配置元数据文件.
 */
@SuppressWarnings("all")
@ConfigurationProperties(prefix = "spring.yaomy", ignoreInvalidFields = true)
public class MetaDataProperties {

    private HttpClient httpClient = new HttpClient();

    private Test test = new Test();

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public Test getTest() {
        return test;
    }

    /**
     * HttpClient网络请求属性配置类
     */
    public static class HttpClient{
        /**
         * HttpClientService read timeout (in milliseconds),default:5000
         */
        private Integer readTimeOut = 5000;
        /**
         * HttpClientService connect timeout (in milliseconds),default:10000
         */
        private Integer connectTimeOut = 10000;

        public Integer getReadTimeOut() {
            return readTimeOut;
        }

        public void setReadTimeOut(Integer readTimeOut) {
            this.readTimeOut = readTimeOut;
        }

        public Integer getConnectTimeOut() {
            return connectTimeOut;
        }

        public void setConnectTimeOut(Integer connectTimeOut) {
            this.connectTimeOut = connectTimeOut;
        }
    }

    /**
     * test metadata
     */
    public static class Test{
        /**
         * context
         */
        private Map<String, String> context;
        /**
         * test tip
         */
        private String testTip;
        /**
         * test type
         */
        private Type testType = Type.ADD;
        /**
         * school
         */
        @NestedConfigurationProperty
        private School school;

        /**
         * enable or disable default false
         */
        private Boolean testEnable = Boolean.FALSE;
        /**
         * deprecated test
         */
        private String deprecated;

        public Map<String, String> getContext() {
            return context;
        }

        public void setContext(Map<String, String> context) {
            this.context = context;
        }

        public String getTestTip() {
            return testTip;
        }

        public void setTestTip(String testTip) {
            this.testTip = testTip;
        }

        public Type getTestType() {
            return testType;
        }

        public void setTestType(Type testType) {
            this.testType = testType;
        }

        public School getSchool() {
            return school;
        }

        public void setSchool(School school) {
            this.school = school;
        }

        public Boolean getTestEnable() {
            return testEnable;
        }

        public void setTestEnable(Boolean testEnable) {
            this.testEnable = testEnable;
        }
        @DeprecatedConfigurationProperty(replacement = "app.acme.name", reason = "not a userful property")
        @Deprecated
        public String getDeprecated() {
            return deprecated;
        }
        @Deprecated
        public void setDeprecated(String deprecated) {
            this.deprecated = deprecated;
        }
    }
    public enum  Type{
        CREATE,UPDATE,DEL,ADD
    }
}
