package com.emily.infrastructure.rpc.common.entity;


/**
 * @program: spring-parent
 * @description: 定义RPC调用消息传递类
 * @author: Emily
 * @create: 2021/09/17
 */
public class ClassInfo {
    /**
     * RPC协议
     */
    public static final String PROTOCOL = "#rpc#";
    /**
     * 类名
     * 自定义name，一般一个接口有多个实现类的时候使用自定义或者默认使用接口名称
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
    /**
     * 自定义rpc协议
     */
    private String protocol = PROTOCOL;

    public ClassInfo(){}
    public ClassInfo(String className, String methodName, Class<?>[] types, Object[] params) {
        this.className = className;
        this.methodName = methodName;
        this.types = types;
        this.params = params;
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

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
