package com.emily.infrastructure.rpc.client.loadbalance;

import org.apache.commons.lang3.RandomUtils;

import java.util.List;

/**
 * @program: spring-parent
 * @description: 随机获取可用负载地址
 * @author: Emily
 * @create: 2021/11/01
 */
public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(List<String> serviceAddress) {
        return serviceAddress.get(RandomUtils.nextInt(0, serviceAddress.size()));
    }
}
