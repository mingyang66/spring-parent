package com.emily.infrastructure.rpc.core.message;


import com.emily.infrastructure.core.trace.context.TraceContextHolder;

import java.io.Serializable;

/**
 * @program: spring-parent
 * @description: 自定义RPC传输协议
 * @author: Emily
 * @create: 2021/09/17
 */
public class IRpcRequest implements Serializable {

    /**
     * 事务唯一标识, 36位
     */
    private String traceId;
    /**
     * 类名
     */
    private String className;
    /**
     * 函数名称
     */
    private String methodName;
    /**
     * 参数类型
     */
    private Class<?>[] types;
    /**
     * 参数列表
     */
    private Object[] params;


    public IRpcRequest(){}

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getTypes() {
        return types;
    }

    public void setTypes(Class<?>[] types) {
        this.types = types;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public static IRpcRequest build(String className, String methodName, Class<?>[] types, Object[] params){
        IRpcRequest request = new IRpcRequest();
        request.setTraceId(TraceContextHolder.get().getTraceId());
        request.setClassName(className);
        request.setMethodName(methodName);
        request.setTypes(types);
        request.setParams(params);
        return request;
    }

}
