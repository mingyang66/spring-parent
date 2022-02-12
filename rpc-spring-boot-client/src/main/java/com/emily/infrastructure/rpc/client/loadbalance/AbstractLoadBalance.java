package com.emily.infrastructure.rpc.client.loadbalance;

import java.util.List;

/**
 * @program: spring-parent
 * @description: 负载均衡策略接口
 * @author: Emily
 * @create: 2021/11/01
 */
public abstract class AbstractLoadBalance implements LoadBalance {
    @Override
    public String selectServiceAddress(List<String> serviceAddress) {
        if (serviceAddress == null || serviceAddress.size() == 0) {
            return null;
        }
        if (serviceAddress.size() == 1) {
            return serviceAddress.get(0);
        }
        return doSelect(serviceAddress);
    }

    /**
     * 选择合适的服务地址
     *
     * @param serviceAddress
     * @return
     */
    protected abstract String doSelect(List<String> serviceAddress);
}
