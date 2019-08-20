package com.yaomy.security.jwt.po;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.po.AjaxResponseBody
 * @Version: 1.0
 */
public class ResponseBody {
    private String status;
    private String msg;
    private Object result;
    private String token;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
