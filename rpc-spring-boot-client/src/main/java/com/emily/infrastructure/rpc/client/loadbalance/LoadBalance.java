package com.emily.infrastructure.rpc.client.loadbalance;

import java.util.List;

/**
 * @Description: 负载均衡策略接口
 * @Author: Emily
 * @create: 2021/11/1
 */
public interface LoadBalance {
    /**
     * 选择一个列表中存在的地址
     *
     * @param serviceAddress 服务地址列表
     * @return 地址host
     */
    String selectServiceAddress(List<String> serviceAddress);
}
