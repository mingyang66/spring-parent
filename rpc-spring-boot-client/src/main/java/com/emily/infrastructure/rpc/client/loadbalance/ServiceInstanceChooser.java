package com.emily.infrastructure.rpc.client.loadbalance;

import com.emily.infrastructure.rpc.client.pool.IRpcConnection;

import java.util.List;

/**
 * 选取可用负载均衡器
 */
@Deprecated
public interface ServiceInstanceChooser {
    /**
     * 选择可用服务连接对象
     * @param serviceAddress 服务器地址
     * @return
     */
    IRpcConnection choose(List<String> serviceAddress);
}
