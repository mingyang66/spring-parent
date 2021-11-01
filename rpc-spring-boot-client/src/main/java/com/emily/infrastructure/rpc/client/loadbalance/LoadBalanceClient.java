package com.emily.infrastructure.rpc.client.loadbalance;

import com.emily.infrastructure.rpc.client.IRpcClientProperties;
import com.emily.infrastructure.rpc.client.pool.IRpcConnection;

import java.util.List;

/**
 * @program: spring-parent
 * @description: 负载均衡实现类
 * @author: Emily
 * @create: 2021/11/01
 */
@Deprecated
public class LoadBalanceClient implements ServiceInstanceChooser {
    private LoadBalance loadBalance;
    private IRpcClientProperties properties;

    public LoadBalanceClient(LoadBalance loadBalance, IRpcClientProperties properties) {
        this.loadBalance = loadBalance;
        this.properties = properties;
    }

    @Override
    public IRpcConnection choose(List<String> serviceAddress) {
        //获取服务器地址
        String address = loadBalance.selectServiceAddress(serviceAddress);
        IRpcConnection connection = new IRpcConnection(properties);
        //建立Rpc连接
        connection.connect(address, properties.getPort());
        return connection;
    }
}
