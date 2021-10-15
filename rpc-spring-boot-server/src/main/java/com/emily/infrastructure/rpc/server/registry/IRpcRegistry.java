package com.emily.infrastructure.rpc.server.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: spring-parent
 * @description: RPC服务注册中心
 * @author: Emily
 * @create: 2021/09/18
 */
public class IRpcRegistry {
    /**
     * rpc服务注册表，会将@RpcService注解标注的bean注册为RPC服务
     */
    private static final Map<String, Object> rpcServiceRegistry = new ConcurrentHashMap<>();

    /**
     * 将@RpcService注解标注的服务存入注册表
     * @param interfaceName
     * @param bean
     */
    public void registerServiceBean(String interfaceName, Object bean){
        rpcServiceRegistry.put(interfaceName, bean);
    }

    /**
     * 获取注册表中指定接口对应的bean名称
     * @param interfaceName
     */
    public Object getServiceBean(String interfaceName){
        return rpcServiceRegistry.get(interfaceName);
    }
}
