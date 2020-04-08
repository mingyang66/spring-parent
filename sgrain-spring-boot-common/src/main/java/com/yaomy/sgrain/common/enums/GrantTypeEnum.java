package com.yaomy.sgrain.common.enums;

/**
 * @Description: 认证模式枚举类
 * @ProjectName: com.uufund.auth.master
 * @Version: 1.0
 */
public enum GrantTypeEnum {
    PASSWORD("password", "密码模式"),
    REFRESH_TOKEN("refresh_token", "刷新token");

    private final String grant_type;
    private final String grant_name;

    private GrantTypeEnum(String grant_type, String grant_name){
        this.grant_type = grant_type;
        this.grant_name = grant_name;
    }

    public String getGrant_type() {
        return grant_type;
    }

}
