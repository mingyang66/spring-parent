package com.emily.framework.datasource.po;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/04/22
 */
public class SqlServer {

    private Integer bankCode;
    private Integer orgId;

    public Integer getBankCode() {
        return bankCode;
    }

    public void setBankCode(Integer bankCode) {
        this.bankCode = bankCode;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }
}
