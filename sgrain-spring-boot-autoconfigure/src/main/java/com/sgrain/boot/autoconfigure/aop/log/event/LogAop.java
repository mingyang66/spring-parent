package com.sgrain.boot.autoconfigure.aop.log.event;

import org.aopalliance.intercept.MethodInvocation;

import javax.servlet.http.HttpServletRequest;

/**
 * @program: spring-parent
 * @description: 日志事件对象
 * @create: 2020/08/07
 */
public class LogAop {
    //日志级别
    private String logLevel;
    private MethodInvocation invocation;
    //耗时
    private long spendTime;
    //返回结果
    private Object result;
    //请求servlet
    private HttpServletRequest request;
    //异常
    private Throwable throwable;

    public LogAop(MethodInvocation invocation, HttpServletRequest request){
        this.invocation = invocation;
        this.request = request;
    }
    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public MethodInvocation getInvocation() {
        return invocation;
    }

    public void setInvocation(MethodInvocation invocation) {
        this.invocation = invocation;
    }

    public long getSpendTime() {
        return spendTime;
    }

    public void setSpendTime(long spendTime) {
        this.spendTime = spendTime;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
}
